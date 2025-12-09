package pdfproject.utils;

import pdfproject.models.MapModel;
import pdfproject.reportutil.ReportHtml;
import org.apache.commons.math3.util.Pair;

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
            List<List<Pair<String, String>>> sourceTexts = model.getSourceTexts();
            List<List<Pair<String, String>>> targetTexts = model.getTargetTexts();

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
                for (int i = 0; i < alignmentImages.size(); i++) {
                    List<String> alignment = alignmentImages.get(i);
                    if (alignment != null && alignment.size() >= 2) {
                        String sourceImg = sanitizePath(alignment.get(0));
                        String targetImg = sanitizePath(alignment.get(1));

                        // get corresponding source/target pairs (if available)
                        List<Pair<String, String>> srcPairs = null;
                        List<Pair<String, String>> tgtPairs = null;

                        if (sourceTexts != null && i < sourceTexts.size()) {
                            srcPairs = sourceTexts.get(i);
                        }
                        if (targetTexts != null && i < targetTexts.size()) {
                            tgtPairs = targetTexts.get(i);
                        }

                        jsContent.append("            {\n");
                        jsContent.append("                source: {\n");
                        jsContent.append("                    image: \"").append(sourceImg).append("\",\n");
                        jsContent.append("                    pairs: ").append(jsPairsFromPairList(srcPairs)).append("\n");
                        jsContent.append("                },\n");
                        jsContent.append("                target: {\n");
                        jsContent.append("                    image: \"").append(targetImg).append("\",\n");
                        jsContent.append("                    pairs: ").append(jsPairsFromPairList(tgtPairs)).append("\n");
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
            sb.append("\"").append(escapeForJs(sanitized)).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Convert List<Pair<String,String>> to JS array of arrays: [["orig","corr"], ...]
    private static String jsPairsFromPairList(List<Pair<String, String>> pairs) {
        if (pairs == null || pairs.isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < pairs.size(); i++) {
            Pair<String, String> p = pairs.get(i);
            String orig = p == null || p.getFirst() == null ? "" : p.getFirst();
            String corr = p == null || p.getSecond() == null ? "" : p.getSecond();

            sb.append("[\"")
                    .append(escapeForJs(orig))
                    .append("\", \"")
                    .append(escapeForJs(corr))
                    .append("\"]");

            if (i < pairs.size() - 1) sb.append(", ");
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

    // Escape string content for safe JS double-quoted string literal
    private static String escapeForJs(String s) {
        if (s == null) return "";
        String r = s;
        // Escape backslashes first
        r = r.replace("\\", "\\\\");
        // Escape double quotes
        r = r.replace("\"", "\\\"");
        // Replace newlines/tabs with escaped sequences
        r = r.replace("\r\n", "\\n").replace("\n", "\\n").replace("\t", "\\t");
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
