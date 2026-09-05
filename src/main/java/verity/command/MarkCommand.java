package verity.command;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;

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
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     * @throws VerityException If no task exists at the given index.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsDone();
        String response = "Nice! I've marked this task as done:\n  " + tasks.get(index);
        return response + save(tasks, storage);
    }
}
