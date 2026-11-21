package ua.coworking;
import org.junit.jupiter.api.Test;
import ua.coworking.model.*;
import ua.coworking.infrastructure.SqliteTimeIntervalStorage;
import ua.coworking.controller.CoworkingController;
import ua.coworking.view.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
class MvcTest {
    private CoworkingController controller(Booking b){return new CoworkingController(b,new CoworkingWebView());}
    @Test void crudAndStatusMapping() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));b.setAccessControl(new ConsoleAccessControl());CoworkingController ctl=controller(b);
            assertEquals(201,ctl.create("A","Олена","Зал","2026-12-01T09:00","2026-12-01T10:00").status());
            assertEquals(409,ctl.create("A","Олена","Інший зал","2026-12-01T10:00","2026-12-01T11:00").status());
            assertEquals(200,ctl.update("A","A","Ірина","Зал","2026-12-01T10:00","2026-12-01T11:00").status());
            assertEquals("Ірина",b.read().getFirst().resident());
            assertEquals(400,ctl.update("A","B","Ірина","Зал","2026-12-01T10:00","2026-12-01T11:00").status());
            assertEquals(200,ctl.delete("A").status());assertTrue(b.read().isEmpty());
            assertEquals(404,ctl.delete("A").status());assertEquals(200,ctl.read().status());
        }
    }
    @Test void rejectsInvalidWithoutClientValidation() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));b.setAccessControl(new ConsoleAccessControl());CoworkingController ctl=controller(b);
            assertEquals(400,ctl.create("A","","Зал","2026-12-01T09:00","2026-12-01T10:00").status());
            assertEquals(400,ctl.create("A","Олена","Зал","bad","2026-12-01T10:00").status());
            assertEquals(400,ctl.create("A","Олена","Зал","2026-12-01T10:00","2026-12-01T09:00").status());
            assertTrue(b.read().isEmpty());
        }
    }
    @Test void updateCannotOverlapAndReloadsSourceOfTruth() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            TimeIntervalStorage storage=new SqliteTimeIntervalStorage(c);Booking b=new Booking(storage);b.setAccessControl(new ConsoleAccessControl());CoworkingController ctl=controller(b);
            ctl.create("A","Олена","Зал","2026-12-01T09:00","2026-12-01T10:00");
            ctl.create("B","Олена","Зал","2026-12-01T10:00","2026-12-01T11:00");
            assertEquals(409,ctl.update("B","B","Олена","зал","2026-12-01T09:30","2026-12-01T10:30").status());
            assertEquals("2026-12-01T10:00",b.read().get(1).start());
            storage.save(new TimeInterval("EXTERNAL","Олена","Інше місце","2026-12-02T09:00","2026-12-02T10:00"));
            assertEquals(3,b.read().size());
        }
    }
    @Test void lockedStateAndMissingUpdate() throws Exception {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Booking b=new Booking(new SqliteTimeIntervalStorage(c));b.setAccessControl(new ConsoleAccessControl());CoworkingController ctl=controller(b);
            assertEquals(404,ctl.update("missing","missing","Ірина","Зал","2026-12-01T09:00","2026-12-01T10:00").status());
            ctl.create("A","Олена","Зал","2026-12-01T09:00","2026-12-01T10:00");b.confirm();
            assertEquals(409,ctl.delete("A").status());assertEquals(1,b.read().size());
        }
    }
    @Test void viewMapsAllKinds() {
        CoworkingWebView v=new CoworkingWebView();
        assertEquals(201,v.result(new OperationResult(ResultKind.SUCCESS,"так"),true).status());
        assertEquals(400,v.invalid().status());assertEquals(500,v.error().status());
        assertEquals(404,v.result(new OperationResult(ResultKind.NOT_FOUND,"немає"),false).status());
    }
}
