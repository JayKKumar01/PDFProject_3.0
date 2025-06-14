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
        // Ensure output directory exists
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // File path for data-map.js
        String filePath = outputDir + File.separator + "data-map.js";

        // StringBuilder to store JavaScript content
        StringBuilder jsContent = new StringBuilder();
        jsContent.append("// Map to store the item image data\n");
        jsContent.append("const itemImageMap = new Map([\n");

        int itemNumber = 1; // Start numbering items from 1

        for (MapModel model : models) {
            List<List<String>> validationImages = model.getContentImages();
            List<List<String>> alignmentImages = model.getAlignmentImages();

            String key = model.getKey();
            if (key == null || key.trim().isEmpty()){
                key = "Item "+itemNumber;
            }

            jsContent.append("    [\"").append(key).append("\", {\n");

            // Validation images
            jsContent.append("        validationImages: [\n");
            for (List<String> validation : validationImages) {
                jsContent.append("            ").append(validation.toString()).append(",\n");
            }
            jsContent.append("        ],\n");

            // Alignment images
            jsContent.append("        alignmentImages: [\n");
            for (List<String> alignment : alignmentImages) {
                jsContent.append("            ").append(alignment.toString()).append(",\n");
            }
            jsContent.append("        ]\n");

            jsContent.append("    }],\n");

            itemNumber++; // Increment for next item
        }

        jsContent.append("]);\n\n");
        jsContent.append("// Example: Log the map to check the result\n");
        jsContent.append("console.log(itemImageMap);\n");

        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsContent.toString());
            System.out.println("data-map.js generated successfully!");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        // Copy predefined files
        createReport(outputDir);

        System.out.println("File Generated Successfully!");
    }

    private static void createReport(String outputDir) {

        File destinationFile = new File(outputDir, "report.html");
        try {
//            InputStream inputStream = DataMapGenerator.class.getResourceAsStream("/assets/report.html");
            Files.writeString(destinationFile.toPath(), ReportHtml.REPORT_HTML);
//            Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Desktop.getDesktop().browse(destinationFile.toURI());
        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
        }
    }

}
