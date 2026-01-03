package pdfproject.window;

import pdfproject.window.core.Window;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Launch the experiment window using 75% of the usable screen height.
 */
public class Main {
    public static void main(String[] args) {
        boolean defaultDark = true;
        ThemeManager.initFromSettings(defaultDark);

        SwingUtilities.invokeLater(() -> {
            // Use usable screen bounds (excludes taskbar/dock)
            Rectangle usable = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getMaximumWindowBounds();

            int height = Math.max(480, (int) (usable.height * 0.75)); // enforce a sensible min height
            new Window(height);
//            testConsole();
        });
    }
    private static void testConsole() {
        System.out.println("This is a standard output message (blue).");
        System.err.println("This is an error message (red).");
        System.out.println("Console is working as expected!");
    }

}
