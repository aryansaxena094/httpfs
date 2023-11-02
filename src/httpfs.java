import java.util.logging.Level;
import java.util.logging.Logger;

public class httpfs {
    private static final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public static void main(String[] args) {
        LOGGER.setLevel(Level.WARNING); // Default Level is WARNING, so we don't debug by default

        String line = "-v -p 8080 -d thedirectorypath";
        String[] args2 = line.split(" ");
        try {
            ServerConfig config = new ServerConfig(args2);
            if (config.isDebugging()) {
                LOGGER.setLevel(Level.FINE);
            }
            // This is where we start the server
            HttpServer server = new HttpServer(config);
            server.start();
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Error parsing arguments: " + e.getMessage());
        }
    }
}
