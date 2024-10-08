package SimpleWebServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTemplateEngine implements TemplateEngine {
    private String templateDir;

    public SimpleTemplateEngine(String templateDir) {
        this.templateDir = templateDir;
    }

    @Override
    public String render(String templateName, Map<String, Object> context) throws IOException {
        String templateContent = new String(Files.readAllBytes(Paths.get(templateDir, templateName)));

        // Handle #each directives
        Pattern eachPattern = Pattern.compile("\\{\\{#each\\s+(\\w+)\\}\\}(.+?)\\{\\{/each\\}\\}", Pattern.DOTALL);
        Matcher eachMatcher = eachPattern.matcher(templateContent);
        StringBuffer sb = new StringBuffer();
        while (eachMatcher.find()) {
            String listName = eachMatcher.group(1);
            String itemTemplate = eachMatcher.group(2);
            List<?> items = (List<?>) context.get(listName);
            if (items != null) {
                StringBuilder replacement = new StringBuilder();
                for (Object item : items) {
                    replacement.append(itemTemplate.replace("{{this}}", item.toString()));
                }
                eachMatcher.appendReplacement(sb, replacement.toString());
            }
        }
        eachMatcher.appendTail(sb);
        templateContent = sb.toString();

        // Handle simple variable replacements
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            templateContent = templateContent.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }

        return templateContent;
    }
}
