import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class httpfs {
    private static final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public static void main(String[] args) {
        LOGGER.setLevel(Level.WARNING);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(consoleHandler);
        LOGGER.setUseParentHandlers(false);

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
