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
        String clientIP = clientSocket.getInetAddress().getHostAddress();
        LOGGER.info("Client connected: "+ clientIP);
        try (
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)) {
                    
                    
            HttpRequest request = HttpRequest.parse(reader);
            if (request == null) {
                LOGGER.warning(clientIP + " - Received an empty request or client opened the connection.");
                return;
            }
            HttpResponse response = new HttpResponse();

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
                handleUnsupportedMethod(request, response, clientIP);
                    break;
            }
            LOGGER.info(clientIP + " - " + request.getMethod() + " response: " + response.getStatusCode() + " " + response.getReasonPhrase());
            // Send the appropriate HTTP response back to the client
            sendResponse(writer, response);

        } catch (Exception e) {
            LOGGER.severe(clientIP + " - Error processing request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                LOGGER.fine(clientIP + " - Client socket closed successfully.");
            } catch (IOException e) {
                LOGGER.warning(clientIP + " - Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleUnsupportedMethod(HttpRequest request, HttpResponse response, String clientIP) {
        response.setHttpVersion(request.getHttpVersion());
        response.setStatusCode(501);
        response.setReasonPhrase("Not Implemented");
        response.setBody("Method not implemented");
        LOGGER.warning(clientIP + " - Received a request for an unimplemented method: " + request.getMethod());
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
