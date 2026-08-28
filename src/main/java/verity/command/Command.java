package verity.command;

import java.io.IOException;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a single user command: something that can be executed against
 * the current task list, reporting through the UI and persisting through
 * storage.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param tasks Task list to act on.
     * @param ui UI to report results through.
     * @param storage Storage to persist any changes through.
     * @throws VerityException If the command can't be carried out, e.g. an invalid task index.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws VerityException;

    /**
     * Returns whether this command should end the program after executing.
     * Overridden by {@link ExitCommand}; every other command keeps the program running.
     *
     * @return {@code true} if the program should exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list to disk, printing a warning instead of failing if it can't be
     * saved. Shared by every command that mutates the task list.
     *
     * @param tasks Task list to save.
     * @param storage Storage to save through.
     * @param ui UI to print a warning through, if saving fails.
     */
    protected void save(TaskList tasks, Storage storage, Ui ui) {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }

    /**
     * Checks that the given 0-based index refers to an existing task, throwing an error
     * worded the same way for every command that takes a task index.
     *
     * @param tasks Task list to check the index against.
     * @param index 0-based index to validate.
     * @throws VerityException If no task exists at that index.
     */
    protected void requireValidIndex(TaskList tasks, int index) throws VerityException {
        if (!tasks.isValidIndex(index)) {
            String taskWord = tasks.size() == 1 ? "task" : "tasks";
            throw new VerityException("There is no task " + (index + 1)
                    + ", you currently only have " + tasks.size() + " " + taskWord + ".");
        }
    }
}
