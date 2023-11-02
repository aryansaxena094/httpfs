import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final boolean isDebugging;
    private final Socket clientSocket;
    private final FileManager fileManager;
    private final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket clientSocket, FileManager fileManager, boolean isDebugging) {
        this.clientSocket = clientSocket;
        this.fileManager = fileManager;
        this.isDebugging = isDebugging;
        LOGGER.setLevel(Level.WARNING);
        if (isDebugging) {
            LOGGER.setLevel(Level.FINE);
        }
    }

    public void run() {
        try {
            // Parse the incoming HTTP request
            // Determine the request type (GET, POST, etc.)
            // Use FileManager to carry out the necessary file operations
            // Send the appropriate HTTP response back to the client
        } catch (Exception e) {
            LOGGER.severe("Error processing request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.warning("Error closing client socket: " + e.getMessage());
            }
        }
    }

}
