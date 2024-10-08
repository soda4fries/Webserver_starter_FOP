package SimpleWebServer;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int statusCode;
    private String statusMessage;
    private String body;
    private Map<String, String> headers;

    public Response(int statusCode, String statusMessage, String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
        this.headers = new HashMap<>();
    }

    public Response addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public int getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public String getBody() { return body; }
    public Map<String, String> getHeaders() { return headers; }
}
