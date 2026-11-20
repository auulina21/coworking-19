package ua.coworking.model;
public final class ConsoleAccessControl implements AccessControl {
    @Override public String grant(String bookingId) {
        return "Імітація: доступ для бронювання " + bookingId + " надано.";
    }
}
