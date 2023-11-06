import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final FileManager fileManager;
    private final static Logger LOGGER = Logger.getLogger(httpfs.class.getName());

    public RequestHandler(Socket clientSocket, FileManager fileManager) {
        this.clientSocket = clientSocket;
        this.fileManager = fileManager;
    }

    public void run() {
        try (
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)) {

            HttpRequest request = HttpRequest.parse(reader);
            if (request == null) {
                LOGGER.info("Received an empty request or client opened the connection.");
                return;
            }
            HttpResponse response = new HttpResponse();

            // Determine the request type (GET, POST, etc.)
            switch (request.getMethod()) {
                case "GET":
                    LOGGER.info("Received a GET request.");
                    handleGetRequest(request, response);
                    break;
                case "POST":
                LOGGER.info("Received a POST request.");
                    handlePostRequest(request, response);
                    break;
                // Add cases for other HTTP methods if needed
                default:
                LOGGER.info("Received a request for an unimplemented method: " + request.getMethod());
                    response.setHttpVersion(request.getHttpVersion());
                    response.setStatusCode(501);
                    response.setReasonPhrase("Not Implemented");
                    response.setBody("Method not implemented");
                    LOGGER.warning("Received a request for an unimplemented method: " + request.getMethod());
                    break;
            }
            LOGGER.info("Sending response: " + response.toHttpString());
            // Send the appropriate HTTP response back to the client
            sendResponse(writer, response);

        } catch (Exception e) {
            LOGGER.severe("Error processing request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                LOGGER.info("Client socket closed successfully.");
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
            LOGGER.info("File " + fileName + " read successfully for GET request.");
        } else {
            response.setHttpVersion(request.getHttpVersion());
            response.setStatusCode(404);
            response.setReasonPhrase("Not Found");
            response.setBody("File not found");
            LOGGER.warning("File " + fileName + " not found for GET request.");
        }
    }

    private void handlePostRequest(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = request.getFilePath();
        String content = request.getBody();
        fileManager.writeFile(fileName, content);
        LOGGER.info("File " + fileName + " written successfully for POST request.");
        response.setHttpVersion(request.getHttpVersion());
        response.setStatusCode(201);
        response.setReasonPhrase("Created");
        response.setBody("File created");
    }

    private void sendResponse(PrintWriter writer, HttpResponse response) {
        // LOGGER.info("Sending response, status code: " + response.getStatusCode() + ", reason phrase: " + response.getReasonPhrase());
        writer.println(response.toHttpString());
        writer.flush();
    }
}
