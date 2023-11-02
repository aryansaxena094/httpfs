import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;

public class HttpServer {
    private final ServerConfig config;
    private final FileManager fileManager;
    private final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());

    public HttpServer(ServerConfig config) {
        this.config = config;
        this.fileManager = new FileManager(config.getDirectory());
        LOGGER.setLevel(Level.WARNING);
        if (config.isDebugging()) {
            LOGGER.setLevel(Level.FINE);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            LOGGER.info("Server started");
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.fine("New connection accepted");
                    Thread thread = new Thread(new RequestHandler(clientSocket, fileManager, config.isDebugging()));
                    thread.start();
                } catch (Exception e) {
                    // This is where we handle client errors
                    LOGGER.severe("Error handling client connection: " + e.getMessage());
                    if (config.isDebugging()) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            // This is where we handle server errors
            LOGGER.severe("Error starting server: " + e.getMessage());
            if (config.isDebugging()) {
                e.printStackTrace();
            }
        }
    }
}
