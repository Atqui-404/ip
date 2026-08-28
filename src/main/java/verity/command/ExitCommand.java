package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Ends the program, after printing the farewell message.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
