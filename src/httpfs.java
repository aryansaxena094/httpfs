import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class httpfs {

    private static final Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public static void main(String[] args) {
        LOGGER.setLevel(Level.WARNING); // Default Level is WARNING, so we don't debug by default
        // This is for testing
        // Scanner sc = new Scanner(System.in);
        // String line = sc.nextLine();
        try {
            String line = "-v -p 8080 -d thedirectorypath";
            String[] args2 = line.split(" ");
            ServerConfig config = new ServerConfig(args2);
            if (config.isDebugging()) {
                LOGGER.setLevel(Level.FINE);
            }
            // This is where we start the server
            startServer(config);
            LOGGER.info("Starting server on port " + config.getPort());
            LOGGER.info("Serving files from directory " + config.getDirectory());
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Error parsing arguments: " + e.getMessage());
        }
    }

    private static void startServer(ServerConfig config) {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            LOGGER.info("Server started");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.fine("New connection accepted");

                    // Handle the client request in a separate thread or by a dedicated handler
                    // For example:
                    // Thread thread = new Thread(new ClientHandler(clientSocket, config));
                    // thread.start();


                    
                } catch (Exception e) {
                    // This is where we handle client errors
                    LOGGER.severe("Error handling client connection: " + e.getMessage());
                    if(config.isDebugging()){
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
