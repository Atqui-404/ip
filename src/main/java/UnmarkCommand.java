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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws VerityException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsNotDone();
        ui.showTaskUnmarked(tasks.get(index));
        save(tasks, storage, ui);
    }
}
