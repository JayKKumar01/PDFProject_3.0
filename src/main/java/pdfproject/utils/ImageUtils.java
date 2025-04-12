package pdfproject.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

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
}
