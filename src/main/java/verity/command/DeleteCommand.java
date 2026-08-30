package verity.command;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Removes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that removes the task at the given index.
     *
     * @param index 0-based index of the task to remove.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Removes the task at this command's index from the task list.
     *
     * @param tasks {@inheritDoc}
     * @param ui {@inheritDoc}
     * @param storage {@inheritDoc}
     * @throws VerityException If no task exists at the given index.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        Task removed = tasks.remove(index);
        ui.showTaskDeleted(removed, tasks.size());
        save(tasks, storage, ui);
    }
}
