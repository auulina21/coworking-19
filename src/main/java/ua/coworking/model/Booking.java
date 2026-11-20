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
    private OperationResult result(ResultKind kind,String message) {return new OperationResult(kind,message);}
    private OperationResult check(TimeInterval t,String ignoreId) {
        String error=validate(t,ignoreId);
        if(error==null)return result(ResultKind.SUCCESS,"");
        ResultKind kind=(error.equals("Ідентифікатор уже існує.")||error.equals("Місце вже зайняте в цей час."))?ResultKind.FORBIDDEN:ResultKind.INVALID;
        return result(kind,"Відмова: "+error);
    }
    private OperationResult locked() {return result(ResultKind.FORBIDDEN,"Відмова: зміни дозволені лише в DRAFT.");}
    private OperationResult missing() {return result(ResultKind.NOT_FOUND,"Відмова: інтервал не знайдено.");}
    private OperationResult failed() {return result(ResultKind.ERROR,"Сховище тимчасово недоступне.");}
    public synchronized List<TimeInterval> read() {
        List<TimeInterval> saved=storage.findAll();intervals.clear();intervals.addAll(saved);return List.copyOf(intervals);
    }
    public synchronized OperationResult create(TimeInterval t) {
        if(state!=BookingState.DRAFT)return locked();
        try {
            read();OperationResult checked=check(t,null);
            if(checked.kind()!=ResultKind.SUCCESS)return checked;
            storage.save(t);read();return result(ResultKind.SUCCESS,"Інтервал додано: "+t.id()+" ("+t.space()+").");
        }catch(IllegalStateException ex){return failed();}
    }
    public synchronized OperationResult update(String id,TimeInterval t) {
        if(state!=BookingState.DRAFT)return locked();
        try {
            read();if(intervals.stream().noneMatch(old->old.id().equals(id)))return missing();
            if(t==null||!Objects.equals(id,t.id()))return result(ResultKind.INVALID,"Відмова: ідентифікатор не можна змінювати.");
            OperationResult checked=check(t,id);if(checked.kind()!=ResultKind.SUCCESS)return checked;
            if(!storage.update(t))return missing();read();return result(ResultKind.SUCCESS,"Інтервал змінено.");
        }catch(IllegalStateException ex){return failed();}
    }
    public synchronized OperationResult delete(String id) {
        if(state!=BookingState.DRAFT)return locked();
        try {
            read();if(intervals.stream().noneMatch(old->old.id().equals(id)))return missing();
            if(!storage.delete(id))return missing();read();return result(ResultKind.SUCCESS,"Інтервал видалено.");
        }catch(IllegalStateException ex){return failed();}
    }
    public synchronized String add(TimeInterval t) {return create(t).message();}
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
