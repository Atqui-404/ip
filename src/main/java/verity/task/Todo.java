package verity.task;

/**
 * Represents a task without any date/time attached to it.
 */
public class Todo extends Task {

    /**
     * Creates a todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }
}
