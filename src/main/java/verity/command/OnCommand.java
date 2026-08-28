package verity.command;

import java.time.LocalDate;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Lists every deadline due, or event spanning, a given date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that lists tasks occurring on the given date.
     *
     * @param date Date to filter tasks by.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(tasks.getTasksOnDate(date), date);
    }
}
