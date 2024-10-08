package SimpleWebServer;

import java.io.IOException;


@FunctionalInterface
public interface RouteHandler {
    Response handle(Request request, ContextHolder contextHolder, String sessionId) throws IOException;
}