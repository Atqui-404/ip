import java.io.IOException;

/**
 * Entry point and top-level orchestrator: owns the {@link Ui}, {@link Storage}, and
 * {@link TaskList}, and drives the read-parse-execute loop.
 */
public class Verity {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

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
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (VerityException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Verity("data/verity.txt").run();
    }
}
