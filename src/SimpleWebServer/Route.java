package SimpleWebServer;

import SimpleWebServer.Context.ContextHolder;

import java.io.IOException;

// Route interface to handle client requests
// This can be implemented by concrete classes to handle different routes
// The string sessionId can be used to store and retrieve user-specific data
// from the context holder
// But you can make the context holder more advanced to handle sessions and other data
// This can be used to implement session management, auth, and other features
@FunctionalInterface
public interface Route {
    Response handle(Request request, ContextHolder contextHolder, String sessionId) throws IOException;
}