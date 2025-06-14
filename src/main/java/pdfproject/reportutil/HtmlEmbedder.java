package pdfproject.reportutil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlEmbedder {

    public static void main(String[] args) throws IOException {
        Path htmlPath = Paths.get("E:\\ReportProject 2.0\\index.html");
        Path baseFolder = htmlPath.getParent();

        String html = Files.readString(htmlPath, StandardCharsets.UTF_8);

        // Inline CSS and JS (but skip specific scripts like 'script/data-map.js')
        html = inlineResources(html, baseFolder, "link", "href", "rel=\"stylesheet\"", "style", "text/css", null);
        html = inlineResources(html, baseFolder, "script", "src", null, "script", "text/javascript", "scripts/data-map.js");

        String escapedHtml = html
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");

        String classContent =
                "package pdfproject.reportutil;\n\n" +
                        "public class ReportHtml {\n" +
                        "    public static final String REPORT_HTML = \"" + escapedHtml + "\";\n" +
                        "}";

        String packagePath = HtmlEmbedder.class.getPackageName().replace('.', File.separatorChar);
        Path outputDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", packagePath);
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("ReportHtml.java"), classContent, StandardCharsets.UTF_8);

        System.out.println("✅ ReportHtml.java generated at: " + outputDir.resolve("ReportHtml.java").toAbsolutePath());
    }

    private static String inlineResources(String html, Path baseFolder, String tag, String attr,
                                          String filterAttr, String wrapperTag, String typeAttrValue,
                                          String skipPath) throws IOException {

        Pattern pattern = Pattern.compile(
                "<" + tag + "\\b([^>]*?)\\s" + attr + "=\"([^\"]+)\"([^>]*)>(</" + tag + ">)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String attrValue = matcher.group(2);  // path from the HTML src/href attribute

            if (filterAttr != null && !fullMatch.contains(filterAttr)) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullMatch));
                continue;
            }

            // ✅ Do not inline the specified JS path (e.g., script/data-map.js)
            if (skipPath != null && attrValue.replace("\\", "/").equals(skipPath)) {
                System.out.println("⏭ Skipping inlining for: " + attrValue);
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullMatch));
                continue;
            }

            Path resourcePath = baseFolder.resolve(attrValue).normalize();
            if (!Files.exists(resourcePath)) {
                System.err.println("⚠ Missing file: " + resourcePath);
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullMatch));
                continue;
            }

            String content = Files.readString(resourcePath, StandardCharsets.UTF_8);
            String inlineTag = "<" + wrapperTag + " type=\"" + typeAttrValue + "\">\n"
                    + content + "\n</" + wrapperTag + ">";

            matcher.appendReplacement(result, Matcher.quoteReplacement(inlineTag));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
