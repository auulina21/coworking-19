package ua.coworking.model;
import java.util.Set;
public final class Resident extends CoworkingMember implements TariffPlan {
    private final String currentTariff = "BASIC";
    private String nextTariff = "BASIC";
    public Resident(String id, String name) { super(id, name); }
    @Override public String role() { return "Резидент"; }
    @Override public String tariff() { return currentTariff; }
    public String book(Booking booking, TimeInterval interval) { return booking.add(interval); }
    public String book(Booking booking, String space, String start, String end) {
        return book(booking, new TimeInterval(getId()+"-"+start, getName(), space, start, end));
    }
    public String changeTariff(String next) {
        if (next == null || !Set.of("BASIC", "FLEX").contains(next)) return "Відмова: тариф має бути BASIC або FLEX.";
        nextTariff = next;
        return "Тариф наступного місяця: " + nextTariff + ". Поточний тариф: " + currentTariff + ".";
    }
}
