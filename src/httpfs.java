import java.util.logging.Level;
import java.util.logging.Logger;

public class httpfs {
    private static final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public static void main(String[] args) {
        LOGGER.setLevel(Level.WARNING);
        String line = "-p 8080 -d /Users/aryansaxena/Desktop/CN/LA2/data";
        String[] args2 = line.split(" ");
        try {
            ServerConfig config = new ServerConfig(args2);
            if (config.isDebugging()) {
                LOGGER.setLevel(Level.FINE);
                LOGGER.info("Debugging has been enabled");
            }
            HttpServer server = new HttpServer(config);
            server.start();
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Error parsing arguments: " + e.getMessage());
        }
    }
}
