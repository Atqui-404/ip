import java.util.Scanner;

public class Verity {
    private static final String DIVIDER = "----------------------------------------------------";

    private static final String BANNER = "__     __            _  _          \n"
            + "\\ \\   / / ___  _ __ (_)| |_  _   _ \n"
            + " \\ \\ / / / _ \\| '__|| || __|| | | |\n"
            + "  \\ V / |  __/| |   | || |_ | |_| |\n"
            + "   \\_/   \\___||_|   |_| \\__| \\__, |\n"
            + "                             |___/ \n";

    // Fixed-size storage for tasks, as allowed by the Level-2 requirements
    // (assume no more than 100 tasks, no need to persist to disk yet).
    private static final String[] tasks = new String[100];
    private static final boolean[] isDone = new boolean[100];
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
                        String icon = isDone[i] ? "X" : " "; // X if done, else empty space
                        System.out.println((i + 1) + ".[" + icon + "] " + tasks[i]);
                    }
                } else { // No tasks
                    System.out.println("You have no tasks!");
                }
            } else if (command.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7).trim()) - 1;

                if (index >= 0 && index < taskCount) {
                    isDone[index] = false;
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  [ ] " + tasks[index]);
                } else { // Index not in range of existing tasks
                    System.out.println("ERROR: Please input an index to unmark as done");
                }

            } else if (command.startsWith("mark ")) {
                // "mark 2" -> index 1 (0-based) into the tasks/isDone arrays.
                int index = Integer.parseInt(input.substring(5).trim()) - 1;

                if (index >= 0 && index < taskCount) {
                    isDone[index] = true;
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  [X] " + tasks[index]);
                } else { // Index not in range of existing tasks
                    System.out.println("ERROR: Please input an index to mark as done");
                }
            } else {
                // Anything that isn't "list" or "bye" is treated as a new task to store.
                tasks[taskCount] = input;
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
}
