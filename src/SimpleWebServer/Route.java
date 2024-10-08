package SimpleWebServer;

import SimpleWebServer.Context.ContextHolder;

import java.io.IOException;


@FunctionalInterface
public interface Route {
    Response handle(Request request, ContextHolder contextHolder, String sessionId) throws IOException;
}