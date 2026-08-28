import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all interaction with the user: printing messages to the console
 * and reading command input.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private static final String BANNER = "__     __            _  _          \n"
            + "\\ \\   / / ___  _ __ (_)| |_  _   _ \n"
            + " \\ \\ / / / _ \\| '__|| || __|| | | |\n"
            + "  \\ V / |  __/| |   | || |_ | |_| |\n"
            + "   \\_/   \\___||_|   |_| \\__| \\__, |\n"
            + "                             |___/ \n";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the welcome banner and greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Verity");
        System.out.println("Ask me anything! I know everything!");
        System.out.println(DIVIDER);
    }

    /**
     * Reads the next line of user input.
     *
     * @return Line of input typed by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the divider line used to separate one command's output from the next.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints an error message in the standard error format.
     *
     * @param message Message explaining what went wrong.
     */
    public void showError(String message) {
        System.out.println("ERROR!!! >.<\n" + message);
    }

    /**
     * Prints the farewell message shown when the user exits.
     */
    public void showGoodbye() {
        System.out.println("Bye! See you soon! ;)");
    }

    /**
     * Prints the confirmation shown after a task is added.
     *
     * @param task Task that was added.
     * @param totalCount Total number of tasks after adding it.
     */
    public void showTaskAdded(Task task, int totalCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Prints the confirmation shown after a task is marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Prints the confirmation shown after a task is marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task Task that was removed.
     * @param totalCount Total number of tasks remaining after removing it.
     */
    public void showTaskDeleted(Task task, int totalCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Prints the full task list, numbered from 1, or a "no tasks" message if it's empty.
     *
     * @param tasks Tasks to print.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("You have no tasks!");
            return;
        }
        System.out.printf("You have %d tasks!\n", tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the tasks occurring on a given date, numbered from 1, or a "no tasks"
     * message if none match.
     *
     * @param matches Tasks occurring on {@code date}.
     * @param date Date they were filtered by.
     */
    public void showTasksOnDate(List<Task> matches, LocalDate date) {
        String formattedDate = date.format(Task.DATE_DISPLAY_FORMAT);
        if (matches.isEmpty()) {
            System.out.println("You have no tasks on " + formattedDate + "!");
            return;
        }
        System.out.printf("You have %d tasks on %s!\n", matches.size(), formattedDate);
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Prints a warning that the task list could not be saved to disk.
     *
     * @param reason Reason the save failed.
     */
    public void showSaveWarning(String reason) {
        System.out.println("Warning: could not save tasks to disk (" + reason + ")");
    }

    /**
     * Prints a warning that saved tasks could not be loaded from disk.
     *
     * @param reason Reason the load failed.
     */
    public void showLoadWarning(String reason) {
        System.out.println("Warning: could not load saved tasks (" + reason + ")");
    }
}
