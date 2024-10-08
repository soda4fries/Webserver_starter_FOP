package Application;

import SimpleWebServer.*;
import SimpleWebServer.Context.SimpleContextHolder;
import SimpleWebServer.TempleteEngine.SimpleTemplateEngine;
import SimpleWebServer.Context.ContextHolder;
import SimpleWebServer.TempleteEngine.TemplateEngine;

import java.io.IOException;
import java.util.*;

public class EmailApplication {
    public static void main(String[] args) throws IOException {
        TemplateEngine templateEngine = new SimpleTemplateEngine("Templetes");
        ContextHolder contextHolder = new SimpleContextHolder();
        WebServer server = new WebServer(8080, templateEngine, contextHolder);

        // Initialize email storage
        contextHolder.set("allEmails", new ArrayList<String>());

        // Root route handler as lambda expression
        server.addRoute("/", (request, contextHolder_passed, sessionId) -> {
            if (request.getMethod().equals("GET")) {
                String email = (String) contextHolder_passed.get("email_" + sessionId);
                Map<String, Object> templateContext = new HashMap<>();
                templateContext.put("email", email != null ? email : "Not set");
                String body = templateEngine.render("index.html", templateContext);
                return new Response(200, "OK", body);
            } else if (request.getMethod().equals("POST")) {
                String email = request.getBody().split("=")[1];
                contextHolder_passed.set("email_" + sessionId, email);
                List<String> allEmails = (List<String>) contextHolder_passed.get("allEmails");
                if (!allEmails.contains(email)) {
                    allEmails.add(email);
                }
                return new Response(302, "Found", "<html><body>Redirecting...</body></html>")
                        .addHeader("Location", "/");
            } else {
                return new Response(405, "Method Not Allowed", "Only GET and POST methods are allowed");
            }
        });

        // All-emails route handler as concrete implementation
        server.addRoute("/all-emails", new AllEmailRoute(templateEngine));

        server.start();
    }
}

