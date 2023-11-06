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

        for (int i = 0; i < args.length; i++) {
            try {
                switch (args[i]) {
                    case "-v":
                        LOGGER.info("Debugging enabled");
                        debugging = true;
                        break;
                    case "-p":
                        // Check if a port number is provided and if it's a valid integer
                        if (i + 1 < args.length) {
                            LOGGER.info("Using port number: " + args[i + 1]);
                            port = Integer.parseInt(args[++i]);
                        } else {
                            throw new IllegalArgumentException("No port number provided after -p");
                        }
                        break;
                    case "-d":
                        // Check if a directory path is provided
                        if (i + 1 < args.length) {
                            LOGGER.info("Using directory: " + args[i + 1]);
                            directory = args[++i];
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
