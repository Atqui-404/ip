package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.VerityException;
import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.OnCommand;
import verity.command.UnmarkCommand;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

class ParserTest {

    @TempDir
    Path tempDir;

    private final Ui ui = new Ui();

    private Storage newStorage() {
        return new Storage(tempDir.resolve("verity.txt").toString());
    }

    // ---- todo ----

    @Test
    void parse_validTodo_addsTodoToTaskList() throws VerityException, IOException {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, command);

        TaskList tasks = new TaskList();
        command.execute(tasks, ui, newStorage());

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void parse_todoEmptyDescription_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("todo"));
    }

    // ---- deadline ----

    @Test
    void parse_validDeadline_addsDeadlineWithParsedDate() throws VerityException, IOException {
        Command command = Parser.parse("deadline return book /by 2019-10-15");

        TaskList tasks = new TaskList();
        command.execute(tasks, ui, newStorage());

        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    void parse_deadlineMissingByMarker_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    void parse_deadlineEmptyDescription_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("deadline /by 2019-10-15"));
    }

    @Test
    void parse_deadlineEmptyDateText_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("deadline return book /by"));
    }

    @Test
    void parse_deadlineInvalidDateFormat_exceptionThrown() {
        String input = "deadline return book /by tomorrow";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    void parse_deadlineByMarkerIsCaseInsensitive_parsedSuccessfully() throws VerityException, IOException {
        Command command = Parser.parse("deadline return book /BY 2019-10-15");

        TaskList tasks = new TaskList();
        command.execute(tasks, ui, newStorage());

        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    // ---- event ----

    @Test
    void parse_validEvent_addsEventWithParsedDates() throws VerityException, IOException {
        Command command = Parser.parse("event project meeting /from 2019-08-06 /to 2019-08-07");

        TaskList tasks = new TaskList();
        command.execute(tasks, ui, newStorage());

        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", tasks.get(0).toString());
    }

    @Test
    void parse_eventMissingFromMarker_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event project meeting"));
    }

    @Test
    void parse_eventMissingToMarker_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event project meeting /from 2019-08-06"));
    }

    @Test
    void parse_eventEmptyDescription_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event /from 2019-08-06 /to 2019-08-07"));
    }

    @Test
    void parse_eventInvalidToDate_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event meeting /from 2019-08-06 /to whenever"));
    }

    // ---- list ----

    @Test
    void parse_list_returnsListCommand() {
        assertInstanceOf(ListCommand.class, assertDoesNotThrowParse("list"));
    }

    // ---- on ----

    @Test
    void parse_validOnDate_returnsOnCommandThatFiltersMatchingTasks() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("deadline return book /by 2019-12-02").execute(tasks, ui, newStorage());
        Parser.parse("deadline other /by 2019-12-25").execute(tasks, ui, newStorage());

        Command onCommand = Parser.parse("on 2019-12-02");
        assertInstanceOf(OnCommand.class, onCommand);
        // Executing it must not throw, and must not mutate the task list.
        onCommand.execute(tasks, ui, newStorage());

        assertEquals(2, tasks.size());
    }

    @Test
    void parse_onMissingDate_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("on"));
    }

    @Test
    void parse_onInvalidDate_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("on someday"));
    }

    // ---- find ----

    @Test
    void parse_validFind_returnsFindCommandThatFiltersMatchingTasks() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, ui, newStorage());
        Parser.parse("todo write essay").execute(tasks, ui, newStorage());

        Command findCommand = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, findCommand);
        // Executing it must not throw, and must not mutate the task list.
        findCommand.execute(tasks, ui, newStorage());

        assertEquals(2, tasks.size());
    }

    @Test
    void parse_findMissingKeyword_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("find"));
    }

    // ---- mark / unmark / delete ----

    @Test
    void parse_validMark_marksTaskAsDone() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, ui, newStorage());

        Command markCommand = Parser.parse("mark 1");
        assertInstanceOf(MarkCommand.class, markCommand);
        markCommand.execute(tasks, ui, newStorage());

        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void parse_validUnmark_marksTaskAsNotDone() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, ui, newStorage());
        Parser.parse("mark 1").execute(tasks, ui, newStorage());

        Command unmarkCommand = Parser.parse("unmark 1");
        assertInstanceOf(UnmarkCommand.class, unmarkCommand);
        unmarkCommand.execute(tasks, ui, newStorage());

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void parse_validDelete_removesTask() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, ui, newStorage());

        Command deleteCommand = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, deleteCommand);
        deleteCommand.execute(tasks, ui, newStorage());

        assertTrue(tasks.isEmpty());
    }

    @Test
    void parse_markMissingNumber_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("mark"));
    }

    @Test
    void parse_markNonNumericArgument_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    void parse_markIndexOutOfRange_exceptionThrownOnlyAtExecuteTime() throws VerityException {
        // Parsing succeeds - "5" is a well-formed number. Only execute(), which has access
        // to the actual task list, can know it doesn't refer to an existing task.
        Command markCommand = Parser.parse("mark 5");

        TaskList tasks = new TaskList();
        VerityException e = assertThrows(VerityException.class, () -> markCommand.execute(tasks, ui, newStorage()));
        assertTrue(e.getMessage().contains("no task 5"));
    }

    // ---- bye ----

    @Test
    void parse_bye_returnsExitCommandWhoseIsExitIsTrue() {
        Command command = assertDoesNotThrowParse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    // ---- unrecognized ----

    @Test
    void parse_unrecognizedCommand_exceptionThrown() {
        VerityException e = assertThrows(VerityException.class, () -> Parser.parse("gibberish"));

        assertTrue(e.getMessage().contains("invalid command"));
    }

    private static Command assertDoesNotThrowParse(String input) {
        try {
            return Parser.parse(input);
        } catch (VerityException e) {
            throw new AssertionError("Parser.parse(\"" + input + "\") should not have thrown", e);
        }
    }
}
