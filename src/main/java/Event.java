import java.time.LocalDate;

/**
 * Represents a task that starts and ends on specific dates.
 */
public class Event extends Task {

    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event with the given description, start, and end date.
     *
     * @param description Description of the event.
     * @param from Date the event starts.
     * @param to Date the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DATE_DISPLAY_FORMAT)
                + " to: " + to.format(DATE_DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }
}
