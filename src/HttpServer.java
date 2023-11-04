import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class HttpServer {
    private final ServerConfig config;
    private final FileManager fileManager;
    private final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public HttpServer(ServerConfig config) {
        this.config = config;
        this.fileManager = new FileManager(config.getDirectory());
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            LOGGER.info("Server started on port: " + config.getPort());
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.info("New connection accepted");
                    Thread thread = new Thread(new RequestHandler(clientSocket, fileManager));
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
