package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Lists every task whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks matching the given keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Lists every task whose description contains this command's keyword.
     *
     * @param tasks {@inheritDoc}
     * @param ui {@inheritDoc}
     * @param storage {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findByKeyword(keyword));
    }
}
