package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
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

    public static BufferedImage drawBoundingBoxes(BufferedImage image, List<WordInfo> words, float dpi) {
        float scale = dpi / 72f; // PDF default is 72 DPI

        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(1.5f));

        for (WordInfo word : words) {
            if (word.getPositions().isEmpty()) continue;

            TextPosition first = word.getPositions().getFirst();
            TextPosition last = word.getPositions().getLast();

            float x = first.getX() * scale;
            float y = first.getY() * scale;
            float width = (last.getX() + last.getWidth()) * scale - x;
            float height = first.getHeight() * scale;

            // Draw box (adjust y for image coord system if needed)
            g2d.drawRect(Math.round(x), Math.round(y - height), Math.round(width), Math.round(height));
        }

        g2d.dispose();
        return output;
    }

}
