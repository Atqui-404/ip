package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.task.Todo;

class CommandTest {

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("verity.txt").toString());
    }

    @Test
    void requireValidIndex_singleTaskInList_errorUsesSingularTaskWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Command command = new MarkCommand(5);

        VerityException e = assertThrows(VerityException.class, () -> command.execute(tasks, newStorage()));

        assertEquals("There is no task 6, you currently only have 1 task.", e.getMessage());
    }

    @Test
    void requireValidIndex_multipleTasksInList_errorUsesPluralTaskWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        Command command = new DeleteCommand(5);

        VerityException e = assertThrows(VerityException.class, () -> command.execute(tasks, newStorage()));

        assertEquals("There is no task 6, you currently only have 2 tasks.", e.getMessage());
    }

    @Test
    void requireValidIndex_emptyList_errorUsesPluralTaskWord() {
        TaskList tasks = new TaskList();
        Command command = new UnmarkCommand(0);

        VerityException e = assertThrows(VerityException.class, () -> command.execute(tasks, newStorage()));

        assertEquals("There is no task 1, you currently only have 0 tasks.", e.getMessage());
    }

    @Test
    void execute_saveFails_warningAppendedButNoExceptionPropagates() throws IOException, VerityException {
        // Force IOException: the save file's parent path already exists as a regular
        // file, so Files.createDirectories() inside Storage.save() cannot create it.
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Storage storage = new Storage(blocker.resolve("verity.txt").toString());
        TaskList tasks = new TaskList();
        Command command = new AddCommand(new Todo("read book"));

        String response = command.execute(tasks, storage);

        assertEquals(1, tasks.size(), "the in-memory task list must still be updated even if saving fails");
        assertTrue(response.contains("Warning: could not save tasks to disk"));
    }

    @Test
    void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void isExit_everyOtherCommand_returnsFalse() {
        assertFalse(new ListCommand().isExit());
        assertFalse(new AddCommand(new Todo("read book")).isExit());
        assertFalse(new MarkCommand(0).isExit());
        assertFalse(new UnmarkCommand(0).isExit());
        assertFalse(new DeleteCommand(0).isExit());
    }

    @Test
    void listCommand_emptyList_noTasksMessage() {
        String response = new ListCommand().execute(new TaskList(), null);

        assertEquals("You have no tasks!", response);
    }

    @Test
    void listCommand_nonEmptyList_numberedListReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String response = new ListCommand().execute(tasks, null);

        assertEquals("You have 1 tasks!\n1.[T][ ] read book", response);
    }
}
