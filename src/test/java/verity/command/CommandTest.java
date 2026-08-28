package verity.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

class CommandTest {

    @TempDir
    Path tempDir;

    private final Ui ui = new Ui();

    @Test
    void requireValidIndex_singleTaskInList_errorUsesSingularTaskWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Command command = new MarkCommand(5);

        VerityException e = assertThrows(VerityException.class,
                () -> command.execute(tasks, ui, new Storage(tempDir.resolve("verity.txt").toString())));

        assertEquals("There is no task 6, you currently only have 1 task.", e.getMessage());
    }

    @Test
    void requireValidIndex_multipleTasksInList_errorUsesPluralTaskWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        Command command = new DeleteCommand(5);

        VerityException e = assertThrows(VerityException.class,
                () -> command.execute(tasks, ui, new Storage(tempDir.resolve("verity.txt").toString())));

        assertEquals("There is no task 6, you currently only have 2 tasks.", e.getMessage());
    }

    @Test
    void requireValidIndex_emptyList_errorUsesPluralTaskWord() {
        TaskList tasks = new TaskList();
        Command command = new UnmarkCommand(0);

        VerityException e = assertThrows(VerityException.class,
                () -> command.execute(tasks, ui, new Storage(tempDir.resolve("verity.txt").toString())));

        assertEquals("There is no task 1, you currently only have 0 tasks.", e.getMessage());
    }

    @Test
    void execute_saveFails_warningPrintedButNoExceptionPropagates() throws IOException, VerityException {
        // Force IOException: the save file's parent path already exists as a regular
        // file, so Files.createDirectories() inside Storage.save() cannot create it.
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Storage storage = new Storage(blocker.resolve("verity.txt").toString());
        TaskList tasks = new TaskList();
        Command command = new AddCommand(new Todo("read book"));

        String output = captureStdout(() -> command.execute(tasks, ui, storage));

        assertEquals(1, tasks.size(), "the in-memory task list must still be updated even if saving fails");
        assertTrue(output.contains("Warning: could not save tasks to disk"));
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
    void listCommand_execute_doesNotThrowOnEmptyOrNonEmptyList() {
        assertDoesNotThrow(() -> new ListCommand().execute(new TaskList(), ui, null));

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertDoesNotThrow(() -> new ListCommand().execute(tasks, ui, null));
    }

    private static String captureStdout(ThrowingRunnable action) throws VerityException {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws VerityException;
    }
}
