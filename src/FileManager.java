import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    private final Path baseDirectory;

    FileManager(String directory) {
        this.baseDirectory = Path.of(directory);
    }

    public synchronized List<String> listFiles() throws IOException {
        return Files.list(baseDirectory).map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
    }

    public synchronized String readFile(String fileName) throws IOException {
        Path filePath = baseDirectory.resolve(fileName);
        validatePath(filePath);
        return new String(Files.readAllBytes(filePath));
    }

    public synchronized void writeFile(String fileName, String content) throws IOException {
        Path filePath = baseDirectory.resolve(fileName);
        validatePath(filePath);
        Files.write(filePath, content.getBytes());
    }

    public synchronized boolean fileExists(String fileName) {
        Path filePath = baseDirectory.resolve(fileName);
        return Files.exists(filePath);
    }

    private void validatePath(Path filePath) {
        if (!filePath.normalize().startsWith(baseDirectory)) {
            throw new SecurityException("Attempt to access file outside of base directory");
        }
    }
}
