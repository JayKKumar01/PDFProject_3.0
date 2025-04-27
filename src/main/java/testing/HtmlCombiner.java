package testing;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class HtmlCombiner {

    private static String base = "";

    public static void main(String[] args) {
        // Example usage: Replace with your HTML file path
        String htmlFilePath = "E:\\ReportProject 2.0\\index.html";
        base = htmlFilePath.substring(0,htmlFilePath.lastIndexOf("\\")+1);
        String outputFilePath = "combined.html";  // Output file path
        
        // Generate the combined HTML content
        String combinedHtml = generateCombinedHtml(htmlFilePath);
        
        // Save the combined HTML to a new file
        saveCombinedHtml(outputFilePath, combinedHtml);
    }

    public static String generateCombinedHtml(String htmlFilePath) {
        StringBuilder combinedHtml = new StringBuilder();
        try {
            // Read the HTML file
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), "UTF-8");

            // Extract and embed CSS
            List<String> cssLinks = extractCssLinks(htmlContent);
            String cssContent = combineCss(cssLinks);
            htmlContent = embedCssInHtml(htmlContent, cssContent);

            // Extract and embed JS
            List<String> jsLinks = extractJsLinks(htmlContent);
            String jsContent = combineJs(jsLinks);
            htmlContent = embedJsInHtml(htmlContent, jsContent);

            combinedHtml.append(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return combinedHtml.toString();
    }

    // Extract CSS file links from the HTML content
    private static List<String> extractCssLinks(String htmlContent) {
        List<String> cssLinks = new ArrayList<>();
        Pattern cssPattern = Pattern.compile("<link[^>]+rel=[\"']stylesheet[\"'][^>]+href=[\"']([^\"']+)[\"'][^>]*>");
        Matcher matcher = cssPattern.matcher(htmlContent);

        while (matcher.find()) {
            cssLinks.add(base+matcher.group(1));
        }
        return cssLinks;
    }

    // Combine all CSS files into a single string
    private static String combineCss(List<String> cssLinks) {
        StringBuilder combinedCss = new StringBuilder();

        for (String cssLink : cssLinks) {
            try {
                // Read the content of each CSS file
                String cssContent = new String(Files.readAllBytes(Paths.get(cssLink)), "UTF-8");
                combinedCss.append(cssContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return combinedCss.toString();
    }

    // Embed combined CSS into the HTML
    private static String embedCssInHtml(String htmlContent, String combinedCss) {
        String cssTag = "<style>\n" + combinedCss + "\n</style>";
        return htmlContent.replaceFirst("<head>", "<head>" + cssTag);
    }

    // Extract JS file links from the HTML content
    private static List<String> extractJsLinks(String htmlContent) {
        List<String> jsLinks = new ArrayList<>();
        Pattern jsPattern = Pattern.compile("<script[^>]+src=[\"']([^\"']+)[\"'][^>]*>");
        Matcher matcher = jsPattern.matcher(htmlContent);

        while (matcher.find()) {
            jsLinks.add(base+matcher.group(1));
        }
        return jsLinks;
    }

    // Combine all JS files into a single string
    private static String combineJs(List<String> jsLinks) {
        StringBuilder combinedJs = new StringBuilder();

        for (String jsLink : jsLinks) {
            try {
                // Read the content of each JS file
                String jsContent = new String(Files.readAllBytes(Paths.get(jsLink)), "UTF-8");
                combinedJs.append(jsContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return combinedJs.toString();
    }

    // Embed combined JS into the HTML
    private static String embedJsInHtml(String htmlContent, String combinedJs) {
        String jsTag = "<script>\n" + combinedJs + "\n</script>";
        // Escaping the curly braces to treat them literally
        return htmlContent.replaceFirst("</body>", Matcher.quoteReplacement(jsTag) + "\n</body>");
    }


    // Save the combined HTML content to a new file
    private static void saveCombinedHtml(String outputFilePath, String combinedHtml) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
            writer.write(combinedHtml);
            System.out.println("Combined HTML saved to: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
