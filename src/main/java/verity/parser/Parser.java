package verity.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import verity.VerityException;
import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.OnCommand;
import verity.command.UnmarkCommand;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Todo;

/**
 * Turns a full line of user input into the {@link Command} it represents.
 */
public class Parser {

    /**
     * Parses a full line of user input into the {@link Command} it represents.
     *
     * @param fullCommand Full line of user input.
     * @return Command to execute.
     * @throws VerityException If the input isn't a recognized command, or its arguments are
     *                          malformed (e.g. an empty description, a missing marker, or an
     *                          invalid date). A task index that doesn't exist is <em>not</em>
     *                          caught here - that's only knowable once the command executes
     *                          against the actual task list.
     */
    public static Command parse(String fullCommand) throws VerityException {
        String command = fullCommand.toLowerCase();
        CommandWord matched = CommandWord.match(command);
        if (matched == null) {
            throw new VerityException(
                    "That's an invalid command! >:[\nTry " + CommandWord.describeAll() + ". :)");
        }
        switch (matched) {
            case LIST:
                return new ListCommand();
            case ON:
                return new OnCommand(parseOnDate(fullCommand));
            case UNMARK:
                return new UnmarkCommand(parseTaskIndex(fullCommand, CommandWord.UNMARK));
            case MARK:
                return new MarkCommand(parseTaskIndex(fullCommand, CommandWord.MARK));
            case DELETE:
                return new DeleteCommand(parseTaskIndex(fullCommand, CommandWord.DELETE));
            case TODO:
                return new AddCommand(parseTodo(fullCommand));
            case DEADLINE:
                return new AddCommand(parseDeadline(fullCommand));
            case EVENT:
                return new AddCommand(parseEvent(fullCommand));
            case BYE:
                // Fallthrough
            default:
                return new ExitCommand();
        }
    }

    /**
     * Parses a {@code todo} command into a {@link Todo}.
     *
     * @param input Full line of user input, starting with "todo".
     * @return Todo built from the input.
     * @throws VerityException If the description is empty.
     */
    private static Todo parseTodo(String input) throws VerityException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new VerityException("The description of a todo can't be empty. Try `todo <what you want to do>`. ;)");
        }
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline} command into a {@link Deadline}.
     *
     * @param input Full line of user input, starting with "deadline".
     * @return Deadline built from the input.
     * @throws VerityException If the description is empty, the {@code /by} marker is missing,
     *                          the due date after it is empty, or isn't a valid {@code yyyy-MM-dd} date.
     */
    private static Deadline parseDeadline(String input) throws VerityException {
        String rest = input.substring("deadline".length()).trim();
        String[] parts = rest.split("(?i)/by", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new VerityException(
                    "The description of a deadline can't be empty... :( \nTry `deadline <what to do> /by <when>`.");
        }
        if (parts.length < 2) {
            throw new VerityException(
                    "A deadline needs a due date! >:( \nAdd `/by <when>` after the task description.");
        }
        String by = parts[1].trim();
        if (by.isEmpty()) {
            throw new VerityException("The due time after `/by` can't be empty. Tell me when it's due.");
        }
        return new Deadline(description, parseDate(by, "/by"));
    }

    /**
     * Parses a date string in {@code yyyy-MM-dd} format, e.g. "2019-10-15".
     *
     * @param text Date text to parse.
     * @param marker Marker the date followed (e.g. "/by"), used to word the error message.
     * @return Parsed date.
     * @throws VerityException If the text isn't a valid date in {@code yyyy-MM-dd} format.
     */
    private static LocalDate parseDate(String text, String marker) throws VerityException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new VerityException("The date after `" + marker
                    + "` must be in yyyy-MM-dd format (e.g. 2019-10-15), not '" + text + "'.");
        }
    }

    /**
     * Parses an {@code on} command into the date to filter tasks by.
     *
     * @param input Full line of user input, starting with "on".
     * @return Date to filter deadlines/events by.
     * @throws VerityException If no date is given, or it isn't a valid {@code yyyy-MM-dd} date.
     */
    private static LocalDate parseOnDate(String input) throws VerityException {
        String text = input.substring("on".length()).trim();
        if (text.isEmpty()) {
            throw new VerityException("Tell me which date to look up! Try `on <yyyy-MM-dd>`, e.g. `on 2019-10-15`.");
        }
        return parseDate(text, "on");
    }

    /**
     * Parses an {@code event} command into an {@link Event}.
     *
     * @param input Full line of user input, starting with "event".
     * @return Event built from the input.
     * @throws VerityException If the description is empty, the {@code /from} or {@code /to}
     *                          marker is missing, either date after them is empty, or isn't a
     *                          valid {@code yyyy-MM-dd} date.
     */
    private static Event parseEvent(String input) throws VerityException {
        String rest = input.substring("event".length()).trim();
        String[] parts = rest.split("(?i)/from", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new VerityException(
                    "The description of an event can't be empty... :( \n"
                            + "Try `event <what's happening> /from <start> /to <end>`.");
        }
        if (parts.length < 2) {
            throw new VerityException("An event needs a start time! :| \nAdd `/from <when>` after the description.");
        }
        String[] fromTo = parts[1].split("(?i)/to", 2);
        if (fromTo.length < 2) {
            throw new VerityException("An event needs an end time. :| \nAdd `/to <when>` after the start time.");
        }
        String from = fromTo[0].trim();
        if (from.isEmpty()) {
            throw new VerityException("The start time after `/from` can't be empty! \nTell me when it begins.");
        }
        String to = fromTo[1].trim();
        if (to.isEmpty()) {
            throw new VerityException("The end time after `/to` can't be empty! \nTell me when it ends.");
        }
        return new Event(description, parseDate(from, "/from"), parseDate(to, "/to"));
    }

    /**
     * Parses the task number following a {@code mark}/{@code unmark}/{@code delete} command.
     * Only checks that it's a well-formed number - whether a task actually exists at that
     * index is checked later, by the resulting command, once it has access to the task list.
     *
     * @param input Full line of user input, starting with the command keyword.
     * @param command Command the number was given for (its keyword is used to word the
     *                error messages and to know how many characters of {@code input}
     *                are the command word).
     * @return 0-based index the number refers to.
     * @throws VerityException If no number was given, or it isn't a number.
     */
    private static int parseTaskIndex(String input, CommandWord command) throws VerityException {
        String keyword = command.getKeyword();
        String rest = input.substring(keyword.length()).trim();
        if (rest.isEmpty()) {
            throw new VerityException(
                    "Tell me which task number you want to " + keyword + "! For example: `" + keyword + " 2`.");
        }
        int number;
        try {
            number = Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            throw new VerityException(
                    "'" + rest + "' isn't a valid task number.");
        }
        return number - 1;
    }
}
