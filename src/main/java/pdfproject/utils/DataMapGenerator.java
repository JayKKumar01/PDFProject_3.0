package pdfproject.utils;

import pdfproject.models.MapModel;
import pdfproject.reportutil.ReportHtml;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DataMapGenerator {

    public static void generateDataMapJs(List<MapModel> models, String outputDir) {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        String filePath = outputDir + File.separator + "data-map.js";

        StringBuilder jsContent = new StringBuilder();
        jsContent.append("// Map to store the item image data\n");
        jsContent.append("const itemImageMap = new Map([\n");

        int itemNumber = 1;

        for (MapModel model : models) {
            List<List<String>> validationImages = model.getContentImages();
            List<List<String>> alignmentImages = model.getAlignmentImages();

            String key = model.getKey();
            if (key == null || key.trim().isEmpty()) {
                key = "Item_" + itemNumber;
            }

            jsContent.append("    [")
                    .append(itemNumber)
                    .append(", {\n");

            jsContent.append("        name: \"").append(escapeQuotes(key)).append("\",\n");

            // Validation images
            jsContent.append("        validationImages: [\n");
            if (validationImages != null) {
                for (List<String> validation : validationImages) {
                    jsContent.append("            ").append(jsArraySanitized(validation)).append(",\n");
                }
            }
            jsContent.append("        ],\n");

            // Alignment images
            jsContent.append("        alignmentImages: [\n");
            if (alignmentImages != null) {
                for (List<String> alignment : alignmentImages) {
                    jsContent.append("            ").append(jsArraySanitized(alignment)).append(",\n");
                }
            }
            jsContent.append("        ],\n");

            // Prodigy validation (one block per alignment row/page)
            jsContent.append("        prodigyValidation: [\n");
            if (alignmentImages != null) {
                for (List<String> alignment : alignmentImages) {
                    if (alignment != null && alignment.size() >= 2) {
                        String sourceImg = sanitizePath(alignment.get(0));
                        String targetImg = sanitizePath(alignment.get(1));

                        jsContent.append("            {\n");
                        jsContent.append("                source: {\n");
                        jsContent.append("                    image: \"").append(sourceImg).append("\",\n");
                        jsContent.append("                    pairs: []\n");
                        jsContent.append("                },\n");
                        jsContent.append("                target: {\n");
                        jsContent.append("                    image: \"").append(targetImg).append("\",\n");
                        jsContent.append("                    pairs: []\n");
                        jsContent.append("                }\n");
                        jsContent.append("            },\n");
                    }
                }
            }
            jsContent.append("        ]\n");

            jsContent.append("    }],\n");

            itemNumber++;
        }

        jsContent.append("]);\n\n");
        jsContent.append("console.log(itemImageMap);\n");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsContent.toString());
            System.out.println("data-map.js generated successfully!");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        createReport(outputDir);
        System.out.println("File Generated Successfully!");
    }

    // Converts list to JS array, sanitizing each element to remove stray quotes
    private static String jsArraySanitized(List<String> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            String sanitized = sanitizePath(list.get(i));
            sb.append("\"").append(sanitized).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Remove leading/trailing quotes and unescape any \" sequences
    private static String sanitizePath(String s) {
        if (s == null) return "";
        // Unescape escaped quotes first
        String r = s.replace("\\\"", "\"");
        // Remove any surrounding quotes (one or many)
        r = r.replaceAll("^\"+","").replaceAll("\"+$","");
        // Trim whitespace
        r = r.trim();
        return r;
    }

    // Escape double quotes inside the name (unlikely but safe)
    private static String escapeQuotes(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    private static void createReport(String outputDir) {
        File destinationFile = new File(outputDir, "report.html");
        try {
            Files.writeString(destinationFile.toPath(), ReportHtml.REPORT_HTML);
            Desktop.getDesktop().browse(destinationFile.toURI());
        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
        }
    }
}
