import java.io.*;
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
        try (
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)) {
                    
            HttpRequest request = HttpRequest.parse(reader);
            HttpResponse response = new HttpResponse();
            LOGGER.fine("URL: " + request.getURL());
            LOGGER.fine("FilePath: " + request.getFilePath());

            // Debugging info
            if (isDebugging) {
                LOGGER.fine("Request: " + request.toHttpRequestString());
            }

            // Determine the request type (GET, POST, etc.)
            switch (request.getMethod()) {
                case "GET":
                    handleGetRequest(request, response);
                    break;
                case "POST":
                    handlePostRequest(request, response);
                    break;
                // Add cases for other HTTP methods if needed
                default:
                    response.setHttpVersion(request.getHttpVersion());
                    response.setStatusCode(501);
                    response.setReasonPhrase("Not Implemented");
                    response.setBody("Method not implemented");
                    break;
            }

            // Send the appropriate HTTP response back to the client
            sendResponse(writer, response);

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

    private void handleGetRequest(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = request.getFilePath();
        if (fileManager.fileExists(fileName)) {
            String fileContent = fileManager.readFile(fileName);
            response.setHttpVersion(request.getHttpVersion());
            response.setStatusCode(200);
            response.setReasonPhrase("OK");
            response.setBody(fileContent);
        } else {
            response.setHttpVersion(request.getHttpVersion());
            response.setStatusCode(404);
            response.setReasonPhrase("Not Found");
            response.setBody("File not found");
        }
    }

    private void handlePostRequest(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = request.getFilePath();
        String content = request.getBody();
        fileManager.writeFile(fileName, content);
        response.setHttpVersion(request.getHttpVersion());
        response.setStatusCode(201);
        response.setReasonPhrase("Created");
        response.setBody("File created");
    }

    private void sendResponse(PrintWriter writer, HttpResponse response) {
        writer.println(response.toHttpString());
        writer.flush();
    }
}
