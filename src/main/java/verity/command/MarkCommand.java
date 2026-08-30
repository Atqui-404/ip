package verity.command;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks the task at the given index as done.
     *
     * @param index 0-based index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the task at this command's index as done.
     *
     * @param tasks {@inheritDoc}
     * @param ui {@inheritDoc}
     * @param storage {@inheritDoc}
     * @throws VerityException If no task exists at the given index.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsDone();
        ui.showTaskMarked(tasks.get(index));
        save(tasks, storage, ui);
    }
}
