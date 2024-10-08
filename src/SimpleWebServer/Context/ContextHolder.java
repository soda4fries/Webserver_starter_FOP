package SimpleWebServer.Context;

// Context holder interface to store and retrieve data
// This can be implemented by concrete classes to store and retrieve data
// The data can be stored in memory, database, or other storage
// This can be used to store and retrieve data like session data, user data, etc.
// The data can be stored as key-value pairs
// The context holder can be improved to be more dynamic and manage data using a improved api
// This is shared accross multiple requests and at current implementation it is stored in memory
// it is also shared by all users and can to retrieve user-specific data
public interface ContextHolder {
    void set(String key, Object value);
    Object get(String key);
    void remove(String key);
}
