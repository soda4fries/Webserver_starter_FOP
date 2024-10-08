package SimpleWebServer.Context;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleContextHolder implements ContextHolder {
    private ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();

    @Override
    public void set(String key, Object value) {
        context.put(key, value);
    }

    @Override
    public Object get(String key) {
        return context.get(key);
    }

    @Override
    public void remove(String key) {
        context.remove(key);
    }
}
