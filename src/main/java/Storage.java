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
     *
     * @return Tasks read from disk.
     * @throws IOException If the file exists but could not be read.
     */
    public static ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }
        for (String line : Files.readAllLines(FILE_PATH)) {
            tasks.add(parseTask(line));
        }
        return tasks;
    }

    /**
     * Parses one save-format line (e.g. {@code "D | 0 | return book | June 6th"}) back into
     * the {@link Task} it represents.
     *
     * @param line Save-format line, as written by {@link Task#toSaveFormat()}.
     * @return Task the line represents.
     */
    private static Task parseTask(String line) {
        String[] fields = line.split("\\|");
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        String type = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];

        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            task = new Deadline(description, fields[3]);
            break;
        case "E":
            task = new Event(description, fields[3], fields[4]);
            break;
        default:
            throw new IllegalStateException("Unknown task type in save file: " + type);
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
