package SimpleWebServer;// WebServer.java
import SimpleWebServer.Request;
import SimpleWebServer.Response;

import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer {
    private int port;
    private Map<String, RouteHandler> routes;
    private TemplateEngine templateEngine;
    private ContextHolder contextHolder;
    private Map<String, String> sessions;

    public WebServer(int port, TemplateEngine templateEngine, ContextHolder contextHolder) {
        this.port = port;
        this.routes = new HashMap<>();
        this.templateEngine = templateEngine;
        this.contextHolder = contextHolder;
        this.sessions = new HashMap<>();
    }

    public void addRoute(String path, RouteHandler handler) {
        routes.put(path, handler);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

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

            RouteHandler handler = routes.get(request.getPath());
            if (handler != null) {
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