package ua.coworking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.coworking.model.*;
import ua.coworking.infrastructure.*;
import com.google.inject.*;
import java.nio.file.Path;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    @TempDir Path temp;
    private TimeInterval t(String id,String space,String start,String end) {
        return new TimeInterval(id,"Олена",space,"2026-12-01T"+start,"2026-12-01T"+end);
    }
    @Test void boundariesAndConflict() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));
            assertTrue(b.add(t("A","Зал","09:00","10:00")).startsWith("Інтервал додано"));
            assertTrue(b.add(t("B","зал","09:30","10:30")).contains("зайняте"));
            assertTrue(b.add(t("C","Зал","10:00","11:00")).startsWith("Інтервал додано"));
            assertTrue(b.add(t("D","Інший зал","09:00","10:00")).startsWith("Інтервал додано"));
            assertEquals(3,b.getIntervals().size());
        }
    }
    @Test void invalidAndDuplicateDoNotChangeStorage() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            TimeIntervalStorage s=new SqliteTimeIntervalStorage(c);Booking b=new Booking(s);
            assertTrue(b.add(t("A","Зал","10:00","09:00")).startsWith("Відмова"));
            assertTrue(b.add(new TimeInterval("","Олена","Зал","bad","bad")).startsWith("Відмова"));
            b.add(t("A","Зал","09:00","10:00"));
            assertTrue(b.add(t("A","Інше місце","11:00","12:00")).contains("уже існує"));
            assertEquals(1,s.findAll().size());
            assertThrows(UnsupportedOperationException.class,()->b.getIntervals().clear());
        }
    }
    @Test void stateAndInvoice() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));
            Accountant a=new Accountant("A","Марія");
            assertTrue(a.invoice(b).startsWith("Відмова"));
            assertTrue(b.confirm(new ConsoleAccessControl()).startsWith("Відмова"));
            b.add(t("A","Зал","09:00","11:00"));
            assertTrue(b.confirm(new ConsoleAccessControl()).contains("підтверджено"));
            assertTrue(a.invoice(b).contains("300.00"));
            assertTrue(b.add(t("B","Зал","11:00","12:00")).startsWith("Відмова"));
            b.cancel();assertEquals(BookingState.CANCELLED,b.getState());
            assertTrue(b.confirm(new ConsoleAccessControl()).startsWith("Відмова"));
        }
    }
    @Test void persistsAcrossConnections() throws Exception {
        String url="jdbc:sqlite:"+temp.resolve("persist.db");
        try(Connection c=DriverManager.getConnection(url)) {new Booking(new SqliteTimeIntervalStorage(c)).add(t("PERSIST","Зал","09:00","10:00"));}
        try(Connection c=DriverManager.getConnection(url)) {
            Booking restored=new Booking(new SqliteTimeIntervalStorage(c));
            assertEquals("PERSIST",restored.getIntervals().getFirst().id());
        }
    }
    @Test void guiceCreatesDistinctDomainObjects() throws Exception {
        System.setProperty("coworking.db","jdbc:sqlite:"+temp.resolve("guice.db"));
        try {
            Injector i=Guice.createInjector(new CoworkingModule());
            assertNotSame(i.getInstance(Booking.class),i.getInstance(Booking.class));
            assertSame(i.getInstance(Connection.class),i.getInstance(Connection.class));
            i.getInstance(Connection.class).close();
        } finally {System.clearProperty("coworking.db");}
    }
}
