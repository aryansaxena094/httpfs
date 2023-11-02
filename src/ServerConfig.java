public class ServerConfig {
    private int port = 8080; // default port
    private String directory = "."; // default directory
    private boolean debugging = false;

    public ServerConfig(String[] args) throws IllegalArgumentException {
        parseArguments(args);
    }

    private void parseArguments(String[] args) throws IllegalArgumentException {
        for(int i = 0; i < args.length; i++){
            try {
                switch(args[i]) {
                    case "-v":
                        debugging = true;
                        break;
                    case "-p":
                        // Check if a port number is provided and if it's a valid integer
                        if (i + 1 < args.length) {
                            port = Integer.parseInt(args[++i]);
                        } else {
                            throw new IllegalArgumentException("No port number provided after -p");
                        }
                        break;
                    case "-d":
                        // Check if a directory path is provided
                        if (i + 1 < args.length) {
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
