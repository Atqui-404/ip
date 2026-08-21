import java.util.Scanner;

public class Verity {
    private static final String DIVIDER = "____________________________________________________________";

    private static final String BANNER = "__     __            _  _          \n"
            + "\\ \\   / / ___  _ __ (_)| |_  _   _ \n"
            + " \\ \\ / / / _ \\| '__|| || __|| | | |\n"
            + "  \\ V / |  __/| |   | || |_ | |_| |\n"
            + "   \\_/   \\___||_|   |_| \\__| \\__, |\n"
            + "                             |___/ \n";

    // Fixed-size storage for tasks, as allowed by the Level-2 requirements
    // (assume no more than 100 tasks, no need to persist to disk yet).
    private static final Task[] tasks = new Task[100];
    private static int taskCount = 0;

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
            /*
            Lists out all the tasks from 1 to taskCount if tasks exists;
            Otherwise, print "You have no tasks!" if no tasks.
             */
            if (command.equals("list")) {
                if (taskCount > 0) {
                    // Print total number of tasks
                    System.out.printf("You have %d tasks!\n", taskCount);
                    // Print all stored tasks, numbered from 1.
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                } else { // No tasks
                    System.out.println("You have no tasks!");
                }
            } else if (command.startsWith("unmark")) {
                try {
                    int index = Integer.parseInt(input.substring(6).trim()) - 1;

                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[index]);
                    } else { // Index not in range of existing tasks
                        System.out.println("ERROR: Please input a valid index to unmark as done");
                    }
                } catch (NumberFormatException e) { // Index not a number
                    System.out.println("ERROR: Please input an index to unmark as done");
                }

            } else if (command.startsWith("mark")) {
                try {
                    // "mark 2" -> index 1 (0-based) into the tasks array.
                    int index = Integer.parseInt(input.substring(4).trim()) - 1;

                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsDone();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + tasks[index]);
                    } else { // Index not in range of existing tasks
                        System.out.println("ERROR: Please input a valid index to mark as done");
                    }
                } catch (NumberFormatException e) { // Index not a number or out of range
                    System.out.println("ERROR: Please input an index to mark as done");
                }
            } else if (command.startsWith("todo")) {
                String description = input.substring(4).trim();
                addTask(new Todo(description));
            } else if (command.startsWith("deadline")) {
                String rest = input.substring(8).trim();
                String[] parts = rest.split("(?i)/by", 2);
                String description = parts[0].trim();
                String by = parts.length > 1 ? parts[1].trim() : "";
                addTask(new Deadline(description, by));
            } else if (command.startsWith("event")) {
                String rest = input.substring(5).trim();
                String[] parts = rest.split("(?i)/from", 2);
                String description = parts[0].trim();
                String from = "";
                String to = "";
                if (parts.length > 1) {
                    String[] fromTo = parts[1].split("(?i)/to", 2);
                    from = fromTo[0].trim();
                    to = fromTo.length > 1 ? fromTo[1].trim() : "";
                }
                addTask(new Event(description, from, to));
            } else {
                // Anything that isn't a recognised command is treated as a new task to store.
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
            }
            System.out.println(DIVIDER);
            input = scanner.nextLine();
            command = input.toLowerCase();
            System.out.println(DIVIDER);
        }

        System.out.println("Bye! See you soon!");
        System.out.println(DIVIDER);
    }

    /**
     * Stores the given task and prints the standard "task added" confirmation,
     * shared by the {@code todo}, {@code deadline}, and {@code event} commands.
     *
     * @param task Task to add to the task list.
     */
    private static void addTask(Task task) {
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
