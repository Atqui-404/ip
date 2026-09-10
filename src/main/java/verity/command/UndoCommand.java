package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;

/**
 * Reverses the most recently executed undoable command (add/delete/mark/unmark), if any.
 */
public class UndoCommand extends Command {
    private final Undoable lastUndoableCommand;

    /**
     * Creates a command that reverses the given command.
     *
     * @param lastUndoableCommand Most recently executed undoable command, or {@code null}
     *                            if there is nothing to undo.
     */
    public UndoCommand(Undoable lastUndoableCommand) {
        this.lastUndoableCommand = lastUndoableCommand;
    }

    /**
     * Reverses {@link #lastUndoableCommand}, or reports that there's nothing to undo.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        if (lastUndoableCommand == null) {
            return "Nothing to undo!";
        }
        return lastUndoableCommand.undo(tasks, storage);
    }
}
