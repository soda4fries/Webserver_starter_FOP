package SimpleWebServer.TempleteEngine;

import java.io.IOException;
import java.util.Map;

// Template engine interface to render templates
// This can be implemented by concrete classes to render different types of templates
// The context map contains the data to be rendered in the template
// The render method should return the rendered template as a string
// The api can be improved as context passed here will be string
// since the template will be string
// The context must be filtered by route handler before passing to the template engine
// this api can be improved
public interface TemplateEngine {
    String render(String templateName, Map<String, Object> context) throws IOException;
}
