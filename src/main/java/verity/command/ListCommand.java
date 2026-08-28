package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Lists every task currently in the task list.
 */
public class ListCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
