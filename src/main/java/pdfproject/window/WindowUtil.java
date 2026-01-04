package pdfproject.window;

import java.awt.*;

public final class WindowUtil {

    private WindowUtil() {
        // utility class
    }

    /**
     * General-case JFrame height:
     * 75% of usable screen height with a safe minimum.
     */
    public static int calculateWindowHeight() {
        Rectangle usable = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int calculated = (int) (usable.height * 0.75);

        // Minimum height safeguard for small screens
        return Math.max(480, calculated);
    }
}
