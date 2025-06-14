package pdfproject.reportutil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlEmbedder {

    public static void main(String[] args) throws IOException {
        // Path to your HTML file
        Path htmlPath = Paths.get("E:\\ReportProject 2.0\\index.html");
        Path baseFolder = htmlPath.getParent(); // <-- base for resolving relative CSS/JS

        // Read HTML content
        String html = Files.readString(htmlPath, StandardCharsets.UTF_8);

        // Inline CSS and JS, passing base folder
        html = inlineResources(html, baseFolder, "link", "href", "rel=\"stylesheet\"", "style", "text/css");
        html = inlineResources(html, baseFolder, "script", "src", null, "script", "text/javascript");

        // Escape for Java string literal
        String escapedHtml = html
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");

        // Java class content
        String classContent =
                "package pdfproject.reportutil;\n\n" +
                        "public class ReportHtml {\n" +
                        "    public static final String REPORT_HTML = \"" + escapedHtml + "\";\n" +
                        "}";

        // Resolve output location
        String packagePath = HtmlEmbedder.class.getPackageName().replace('.', File.separatorChar);
        Path outputDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", packagePath);
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve("ReportHtml.java");
        Files.writeString(outputFile, classContent, StandardCharsets.UTF_8);

        System.out.println("✅ ReportHtml.java generated at: " + outputFile.toAbsolutePath());
    }

    private static String inlineResources(String html, Path baseFolder, String tag, String attr,
                                          String filterAttr, String wrapperTag, String typeAttrValue) throws IOException {
        Pattern pattern = Pattern.compile("<" + tag + "[^>]*" + attr + "=\"([^\"]+)\"[^>]*>");
        Matcher matcher = pattern.matcher(html);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String tagHtml = matcher.group(0);
            String relativePath = matcher.group(1);

            if (filterAttr != null && !tagHtml.contains(filterAttr)) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(tagHtml));
                continue;
            }

            Path resourcePath = baseFolder.resolve(relativePath).normalize();
            if (!Files.exists(resourcePath)) {
                System.err.println("⚠ Missing file: " + resourcePath);
                matcher.appendReplacement(result, Matcher.quoteReplacement(tagHtml));
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
