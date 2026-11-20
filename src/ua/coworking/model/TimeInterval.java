package ua.coworking.model;
/** Незмінна частина бронювання. Бізнес-перевірки виконує Booking. */
public record TimeInterval(String id, String resident, String space, String start, String end) {
    public TimeInterval {
        id = clean(id); resident = clean(resident); space = clean(space);
        start = clean(start); end = clean(end);
    }
    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
