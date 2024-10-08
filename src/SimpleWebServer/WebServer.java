package SimpleWebServer;// WebServer.java
import SimpleWebServer.Context.ContextHolder;
import SimpleWebServer.TempleteEngine.TemplateEngine;

import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer {
    private int port;
    private Map<String, Route> routes;
    // TemplateEngine are used to render templates with data
    // this can be made more advanced by adding support for other template rendering implementations
    // to support more tags and features
    private TemplateEngine templateEngine;
    // ContextHolder can be used to store and retrieve data across multiple requests
    // It can be used to actually store the data in a database or other storage
    // and retrieve it when needed
    // it can also be made more advanced so it receives session id and other data
    // and can be used to store and retrieve user-specific data
    private ContextHolder contextHolder;
    // Sessions are used to store user session data
    // this can be used to store user-specific data across multiple requests
    // this is a simple implementation of session management
    // but you can use more advanced techniques and start developing auth and other things from here
    // you can also use a session manager to manage the sessions and store them in a database or other storage
    // also implement session timeout and other features
    private Map<String, String> sessions;

    public WebServer(int port, TemplateEngine templateEngine, ContextHolder contextHolder) {
        this.port = port;

        this.routes = new HashMap<>();
        this.templateEngine = templateEngine;
        this.contextHolder = contextHolder;
        this.sessions = new HashMap<>();
    }

    public void addRoute(String path, Route handler) {
        routes.put(path, handler);
    }

    // This method starts the server and listens for incoming client requests
    // for each client request, it creates a new thread to handle the request
    // this can be made more advanced by using a thread pool to manage the threads
    // and limit the number of concurrent requests
    // you can use an executor service to manage the threads and handle the requests
    // or green threads like Quasar or Project Loom to make it more efficient
    // also you can add support for SSL/TLS, logging, and other features
    // to make it more robust and secure
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    // This method handles the client request
    // it reads the request, parses it, and sends it to the appropriate route handler
    // then sends the response back to the client
    // This can be made more advanced by make it pass through a middleware pipeline or filter chain
    // before it reaches the route handler
    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            Request request = parseRequest(in);
            if (request == null) {
                System.out.println("Received empty request. Closing connection.");
                return;
            }

            String sessionId = getSessionId(request);

            Route handler = routes.get(request.getPath());
            if (handler != null) {
                // Pass the session, context holder, and session id to the route handler.
                Response response = handler.handle(request, contextHolder, sessionId);
                sendResponse(out, response, sessionId);
            } else {
                sendResponse(out, new Response(404, "Not Found", "404 Not Found"), sessionId);
            }
        } catch (IOException e) {
            System.out.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    // This session id can be used to identify the user session
    // this can be used by the route handlers to store and retrieve user-specific data
    // this is a simple implementation of session management
    // but you can use more advanced techniques and start developing auth and other things from here
    private String getSessionId(Request request) {
        String sessionId = null;
        String cookieHeader = request.getHeaders().get("Cookie");
        if (cookieHeader != null) {
            sessionId = Arrays.stream(cookieHeader.split(";"))
                    .map(String::trim)
                    .filter(c -> c.startsWith("sessionId="))
                    .map(c -> c.split("=")[1])
                    .findFirst()
                    .orElse(null);
        }
        if (sessionId == null || !sessions.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            sessions.put(sessionId, "");
        }
        return sessionId;
    }

    // This method parses the incoming request
    // and returns a Request object
    // this can be made more advanced by adding support for query parameters, etc.
    // also you can add support for other HTTP methods
    // and other request properties and headers before it
    // reaches the route handler
    private Request parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            System.out.println("Invalid request line: " + requestLine);
            return null;
        }

        String method = requestParts[0];
        String path = requestParts[1];

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0 && colonIndex < headerLine.length() - 1) {
                String key = headerLine.substring(0, colonIndex).trim();
                String value = headerLine.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        StringBuilder requestBody = new StringBuilder();
        while (in.ready()) {
            requestBody.append((char) in.read());
        }

        return new Request(method, path, headers, requestBody.toString());
    }


    // This method sends the response back to the client in the HTTP format
    // this can be made more advanced by adding support for other response properties
    // and other HTTP status codes
    // also you can add support for other content types
    // and other response properties
    private void sendResponse(PrintWriter out, Response response, String sessionId) {
        out.println("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMessage());
        out.println("Set-Cookie: sessionId=" + sessionId + "; HttpOnly; SameSite=Strict");
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            out.println(header.getKey() + ": " + header.getValue());
        }
        out.println("Content-Type: text/html");
        out.println();
        out.println(response.getBody());
    }
}