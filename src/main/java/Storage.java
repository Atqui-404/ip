import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
