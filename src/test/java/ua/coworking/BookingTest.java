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
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));b.setAccessControl(new ConsoleAccessControl());
            assertTrue(b.add(t("A","Зал","09:00","10:00")).startsWith("Інтервал додано"));
            assertTrue(b.add(t("B","зал","09:30","10:30")).contains("зайняте"));
            assertTrue(b.add(t("C","Зал","10:00","11:00")).startsWith("Інтервал додано"));
            assertTrue(b.add(t("D","Інший зал","09:00","10:00")).startsWith("Інтервал додано"));
            assertEquals(3,b.getIntervals().size());
        }
    }
    @Test void invalidAndDuplicateDoNotChangeStorage() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            TimeIntervalStorage s=new SqliteTimeIntervalStorage(c);Booking b=new Booking(s);b.setAccessControl(new ConsoleAccessControl());
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
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));b.setAccessControl(new ConsoleAccessControl());
            Accountant a=new Accountant("A","Марія");
            assertTrue(a.invoice(b).startsWith("Відмова"));
            assertTrue(b.confirm().startsWith("Відмова"));
            b.add(t("A","Зал","09:00","11:00"));
            assertTrue(b.confirm().contains("підтверджено"));
            assertTrue(a.invoice(b).contains("300.00"));
            assertTrue(b.add(t("B","Зал","11:00","12:00")).startsWith("Відмова"));
            b.cancel();assertEquals(BookingState.CANCELLED,b.getState());
            assertTrue(b.confirm().startsWith("Відмова"));
        }
    }
    @Test void persistsAcrossConnections() throws Exception {
        String url="jdbc:sqlite:"+temp.resolve("persist.db");
        try(Connection c=DriverManager.getConnection(url)) {new Booking(new SqliteTimeIntervalStorage(c)).add(t("PERSIST","Зал","09:00","10:00"));}
        try(Connection c=DriverManager.getConnection(url)) {
            Booking restored=new Booking(new SqliteTimeIntervalStorage(c));restored.setAccessControl(new ConsoleAccessControl());
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

    @Test void setterInjectionControlsAccessExactlyOnce() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));
            java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
            b.setAccessControl(id -> {assertEquals("B19",id);calls.incrementAndGet();return "Тестовий доступ";});
            assertTrue(b.confirm().startsWith("Відмова"));assertEquals(0,calls.get());
            b.add(t("SETTER","Зал","09:00","10:00"));
            assertTrue(b.confirm().contains("Тестовий доступ"));assertEquals(1,calls.get());
            assertEquals(BookingState.CONFIRMED,b.getState());
            assertTrue(b.confirm().startsWith("Відмова"));assertEquals(1,calls.get());
        }
    }

    @Test void autoIdsAllowDifferentSpacesAtSameTime() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));
            Resident r=new Resident("R","Олена");
            assertFalse(r.book(b,"Зал A","2026-12-01T09:00","2026-12-01T10:00").startsWith("Відмова"));
            assertFalse(r.book(b,"Зал B","2026-12-01T09:00","2026-12-01T10:00").startsWith("Відмова"));
            assertEquals(2,b.getIntervals().size());
            assertNotEquals(b.getIntervals().get(0).id(),b.getIntervals().get(1).id());
        }
    }
    @Test void layoutAndTariffScenarios() {
        SpaceManager manager=new SpaceManager("M","Менеджер");
        assertFalse(manager.manageLayout("Перший план").startsWith("Відмова"));
        assertFalse(manager.manageLayout("Інший план").startsWith("Відмова"));
        assertTrue(manager.manageLayout("").startsWith("Відмова"));
        Resident r=new Resident("R","Олена");
        assertTrue(r.changeTariff("FLEX").contains("FLEX"));
        assertEquals("BASIC",r.tariff());
        assertTrue(r.changeTariff("UNKNOWN").startsWith("Відмова"));
        assertEquals("BASIC",r.tariff());
    }
    private static final class FailingReloadStorage implements TimeIntervalStorage {
        final SqliteTimeIntervalStorage delegate;
        boolean fail;
        FailingReloadStorage(SqliteTimeIntervalStorage delegate){this.delegate=delegate;}
        public void save(TimeInterval t){delegate.save(t);fail=true;}
        public java.util.List<TimeInterval> findAll(){if(fail){fail=false;throw new IllegalStateException("Тестова помилка читання");}return delegate.findAll();}
        public <T> T inTransaction(java.util.function.Supplier<T> work){return delegate.inTransaction(work);}
    }
    @Test void failedReloadRollsBackInsertAndPreservesCollection() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            SqliteTimeIntervalStorage real=new SqliteTimeIntervalStorage(c);
            Booking b=new Booking(new FailingReloadStorage(real));
            assertThrows(IllegalStateException.class,()->b.add(t("ROLLBACK","Зал","09:00","10:00")));
            assertTrue(real.findAll().isEmpty());assertTrue(b.getIntervals().isEmpty());assertTrue(c.getAutoCommit());
        }
    }

    @Test void acceptsOnlyMinutePrecisionText() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));
            assertTrue(b.add(t("SECONDS0","Зал","09:00:00","10:00")).startsWith("Відмова"));
            assertTrue(b.add(t("FRACTION0","Зал","09:00:00.000","10:00")).startsWith("Відмова"));
            assertTrue(b.getIntervals().isEmpty());
            assertFalse(b.add(t("MINUTE","Зал","09:00","10:00")).startsWith("Відмова"));
        }
    }
    private Connection faultyConnection(Connection real,boolean failRestore,boolean failRollback,java.util.concurrent.atomic.AtomicInteger restores) {
        var committed=new java.util.concurrent.atomic.AtomicBoolean();
        return (Connection)java.lang.reflect.Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class<?>[]{Connection.class},(proxy,method,args)->{
            if(method.getName().equals("setAutoCommit") && Boolean.TRUE.equals(args[0])) {
                restores.incrementAndGet();
                if(failRestore && committed.get())throw new SQLException("Тестовий збій відновлення autocommit");
            }
            if(method.getName().equals("rollback") && failRollback)throw new SQLException("Тестовий збій rollback");
            try {
                Object value=method.invoke(real,args);
                if(method.getName().equals("commit"))committed.set(true);
                return value;
            } catch(java.lang.reflect.InvocationTargetException ex){throw ex.getCause();}
        });
    }
    @Test void restoreFailureAfterCommitKeepsSuccessfulOutcome() throws Exception {
        String url="jdbc:sqlite:"+temp.resolve("cleanup-commit.db");
        var restores=new java.util.concurrent.atomic.AtomicInteger();
        try(Connection real=DriverManager.getConnection(url)) {
            Connection proxy=faultyConnection(real,true,false,restores);
            Booking b=new Booking(new SqliteTimeIntervalStorage(proxy));
            assertFalse(b.add(t("COMMITTED","Зал","09:00","10:00")).startsWith("Відмова"));
            assertEquals("COMMITTED",b.getIntervals().getFirst().id());
            assertTrue(real.isClosed());assertEquals(1,restores.get());
        }
        try(Connection reopened=DriverManager.getConnection(url)) {
            assertEquals("COMMITTED",new SqliteTimeIntervalStorage(reopened).findAll().getFirst().id());
        }
    }
    @Test void failedRollbackClosesWithoutImplicitCommit() throws Exception {
        String url="jdbc:sqlite:"+temp.resolve("cleanup-rollback.db");
        var restores=new java.util.concurrent.atomic.AtomicInteger();
        try(Connection real=DriverManager.getConnection(url)) {
            Connection proxy=faultyConnection(real,false,true,restores);
            Booking b=new Booking(new FailingReloadStorage(new SqliteTimeIntervalStorage(proxy)));
            assertThrows(IllegalStateException.class,()->b.add(t("ROLLBACKFAIL","Зал","09:00","10:00")));
            assertTrue(real.isClosed());assertEquals(0,restores.get());assertTrue(b.getIntervals().isEmpty());
        }
        try(Connection reopened=DriverManager.getConnection(url)) {
            assertTrue(new SqliteTimeIntervalStorage(reopened).findAll().isEmpty());
        }
    }

    @Test void invalidValuesPreserveDatabaseAndCollection() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            SqliteTimeIntervalStorage storage=new SqliteTimeIntervalStorage(c);Booking b=new Booking(storage);
            b.add(t("VALID","Зал","09:00","10:00"));
            java.util.List<TimeInterval> before=storage.findAll();
            for(TimeInterval invalid:java.util.List.of(
                new TimeInterval("CALENDAR","Олена","Інший зал","2026-02-30T09:00","2026-02-30T10:00"),
                new TimeInterval("EMPTYNAME","","Інший зал","2026-12-01T11:00","2026-12-01T12:00"),
                t("EQUAL","Інший зал","11:00","11:00"))) {
                assertTrue(b.add(invalid).startsWith("Відмова"));
                assertEquals(before,b.getIntervals());assertEquals(before,storage.findAll());
                assertEquals(BookingState.DRAFT,b.getState());
            }
        }
    }
}
