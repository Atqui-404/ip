package verity.parser;

/**
 * The set of command keywords Verity recognizes. Each constant knows its own
 * keyword and whether that keyword must match the whole input exactly (no
 * arguments expected, e.g. {@code list}) or only needs to prefix it
 * (arguments follow on the same line, e.g. {@code mark 2}).
 */
public enum CommandWord {
    LIST("list", false),
    ON("on", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    BYE("bye", false);

    private final String keyword;
    private final boolean takesArguments;

    CommandWord(String keyword, boolean takesArguments) {
        this.keyword = keyword;
        this.takesArguments = takesArguments;
    }

    /**
     * Returns this command's keyword, e.g. "mark".
     *
     * @return Keyword typed by the user to invoke this command.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command word whose keyword matches the given (already
     * lowercased) input, or {@code null} if none match.
     *
     * @param command Lowercased user input to match against.
     * @return Matching command word, or {@code null} if unrecognized.
     */
    public static CommandWord match(String command) {
        for (CommandWord candidate : values()) {
            boolean matches = candidate.takesArguments
                    ? command.startsWith(candidate.keyword)
                    : command.equals(candidate.keyword);
            if (matches) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Returns a human-readable, comma-separated list of every command's
     * keyword, e.g. "list, todo, ..., or bye", for use in error messages.
     *
     * @return Description of all recognized commands.
     */
    public static String describeAll() {
        CommandWord[] values = values();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(i == values.length - 1 ? ", or " : ", ");
            }
            result.append(values[i].keyword);
        }
        return result.toString();
    }
}
