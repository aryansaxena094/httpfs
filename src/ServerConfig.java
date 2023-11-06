import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ServerConfig {
    private int port = 8080; // default port
    private String directory = "."; // default directory
    private boolean debugging = false;
    private final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public ServerConfig(String[] args) throws IllegalArgumentException {
        parseArguments(args);
    }

    private void parseArguments(String[] args) throws IllegalArgumentException {
        boolean portSet = false, directorySet = false;

        for (int i = 0; i < args.length; i++) {
            try {
                switch (args[i]) {
                    case "-v":
                        debugging = true;
                        break;
                    case "-p":
                        if (portSet) {
                            throw new IllegalArgumentException("Port number already set to: " + port);
                        }
                        if (i + 1 < args.length) {
                            int parsedPort = Integer.parseInt(args[++i]);
                            if (parsedPort < 1024 || parsedPort > 65535) {
                                throw new IllegalArgumentException("Port number out of range: " + parsedPort);
                            }
                            port = parsedPort;
                            portSet = true;
                        } else {
                            throw new IllegalArgumentException("No port number provided after -p");
                        }
                        break;
                    case "-d":
                        if (i + 1 < args.length) {
                            directory = args[++i];
                            Path dirPath = Path.of(directory);
                            if (!Files.isDirectory(dirPath)) {
                                throw new IllegalArgumentException("Provided path is not a directory: " + directory);
                            }
                            if (!Files.isWritable(dirPath)) {
                                throw new IllegalArgumentException("Provided directory is not writable: " + directory);
                            }
                        } else {
                            throw new IllegalArgumentException("No directory provided after -d");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected argument: " + args[i]);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid port number: " + args[i], e);
            }
        }

        if (debugging) {
            LOGGER.info("Debugging enabled. Port: " + port + ". Directory: " + directory);
        }
    }

    // Getters for the configuration properties
    public int getPort() {
        return port;
    }

    public String getDirectory() {
        return directory;
    }

    public boolean isDebugging() {
        return debugging;
    }
}
