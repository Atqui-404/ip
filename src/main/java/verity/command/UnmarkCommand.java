package verity.command;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks the task at the given index as not done.
     *
     * @param index 0-based index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the task at this command's index as not done.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     * @throws VerityException If no task exists at the given index.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsNotDone();
        String response = "OK, I've marked this task as not done yet:\n  " + tasks.get(index);
        return response + save(tasks, storage);
    }
}
