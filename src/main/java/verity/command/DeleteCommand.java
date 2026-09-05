package verity.command;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;

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
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     * @throws VerityException If no task exists at the given index.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        Task removed = tasks.remove(index);
        String response = "Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
        return response + save(tasks, storage);
    }
}
