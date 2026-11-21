package ua.coworking.model;
import java.time.*;
import com.google.inject.Inject;
import java.util.*;
public final class Booking {
    private final String id;
    private AccessControl access;
    private BookingState state = BookingState.DRAFT;
    private final List<TimeInterval> intervals = new ArrayList<>();
    private final TimeIntervalStorage storage;
    @Inject public Booking(TimeIntervalStorage storage) {
        this.id="B19";this.storage=Objects.requireNonNull(storage);
        intervals.addAll(storage.findAll());
    }
    @Inject
    public synchronized void setAccessControl(AccessControl access) { this.access=Objects.requireNonNull(access); }
    public String getId() { return id; }
    public BookingState getState() { return state; }
    public List<TimeInterval> getIntervals() { return List.copyOf(intervals); }
    private String validate(TimeInterval t, String ignoreId) {
        if (t == null) return "Інтервал відсутній.";
        if (t.id().isBlank()) return "Ідентифікатор обов'язковий.";
        if (t.resident().isBlank() || t.space().isBlank() || t.start().isBlank() || t.end().isBlank()) return "Усі поля обов'язкові.";
        LocalDateTime start, end;
        try {
            var minuteFormat=java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(java.time.format.ResolverStyle.STRICT);
            start=LocalDateTime.parse(t.start(),minuteFormat);end=LocalDateTime.parse(t.end(),minuteFormat);
        }
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
    private OperationResult change(java.util.function.Supplier<OperationResult> work) {
        List<TimeInterval> before=List.copyOf(intervals);
        try { return storage.inTransaction(work); }
        catch(IllegalStateException ex) {intervals.clear();intervals.addAll(before);return failed();}
    }
    public synchronized OperationResult create(TimeInterval t) {
        if(state!=BookingState.DRAFT)return locked();
        return change(() -> {
            read();OperationResult checked=check(t,null);
            if(checked.kind()!=ResultKind.SUCCESS)return checked;
            storage.save(t);read();return result(ResultKind.SUCCESS,"Інтервал додано: "+t.id()+" ("+t.space()+").");
        });
    }
    public synchronized OperationResult update(String id,TimeInterval t) {
        if(state!=BookingState.DRAFT)return locked();
        return change(() -> {
            read();if(intervals.stream().noneMatch(old->old.id().equals(id)))return missing();
            if(t==null||!Objects.equals(id,t.id()))return result(ResultKind.INVALID,"Відмова: ідентифікатор не можна змінювати.");
            OperationResult checked=check(t,id);if(checked.kind()!=ResultKind.SUCCESS)return checked;
            if(!storage.update(t))return missing();read();return result(ResultKind.SUCCESS,"Інтервал змінено.");
        });
    }
    public synchronized OperationResult delete(String id) {
        if(state!=BookingState.DRAFT)return locked();
        return change(() -> {
            read();if(intervals.stream().noneMatch(old->old.id().equals(id)))return missing();
            if(!storage.delete(id))return missing();read();return result(ResultKind.SUCCESS,"Інтервал видалено.");
        });
    }
    public synchronized String add(TimeInterval t) {return create(t).message();}
    public synchronized String confirm() {
        if (state == BookingState.CONFIRMED) return "Відмова: бронювання вже підтверджено.";
        if (state == BookingState.CANCELLED) return "Відмова: бронювання скасовано.";
        if (intervals.isEmpty()) return "Відмова: немає інтервалів для підтвердження.";
        if (access == null) return "Відмова: сервіс доступу не налаштовано.";
        String message=access.grant(id);
        state=BookingState.CONFIRMED;return "Бронювання підтверджено. "+message;
    }
    public synchronized String cancel() {
        if (state==BookingState.CANCELLED) return "Відмова: бронювання вже скасовано.";
        state=BookingState.CANCELLED;return "Бронювання скасовано.";
    }
    public double hours() { return intervals.stream().mapToLong(t->Duration.between(LocalDateTime.parse(t.start()),LocalDateTime.parse(t.end())).toMinutes()).sum()/60.0; }
}
