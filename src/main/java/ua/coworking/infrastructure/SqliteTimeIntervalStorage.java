package ua.coworking.infrastructure;
import com.google.inject.Inject;
import ua.coworking.model.*;
import java.sql.*;
import java.util.*;
/** Єдине місце SQL-запитів. Домен працює через TimeIntervalStorage. */
public final class SqliteTimeIntervalStorage implements TimeIntervalStorage {
    private final Connection connection;
    @Inject public SqliteTimeIntervalStorage(Connection connection) {
        this.connection=connection;
        try (Statement s=connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS time_intervals (id TEXT PRIMARY KEY, resident TEXT NOT NULL, space TEXT NOT NULL, start TEXT NOT NULL, end TEXT NOT NULL)");
        } catch (SQLException ex) { throw new IllegalStateException("Не вдалося підготувати сховище.",ex); }
    }
    @Override public synchronized void save(TimeInterval t) {
        try (PreparedStatement p=connection.prepareStatement("INSERT INTO time_intervals(id,resident,space,start,end) VALUES(?,?,?,?,?)")) {
            p.setString(1,t.id());p.setString(2,t.resident());p.setString(3,t.space());p.setString(4,t.start());p.setString(5,t.end());p.executeUpdate();
        } catch (SQLException ex) { throw new IllegalStateException("Не вдалося зберегти інтервал.",ex); }
    }
    @Override public synchronized List<TimeInterval> findAll() {
        List<TimeInterval> result=new ArrayList<>();
        try (Statement s=connection.createStatement();ResultSet r=s.executeQuery("SELECT id,resident,space,start,end FROM time_intervals ORDER BY start,id")) {
            while(r.next()) result.add(new TimeInterval(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5)));
        } catch (SQLException ex) { throw new IllegalStateException("Не вдалося прочитати інтервали.",ex); }
        return List.copyOf(result);
    }
}
