package ua.coworking.model;
import java.util.Objects;
/** Спільна ідентичність учасників; кожна роль має власну поведінку. */
public abstract class CoworkingMember {
    private final String id;
    private final String name;
    protected CoworkingMember(String id, String name) {
        this.id = Objects.requireNonNull(id); this.name = Objects.requireNonNull(name);
    }
    public final String getId() { return id; }
    public String getName() { return name; }
    public abstract String role();
}
