package ua.coworking.model;
import java.util.List;
public interface TimeIntervalStorage {
    void save(TimeInterval interval);
    List<TimeInterval> findAll();
    boolean update(TimeInterval interval);
    boolean delete(String id);
}
