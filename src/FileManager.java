import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class FileManager {
    private final Path baseDirectory;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    FileManager(String directory) {
        this.baseDirectory = Path.of(directory);
    }

    public List<String> listFiles() throws IOException {
        readWriteLock.readLock().lock();
        try {
            return Files.list(baseDirectory)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public String readFile(String fileName) throws IOException {
        readWriteLock.readLock().lock();
        try {
            Path filePath = baseDirectory.resolve(fileName);
            validatePath(filePath);
            return new String(Files.readAllBytes(filePath));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void writeFile(String fileName, String content) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            Path filePath = baseDirectory.resolve(fileName);
            validatePath(filePath);
            Files.write(filePath, content.getBytes());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public boolean fileExists(String fileName) {
        readWriteLock.readLock().lock();
        try {
            Path filePath = baseDirectory.resolve(fileName);
            return Files.exists(filePath);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private void validatePath(Path filePath) {
        if (!filePath.normalize().startsWith(baseDirectory)) {
            throw new SecurityException("Attempt to access file outside of base directory");
        }
    }
}
