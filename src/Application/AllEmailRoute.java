package Application;

import SimpleWebServer.Context.ContextHolder;
import SimpleWebServer.Request;
import SimpleWebServer.Response;
import SimpleWebServer.Route;
import SimpleWebServer.TempleteEngine.TemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Concrete implementation of the all-emails route handler
class AllEmailRoute implements Route {
    private final TemplateEngine templateEngine;

    public AllEmailRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Response handle(Request request, ContextHolder contextHolder, String sessionId) throws IOException {
        if (request.getMethod().equals("GET")) {
            Map<String, Object> templateContext = new HashMap<>();
            templateContext.put("emails", contextHolder.get("allEmails"));
            String body = templateEngine.render("all_emails.html", templateContext);
            return new Response(200, "OK", body);
        } else {
            return new Response(405, "Method Not Allowed", "Only GET method is allowed");
        }
    }
}
