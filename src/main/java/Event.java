/**
 * Represents a task that starts and ends at specific dates/times.
 * The dates/times are kept as free-form strings; no parsing into an actual
 * date/time type is done at this stage.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /**
     * Creates an event with the given description, start, and end date/time.
     *
     * @param description Description of the event.
     * @param from Date/time the event starts.
     * @param to Date/time the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
