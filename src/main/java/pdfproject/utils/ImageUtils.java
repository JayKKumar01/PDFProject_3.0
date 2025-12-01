package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
import pdfproject.Config;
import pdfproject.constants.FileTypes;
import pdfproject.constants.OperationColor;
import pdfproject.constants.Texts;
import pdfproject.models.WordInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        g2d.setStroke(new BasicStroke(2.5f));

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

    public static BufferedImage createDummyImage(int width, int height, Color textColor, String infoText) {
        // Create a dummy image with the specified width and height
        BufferedImage dummyImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dummyImage.createGraphics();

        // Fill the image with white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Set up the info text
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        // Calculate the position to center the text horizontally and vertically
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(infoText);
        int textHeight = fontMetrics.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fontMetrics.getAscent();

        // Draw the text in the middle
        g2d.drawString(infoText, x, y);

        g2d.dispose();

        return dummyImage;
    }




    private static Color getOperationColor(WordInfo word) {
        // Check if there are multiple operations
        if (word.getOperations().size() > 1) {
            return OperationColor.MULTIPLE; // BLACK for multiple operations
        }
        return OperationColor.get(word.getOperations().iterator().next());

    }


    public static String getDummyReportImage(String outputImagePath) throws IOException {
        String dirPath = String.format("%s", outputImagePath);
        String fileName = Texts.NO_DIFF_IMAGE_NAME + FileTypes.IMAGE_EXTENSION;
        File outputFile = new File(dirPath, fileName);

        // Create the output directory if it doesn't exist
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // If the dummy image already exists, return its path
        if (outputFile.exists()) {
            return outputFile.getAbsolutePath();
        }

        // A4 dimensions in points (72 DPI): 595 x 842
        BufferedImage dummyImage = createDummyImage(595, 842, Color.GREEN.darker(), Texts.NO_DIFF_FOUND);

        // Save the image
        ImageIO.write(dummyImage, FileTypes.IMAGE_TYPE, outputFile);

        return outputFile.getAbsolutePath();
    }
}
