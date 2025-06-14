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
        Path baseFolder = htmlPath.getParent(); // For resolving relative CSS/JS paths

        // Read raw HTML
        String html = Files.readString(htmlPath, StandardCharsets.UTF_8);

        // Inline CSS and JS (skip "data-map.js" but keep tag)
        html = inlineResources(html, baseFolder, "link", "href", "rel=\"stylesheet\"", "style", "text/css", null);
        html = inlineResources(html, baseFolder, "script", "src", null, "script", "text/javascript", "data-map.js");

        // Escape for Java string
        String escapedHtml = html
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");

        // Generate Java class content
        String classContent =
                "package pdfproject.reportutil;\n\n" +
                        "public class ReportHtml {\n" +
                        "    public static final String REPORT_HTML = \"" + escapedHtml + "\";\n" +
                        "}";

        // Write to src/main/java/pdfproject/reportutil/ReportHtml.java
        String packagePath = HtmlEmbedder.class.getPackageName().replace('.', File.separatorChar);
        Path outputDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", packagePath);
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("ReportHtml.java");
        Files.writeString(outputFile, classContent, StandardCharsets.UTF_8);

        System.out.println("✅ ReportHtml.java generated at: " + outputFile.toAbsolutePath());
    }

    private static String inlineResources(String html, Path baseFolder, String tag, String attr,
                                          String filterAttr, String wrapperTag, String typeAttrValue,
                                          String excludeFilename) throws IOException {
        Pattern pattern = Pattern.compile("<" + tag + "[^>]*" + attr + "=\"([^\"]+)\"[^>]*>");
        Matcher matcher = pattern.matcher(html);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String relativePath = matcher.group(1);

            // Skip if tag does not match filter
            if (filterAttr != null && !fullTag.contains(filterAttr)) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullTag));
                continue;
            }

            // Skip specific JS file but keep tag
            if (excludeFilename != null && relativePath.endsWith(excludeFilename)) {
                System.out.println("⏭ Skipping inlining but keeping tag: " + relativePath);
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullTag));
                continue;
            }

            Path resourcePath = baseFolder.resolve(relativePath).normalize();
            if (!Files.exists(resourcePath)) {
                System.err.println("⚠ Missing file: " + resourcePath);
                matcher.appendReplacement(result, Matcher.quoteReplacement(fullTag));
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
