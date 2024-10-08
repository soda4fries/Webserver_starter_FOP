package Application;// EmailApplication.java
import SimpleWebServer.*;

import java.io.IOException;
import java.util.*;

public class EmailApplication {
    public static void main(String[] args) throws IOException {
        TemplateEngine templateEngine = new SimpleTemplateEngine("Templetes");
        ContextHolder contextHolder = new SimpleContextHolder();
        WebServer server = new WebServer(8080, templateEngine, contextHolder);

        // Initialize email storage
        contextHolder.set("allEmails", new ArrayList<String>());

        server.addRoute("/", (request, context, sessionId) -> {
            if (request.getMethod().equals("GET")) {
                String email = (String) context.get("email_" + sessionId);
                Map<String, Object> templateContext = new HashMap<>();
                templateContext.put("email", email != null ? email : "Not set");
                String body = templateEngine.render("index.html", templateContext);
                return new Response(200, "OK", body);
            } else if (request.getMethod().equals("POST")) {
                String email = request.getBody().split("=")[1];
                context.set("email_" + sessionId, email);
                List<String> allEmails = (List<String>) context.get("allEmails");
                if (!allEmails.contains(email)) {
                    allEmails.add(email);
                }
                return new Response(302, "Found", "<html><body>Redirecting...</body></html>")
                        .addHeader("Location", "/");
            } else {
                return new Response(405, "Method Not Allowed", "Only GET and POST methods are allowed");
            }
        });

        server.addRoute("/all-emails", (request, context, sessionId) -> {
            if (request.getMethod().equals("GET")) {
                Map<String, Object> templateContext = new HashMap<>();
                templateContext.put("emails", context.get("allEmails"));
                String body = templateEngine.render("all_emails.html", templateContext);
                return new Response(200, "OK", body);
            } else {
                return new Response(405, "Method Not Allowed", "Only GET method is allowed");
            }
        });

        server.start();
    }
}