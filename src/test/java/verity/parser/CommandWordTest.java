package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CommandWordTest {

    @Test
    void match_exactKeywordCommand_matched() {
        assertEquals(CommandWord.LIST, CommandWord.match("list"));
        assertEquals(CommandWord.BYE, CommandWord.match("bye"));
    }

    @Test
    void match_exactKeywordCommandWithTrailingText_notMatched() {
        // "list" takes no arguments, so it must match the whole input, not just a prefix.
        assertNull(CommandWord.match("listing"));
    }

    @Test
    void match_argumentTakingCommandWithArguments_prefixMatched() {
        assertEquals(CommandWord.MARK, CommandWord.match("mark 2"));
        assertEquals(CommandWord.DEADLINE, CommandWord.match("deadline return book /by 2019-10-15"));
    }

    @Test
    void match_unrecognizedInput_nullReturned() {
        assertNull(CommandWord.match("gibberish"));
    }

    @Test
    void getKeyword_returnsLowercaseKeyword() {
        assertEquals("deadline", CommandWord.DEADLINE.getKeyword());
    }

    @Test
    void describeAll_listsEveryKeywordSeparatedByCommasEndingWithOr() {
        String description = CommandWord.describeAll();

        assertEquals("list, on, todo, deadline, event, mark, unmark, delete, or bye", description);
    }
}
