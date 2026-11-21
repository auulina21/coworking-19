package ua.coworking.infrastructure;
import com.google.inject.*;
import ua.coworking.model.TimeIntervalStorage;
import ua.coworking.model.AccessControl;
import ua.coworking.model.ConsoleAccessControl;
import java.sql.*;
import java.nio.file.*;
/** Інфраструктура може бути Singleton, Booking — окремий доменний об'єкт. */
public final class CoworkingModule extends AbstractModule {
    @Override protected void configure() { bind(AccessControl.class).to(ConsoleAccessControl.class); bind(TimeIntervalStorage.class).to(SqliteTimeIntervalStorage.class); bind(WebServer.class).to(JavalinWebServer.class).in(Singleton.class); }
    @Provides @Singleton Connection connection() {
        try { Files.createDirectories(Path.of("target"));return DriverManager.getConnection(System.getProperty("coworking.db","jdbc:sqlite:target/coworking.db")); }
        catch (Exception ex) { throw new IllegalStateException("Не вдалося відкрити базу даних.",ex); }
    }
}
