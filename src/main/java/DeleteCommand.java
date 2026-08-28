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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        Task removed = tasks.remove(index);
        ui.showTaskDeleted(removed, tasks.size());
        save(tasks, storage, ui);
    }
}
