import java.util.HashMap;
import java.util.Map;

class HttpResponse {
    public String HttpVersion;
    public int statusCode;
    public String reasonPhrase;
    public Map<String, String> headers = new HashMap<String, String>();
    public String body;

    public String getHttpVersion() {
        return HttpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        HttpVersion = httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
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

    public boolean isRedirect() {
        return this.getStatusCode() >= 300 && this.getStatusCode() < 400;
    }

    public String getRedirectLocation() {
        return headers.get("Location");
    }
}