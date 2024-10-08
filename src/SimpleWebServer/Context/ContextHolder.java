package SimpleWebServer.Context;


public interface ContextHolder {
    void set(String key, Object value);
    Object get(String key);
    void remove(String key);
}
