package SimpleWebServer;

import java.io.IOException;
import java.util.Map;

public interface TemplateEngine {
    String render(String templateName, Map<String, Object> context) throws IOException;
}
