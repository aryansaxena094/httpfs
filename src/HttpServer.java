import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {
    private final ServerConfig config;
    private final FileManager fileManager;
    private final Logger LOGGER = Logger.getLogger(httpfs.class.getName());
    private final AtomicInteger clientNumber = new AtomicInteger(0);

    public HttpServer(ServerConfig config) {
        this.config = config;
        this.fileManager = new FileManager(config.getDirectory());
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            LOGGER.info("Server started on port: " + config.getPort());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String threadName = "Client-" + clientNumber.getAndIncrement();
                    Thread thread = new Thread(new RequestHandler(clientSocket, fileManager), threadName);
                    thread.start();
                    LOGGER.fine("Started thread for client " + threadName);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error handling client connection: ", e);
                    if (config.isDebugging()) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting server on port " + config.getPort() + ": ", e);
            if (config.isDebugging()) {
                e.printStackTrace();
            }
        }
    }
}
