package ua.coworking.model;
import java.time.*;
import com.google.inject.Inject;
import java.util.*;
public final class Booking {
    private final String id;
    private BookingState state = BookingState.DRAFT;
    private final List<TimeInterval> intervals = new ArrayList<>();
    private final TimeIntervalStorage storage;
    @Inject public Booking(TimeIntervalStorage storage) {
        this.id="B19";this.storage=Objects.requireNonNull(storage);
        intervals.addAll(storage.findAll());
    }
    public String getId() { return id; }
    public BookingState getState() { return state; }
    public List<TimeInterval> getIntervals() { return List.copyOf(intervals); }
    private String validate(TimeInterval t, String ignoreId) {
        if (t == null || t.id().isBlank() || t.resident().isBlank() || t.space().isBlank() || t.start().isBlank() || t.end().isBlank()) return "Усі поля обов'язкові.";
        if (t.id().length()>80 || t.resident().length()>120 || t.space().length()>120) return "Перевищено допустиму довжину поля.";
        LocalDateTime start, end;
        try { start=LocalDateTime.parse(t.start()); end=LocalDateTime.parse(t.end()); }
        catch (RuntimeException ex) { return "Некоректний формат дати й часу."; }
        if (start.getSecond()!=0 || start.getNano()!=0 || end.getSecond()!=0 || end.getNano()!=0) return "Час задається з точністю до хвилини.";
        if (!start.isBefore(end)) return "Початок має бути раніше за кінець.";
        for (TimeInterval old: intervals) {
            if (old.id().equals(ignoreId)) continue;
            if (old.id().equals(t.id())) return "Ідентифікатор уже існує.";
            if (old.space().equalsIgnoreCase(t.space()) && start.isBefore(LocalDateTime.parse(old.end())) && end.isAfter(LocalDateTime.parse(old.start()))) return "Місце вже зайняте в цей час.";
        }
        return null;
    }
    public synchronized String add(TimeInterval t) {
        if (state != BookingState.DRAFT) return "Відмова: зміни дозволені лише в DRAFT.";
        String error=validate(t,null);
        if (error != null) return "Відмова: "+error;
        storage.save(t); intervals.clear(); intervals.addAll(storage.findAll()); return "Інтервал додано: "+t.id()+" ("+t.space()+").";
    }
    public synchronized String confirm(AccessControl access) {
        if (state != BookingState.DRAFT || intervals.isEmpty()) return "Відмова: підтвердження потребує DRAFT і хоча б одного інтервалу.";
        String message=Objects.requireNonNull(access).grant(id);
        state=BookingState.CONFIRMED;return "Бронювання підтверджено. "+message;
    }
    public synchronized String cancel() {
        if (state==BookingState.CANCELLED) return "Відмова: бронювання вже скасовано.";
        state=BookingState.CANCELLED;return "Бронювання скасовано.";
    }
    public double hours() { return intervals.stream().mapToLong(t->Duration.between(LocalDateTime.parse(t.start()),LocalDateTime.parse(t.end())).toMinutes()).sum()/60.0; }
}
