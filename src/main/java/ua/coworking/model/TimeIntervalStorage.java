package ua.coworking.model;
import java.util.List;
public interface TimeIntervalStorage {
    <T> T inTransaction(java.util.function.Supplier<T> work);
    void save(TimeInterval interval);
    List<TimeInterval> findAll();
}
