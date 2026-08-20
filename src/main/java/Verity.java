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
            if (command.equals("list")) {
                if (taskCount > 0) {
                    // Print total number of tasks
                    System.out.printf("You have %d tasks!\n", taskCount);
                    // Print all stored tasks, numbered from 1.
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                } else { // No tasks
                    System.out.println("You have no tasks!");
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
