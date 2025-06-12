package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
import pdfproject.Config;
import pdfproject.constants.Operation;
import pdfproject.constants.OperationColor;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageUtils {
    public static BufferedImage generateDiffImage(BufferedImage img1, BufferedImage img2) {
        int width = Math.min(img1.getWidth(), img2.getWidth());
        int height = Math.min(img1.getHeight(), img2.getHeight());
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                if (rgb1 != rgb2) {
                    result.setRGB(x, y, Color.RED.getRGB()); // Mark difference in red
                } else {
                    result.setRGB(x, y, rgb1); // Keep original pixel
                }
            }
        }

        return result;
    }

    public static BufferedImage drawBoundingBoxes(BufferedImage image, List<WordInfo> words) {
        if (words.isEmpty() || image == null){
            return image;
        }
        float scale = Config.RENDER_DPI / 72f; // PDF default is 72 DPI
        float padding = 3.0f; // Padding around the word

        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setStroke(new BasicStroke(3f));

        for (WordInfo word : words) {
            if (word.getTextPositions().isEmpty()) continue;

            // Get the first and last text positions
            TextPosition first = word.getTextPositions().get(0);
            TextPosition last = word.getTextPositions().get(word.getTextPositions().size()-1);

            // Calculate position and size with padding
            float x = first.getX() * scale - padding; // Apply padding on left
            float y = first.getY() * scale - padding; // Apply padding on top
            float width = (last.getX() + last.getWidth()) * scale - x; // Apply padding on both sides
            float height = first.getHeight() * scale; // Apply padding on top and bottom

            // Set the color based on the operation
            Color boxColor = getOperationColor(word);
            if (OperationColor.DELETED.equals(boxColor)) {
                word.setBelongsToFirst(true);
            }

            // check if boxColor is same as Deleted color then set wordInfo.setBelongsToFirst(true)

            // Set the color for the bounding box
            g2d.setColor(boxColor);
            // here do wordInfo.setBoundingBox(rect) then use this rect to draw

            Rectangle boundingBox = new Rectangle(Math.round(x), Math.round(y - height), Math.round(width + (padding)), Math.round(height + (2 * padding)));
            word.setBoundingBox(boundingBox);
            // Draw using the bounding box stored earlier
            g2d.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }

        g2d.dispose();
        return output;
    }


    public static Color getOperationColor(WordInfo word) {
        // Check if there are multiple operations
        if (word.getOperations().size() > 1) {
            return OperationColor.MULTIPLE; // BLACK for multiple operations
        }

        // If only one operation, use switch to determine the color
        Operation operation = word.getOperations().iterator().next(); // Get the single operation

        switch (operation) {
            case DELETED:
                return OperationColor.DELETED; // RED for DELETED
            case ADDED:
                return OperationColor.ADDED; // GREEN for ADDED
            case FONT:
                return OperationColor.FONT_NAME; // MAGENTA for FONT_NAME
            case SIZE:
                return OperationColor.FONT_SIZE; // BLUE for FONT_SIZE
            case STYLE:
                return OperationColor.FONT_STYLE; // CYAN for FONT_STYLE
            default:
                return OperationColor.MULTIPLE; // BLACK for unknown or unhandled cases
        }

    }



}
