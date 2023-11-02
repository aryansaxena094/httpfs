import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    private final Path baseDirectory;

    FileManager(String directory){
        this.baseDirectory = Path.of(directory);
    }

    public List<String> listFiles() throws IOException {
        return Files.list(baseDirectory).map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
    }

}
