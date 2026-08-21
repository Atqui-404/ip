/**
 * Represents a task that needs to be done before a specific date/time.
 * The date/time is kept as a free-form string; no parsing into an actual
 * date/time type is done at this stage.
 */
public class Deadline extends Task {

    protected String by;

    /**
     * Creates a deadline with the given description and due date/time.
     *
     * @param description Description of the deadline.
     * @param by Date/time by which the task should be done.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
