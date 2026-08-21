import java.util.ArrayList;
import java.util.Scanner;

public class Verity {
    private static final String DIVIDER = "____________________________________________________________";

    private static final String BANNER = "__     __            _  _          \n"
            + "\\ \\   / / ___  _ __ (_)| |_  _   _ \n"
            + " \\ \\ / / / _ \\| '__|| || __|| | | |\n"
            + "  \\ V / |  __/| |   | || |_ | |_| |\n"
            + "   \\_/   \\___||_|   |_| \\__| \\__, |\n"
            + "                             |___/ \n";

    // A-Collections: dynamically-sized storage for tasks, so there's no
    // fixed capacity to run out of (no need to persist to disk yet).
    private static final ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Verity");
        System.out.println("Ask me anything! I know everything!");
        System.out.println(DIVIDER);

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String command = input.toLowerCase();
        System.out.println(DIVIDER);

        while (!command.equals("bye")) {
            try {
                /*
                Lists out all the tasks, numbered from 1, if any exist;
                Otherwise, print "You have no tasks!" if no tasks.
                 */
                Command matched = Command.match(command);
                if (matched == null) {
                    throw new VerityException(
                            "That's an invalid command! >:[\nTry " + Command.describeAll() + ". :)");
                }
                switch (matched) {
                case LIST: {
                    if (!tasks.isEmpty()) {
                        // Print total number of tasks
                        System.out.printf("You have %d tasks!\n", tasks.size());
                        // Print all stored tasks, numbered from 1.
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println((i + 1) + "." + tasks.get(i));
                        }
                    } else { // No tasks
                        System.out.println("You have no tasks!");
                    }
                    break;
                }
                case UNMARK: {
                    int index = parseTaskIndex(input, Command.UNMARK);
                    tasks.get(index).markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(index));
                    break;
                }
                case MARK: {
                    int index = parseTaskIndex(input, Command.MARK);
                    tasks.get(index).markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(index));
                    break;
                }
                case DELETE: {
                    int index = parseTaskIndex(input, Command.DELETE);
                    Task removed = tasks.remove(index);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    break;
                }
                case TODO:
                    addTask(parseTodo(input));
                    break;
                case DEADLINE:
                    addTask(parseDeadline(input));
                    break;
                case EVENT:
                    addTask(parseEvent(input));
                    break;
                case BYE:
                default:
                    // BYE is unreachable here: the while-loop condition above already
                    // exits before "bye" reaches this switch. default is unreachable
                    // too, since an unrecognized command throws before the switch is
                    // entered. Both are included only so every Command constant is covered.
                    break;
                }
            } catch (VerityException e) {
                System.out.println("ERROR!!! >.<\n" + e.getMessage());
            }
            System.out.println(DIVIDER);
            input = scanner.nextLine();
            command = input.toLowerCase();
            System.out.println(DIVIDER);
        }

        System.out.println("Bye! See you soon! ;)");
        System.out.println(DIVIDER);
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
     *                          or the due time after it is empty.
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
            throw new VerityException("A deadline needs a due date! >:( \nAdd `/by <when>` after the task description.");
        }
        String by = parts[1].trim();
        if (by.isEmpty()) {
            throw new VerityException("The due time after `/by` can't be empty. Tell me when it's due.");
        }
        return new Deadline(description, by);
    }

    /**
     * Parses an {@code event} command into an {@link Event}.
     *
     * @param input Full line of user input, starting with "event".
     * @return Event built from the input.
     * @throws VerityException If the description is empty, the {@code /from} or {@code /to}
     *                          marker is missing, or either time after them is empty.
     */
    private static Event parseEvent(String input) throws VerityException {
        String rest = input.substring("event".length()).trim();
        String[] parts = rest.split("(?i)/from", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new VerityException(
                    "The description of an event can't be empty... :( \nTry `event <what's happening> /from <start> /to <end>`.");
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
        return new Event(description, from, to);
    }

    /**
     * Parses the task number following a {@code mark}/{@code unmark}/{@code delete} command
     * and validates it against the current task list.
     *
     * @param input Full line of user input, starting with the command keyword.
     * @param command Command the number was given for (its keyword is used to word the
     *                error messages and to know how many characters of {@code input}
     *                are the command word).
     * @return 0-based index of the referenced task.
     * @throws VerityException If no number was given, it isn't a number, or it's out of range.
     */
    private static int parseTaskIndex(String input, Command command) throws VerityException {
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
        int index = number - 1;
        if (index < 0 || index >= tasks.size()) {
            String taskWord = tasks.size() == 1 ? "task" : "tasks";
            throw new VerityException(
                    "There is no task " + number + ", you currently only have " + tasks.size() + " " + taskWord + ".");
        }
        return index;
    }

    /**
     * Stores the given task and prints the standard "task added" confirmation,
     * shared by the {@code todo}, {@code deadline}, and {@code event} commands.
     *
     * @param task Task to add to the task list.
     */
    private static void addTask(Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }
}
