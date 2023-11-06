import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class httpfs {
    private static final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public static void main(String[] args) {
        formattingLog();
        try {
            ServerConfig config = new ServerConfig(args);
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

    public static void formattingLog() {
        LOGGER.setLevel(Level.WARNING);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%1$tF %1$tT] [%2$-7s] %3$s%n", new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(), record.getMessage().trim());
            }
        });
        LOGGER.addHandler(consoleHandler);
        LOGGER.setUseParentHandlers(false);
    }
}