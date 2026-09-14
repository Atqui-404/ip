package verity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Verity's end-to-end handling of commands and recoverable errors. */
class VerityTest {

    @TempDir
    Path tempDir;

    @Test
    void getResponse_addThenUndo_taskIsRemoved() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

        verity.getResponse("todo read book");
        String response = verity.getResponse("undo");

        assertTrue(response.contains("Whatever. Un-did it"));
        assertTrue(verity.getResponse("list").contains("zero tasks"));
    }

    @Test
    void getResponse_repeatedUndo_reversesSeveralActionsInLastInFirstOutOrder() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("todo read book");
        verity.getResponse("todo write essay");
        verity.getResponse("mark 1");

        String undoMark = verity.getResponse("undo");
        String undoSecondTodo = verity.getResponse("undo");
        String undoFirstTodo = verity.getResponse("undo");
        String noMoreActions = verity.getResponse("undo");

        assertTrue(undoMark.contains("Whatever. Un-did it"));
        assertTrue(undoSecondTodo.contains("write essay"));
        assertTrue(undoFirstTodo.contains("read book"));
        assertTrue(verity.getResponse("list").contains("zero tasks"));
        assertTrue(noMoreActions.contains("nothing to undo"));
    }

    @Test
    void getResponse_nonMutatingCommand_doesNotClearUndoHistory() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("todo read book");
        verity.getResponse("list");

        String response = verity.getResponse("undo");

        assertTrue(response.contains("Whatever. Un-did it"));
        assertTrue(verity.getResponse("list").contains("zero tasks"));
    }

    @Test
    void getResponse_invalidInput_errorResponseReturnedWithoutEndingSession() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

        String response = verity.getResponse("xyzzy plugh");

        assertTrue(Verity.isErrorResponse(response));
        assertFalse(verity.isExit());
    }
}
