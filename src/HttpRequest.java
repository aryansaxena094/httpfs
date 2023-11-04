import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String URL;
    private String HttpVersion = "HTTP/1.0";
    private Map<String, String> headers;
    private String body;
    private Map<String, String> query;
    private String cookies;
    private String authenticationInfo;
    private String filePath;


    public HttpRequest() {
        this.headers = new HashMap<>();
        this.query = new HashMap<>();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        this.URL = url;
    }

    public String getHttpVersion() {
        return HttpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        HttpVersion = httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getAuthenticationInfo() {
        return authenticationInfo;
    }

    public void setAuthenticationInfo(String authenticationInfo) {
        this.authenticationInfo = authenticationInfo;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getFilePath() {
        try {
            URI uri = new URI(this.URL);
            return uri.getPath().substring(1); // This removes the leading '/' from the path
        } catch (URISyntaxException e) {
            return null;
        }
    }
    

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setInlineData(String inlineData) {
        this.body = inlineData;
    }

    public String getHost() {
        try {
            URI uri = new URI(this.URL);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public int getPort() {
        try {
            URI uri = new URI(this.URL);
            int port = uri.getPort();
            return port == -1 ? 80 : port;
        } catch (URISyntaxException e) {
            return 80;
        }
    }

    public void extractQueryParams() {
        try {
            URI uri = new URI(this.URL);
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValuePair = param.split("=");
                    if (keyValuePair.length > 1) {
                        this.query.put(keyValuePair[0], keyValuePair[1]);
                    } else if (keyValuePair.length == 1) {
                        this.query.put(keyValuePair[0], "");
                    }
                }
            }
        } catch (URISyntaxException e) {
            System.out.println("Error in handling Parameters");
        }
    }

    public String toHttpRequestString() {
        StringBuilder sb = new StringBuilder();
        try {
            URI uri = new URI(this.URL);
            String path = uri.getPath() == null ? "/" : uri.getPath();
            String query = uri.getQuery();
            sb.append(getMethod()).append(" ").append(path);
            if (query != null) {
                sb.append("?").append(query);
            }
            sb.append(" ").append(getHttpVersion()).append("\r\n");
        } catch (URISyntaxException e) {
            System.out.println("Invalid URL");
            return null;
        }
        sb.append("Host: ").append(getHost()).append("\r\n");
        for (Map.Entry<String, String> header : getHeaders().entrySet()) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        if (getBody() != null && !getBody().isEmpty()) {
            sb.append("Content-Length: ").append(getBody().length()).append("\r\n");
            sb.append("\r\n").append(getBody());
        }
        sb.append("\r\n\r\n");
        return sb.toString();
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();
        String line = reader.readLine();
    
        if (line == null || line.trim().isEmpty()) {
            throw new IOException("Request line is empty");
        }
    
        // Parse the request line
        String[] requestLineParts = line.trim().split("\\s+");
        if (requestLineParts.length < 3) {
            throw new IOException("Invalid request line");
        }
        request.method = requestLineParts[0];
        request.URL = requestLineParts[1];
        request.HttpVersion = requestLineParts[2];
    
        // Parse the headers
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length < 2) {
                throw new IOException("Invalid header line");
            }
            request.headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
    
        // If POST, handle the body
        if ("POST".equalsIgnoreCase(request.method) && request.headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            int bytesRead = 0;
            while (bytesRead < contentLength) {
                int read = reader.read(bodyChars, bytesRead, contentLength - bytesRead);
                if (read == -1) { // End of stream
                    throw new IOException("Not enough data received for Content-Length");
                }
                bytesRead += read;
            }
            request.body = new String(bodyChars);
        }
    
        // Extract query params if any
        // This requires an implementation for extracting query params from the URL
        request.extractQueryParams();
    
        return request;
    }
    
}