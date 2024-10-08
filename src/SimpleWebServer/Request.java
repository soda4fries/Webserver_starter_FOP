package SimpleWebServer;

import java.util.Map;

//This is used to parse the request data
//The parser can be improped then this class can be updated to handle the new parser
//This can be used to parse the request data and pass it to the route handler
//or other components to handle the request
// can be converted to record in Java 16
public class Request {
    private String method;
    private String path;
    private Map<String, String> headers;
    private String body;

    public Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
}
