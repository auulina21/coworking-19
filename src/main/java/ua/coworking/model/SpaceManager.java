package ua.coworking.model;
public final class SpaceManager extends CoworkingMember implements TariffPlan {
    private String layout = "Початковий план";
    public SpaceManager(String id, String name) { super(id, name); }
    @Override public String role() { return "Менеджер простору"; }
    @Override public String tariff() { return "STAFF"; }
    public String manageLayout(String layout) {
        if (layout == null || layout.isBlank()) return "Відмова: план не може бути порожнім.";
        this.layout = layout.trim(); return "План розміщення: " + this.layout + ".";
    }
}
