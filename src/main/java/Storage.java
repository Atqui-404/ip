import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes the task list to a fixed location on disk, so tasks
 * persist between runs of the program.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "verity.txt");

    /**
     * Writes the given tasks to disk, one per line, in save format.
     * Creates the containing "data" directory first if it doesn't already exist.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the directory or file could not be written to.
     */
    public static void save(List<Task> tasks) throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        StringBuilder content = new StringBuilder();
        for (Task task : tasks) {
            content.append(task.toSaveFormat()).append(System.lineSeparator());
        }
        Files.writeString(FILE_PATH, content.toString());
    }

    /**
     * Loads tasks previously written by {@link #save}, in the order they appear in the file.
     * Returns an empty list if the file doesn't exist yet, e.g. on the very first run.
     * A line that isn't in the expected format is skipped (with a warning printed to the
     * console) rather than aborting the whole load, so one corrupted line doesn't cost the
     * user every other saved task.
     *
     * @return Tasks read from disk.
     * @throws IOException If the file exists but could not be read.
     */
    public static ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(FILE_PATH);
        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (CorruptedSaveDataException e) {
                System.out.println("Warning: skipping corrupted save-file line " + lineNumber
                        + " (" + e.getMessage() + ")");
            }
        }
        return tasks;
    }

    /**
     * Parses one save-format line (e.g. {@code "D | 0 | return book | June 6th"}) back into
     * the {@link Task} it represents.
     *
     * @param line Save-format line, as written by {@link Task#toSaveFormat()}.
     * @return Task the line represents.
     * @throws CorruptedSaveDataException If the line doesn't have enough fields, has an
     *                                     unrecognized task type, or an invalid done flag.
     */
    private static Task parseTask(String line) throws CorruptedSaveDataException {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        if (fields.length < 3) {
            throw new CorruptedSaveDataException("expected at least 3 fields, found " + fields.length);
        }
        String type = fields[0];
        String doneFlag = fields[1];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new CorruptedSaveDataException("done flag must be '0' or '1', found '" + doneFlag + "'");
        }
        boolean isDone = doneFlag.equals("1");
        String description = fields[2];

        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (fields.length < 4) {
                throw new CorruptedSaveDataException("deadline is missing its due-date field");
            }
            task = new Deadline(description, fields[3]);
            break;
        case "E":
            if (fields.length < 5) {
                throw new CorruptedSaveDataException("event is missing its start/end-time field(s)");
            }
            task = new Event(description, fields[3], fields[4]);
            break;
        default:
            throw new CorruptedSaveDataException("unrecognized task type '" + type + "'");
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
