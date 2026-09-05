package verity;

import java.io.IOException;

import verity.command.Command;
import verity.parser.Parser;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Entry point and top-level orchestrator: owns the {@link Ui}, {@link Storage}, and
 * {@link TaskList}, and drives the read-parse-execute loop.
 */
public class Verity {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit = false;

    /**
     * Creates Verity, loading any previously saved tasks from the given file.
     *
     * @param filePath Relative path to the save file, e.g. "data/verity.txt".
     */
    public Verity(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadWarning(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main read-parse-execute loop until an {@code ExitCommand} is executed.
     */
    public void run() {
        ui.showWelcome();
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                ui.showResponse(getResponse(fullCommand));
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Parses and executes a single line of input, for use by a GUI (or any caller that wants
     * one command's response as a string rather than driving the read-parse-execute loop).
     * Check {@link #isExit()} afterwards to know whether the user asked to exit.
     *
     * @param input Full line of user input.
     * @return Response to show the user.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            String response = command.execute(tasks, storage);
            isExit = command.isExit();
            return response;
        } catch (VerityException e) {
            return "ERROR!!! >.<\n" + e.getMessage();
        }
    }

    /**
     * Returns whether the most recently executed command was an exit command.
     *
     * @return {@code true} if the user asked to exit.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Entry point of the program: creates Verity backed by the given save file and runs it.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Verity("data/verity.txt").run();
    }
}
