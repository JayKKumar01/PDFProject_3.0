package pdfproject.window;

import com.formdev.flatlaf.FlatDarkLaf;
import pdfproject.window.core.Window;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                FlatDarkLaf.setup();

                // Let FlatLaf draw the title bar
                JFrame.setDefaultLookAndFeelDecorated(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Rectangle usable = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getMaximumWindowBounds();

            int height = Math.max(480, (int) (usable.height * 0.75));
            new Window(height);
//            testConsole();
        });
    }
    private static void testConsole() {
        System.out.println("This is a standard output message.");
        System.err.println("This is an error message (red).");
    }
}
