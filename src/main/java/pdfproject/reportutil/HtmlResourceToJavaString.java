package pdfproject.reportutil;

import java.io.*;
import java.nio.file.*;

public class HtmlResourceToJavaString {

    public static void main(String[] args) throws IOException {
        // Read from InputStream (e.g. embedded JAR resource)
        try (InputStream inputStream = HtmlResourceToJavaString.class.getResourceAsStream("/assets/report.html")) {
            if (inputStream == null) {
                System.err.println("Resource not found: /assets/report.html");
                return;
            }

            String htmlContent = new String(inputStream.readAllBytes());

            // Escape for Java string
            String escaped = escapeForJavaString(htmlContent);

            // Determine output directory: same as package folder
            String packagePath = HtmlResourceToJavaString.class.getPackageName().replace('.', File.separatorChar);
            Path sourceRoot = Paths.get(System.getProperty("user.dir"), "src", "main", "java");
            Path targetDir = sourceRoot.resolve(packagePath);
            Files.createDirectories(targetDir);  // Ensure the folder exists

            // Final output path
            Path outputPath = targetDir.resolve("ReportHtml.java");

            // Write the file
            String classContent = generateJavaClass("ReportHtml", "REPORT_HTML", escaped);
            Files.writeString(outputPath, classContent);

            System.out.println("ReportHtml.java generated at: " + outputPath.toAbsolutePath());
        }
    }

    private static String escapeForJavaString(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32 || c > 126) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static String generateJavaClass(String className, String variableName, String content) {
        return """
                package pdfproject.reportutil;

                public class %s {
                    public static final String %s = "%s";
                }
                """.formatted(className, variableName, content);
    }
}
