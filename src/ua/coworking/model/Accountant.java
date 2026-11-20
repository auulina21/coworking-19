package ua.coworking.model;
import java.util.Locale;
public final class Accountant extends CoworkingMember implements TariffPlan {
    public Accountant(String id, String name) { super(id, name); }
    @Override public String role() { return "Бухгалтер"; }
    @Override public String tariff() { return "STAFF"; }
    public String invoice(Booking booking) {
        if (booking.getState() != BookingState.CONFIRMED) return "Відмова: рахунок доступний лише після підтвердження.";
        return String.format(Locale.ROOT, "Імітація рахунку %s: %.2f грн (%.2f год × 150 грн).", booking.getId(), booking.hours()*150, booking.hours());
    }
}
