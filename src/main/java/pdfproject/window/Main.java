package pdfproject.window;

import com.formdev.flatlaf.FlatDarkLaf;
import pdfproject.window.core.Window;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                FlatDarkLaf.setup();
                JFrame.setDefaultLookAndFeelDecorated(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int height = WindowUtil.calculateWindowHeight();
            new Window(height);
        });
    }

    private static void testConsole() {
        System.out.println("This is a standard output message.");
        System.err.println("This is an error message (red).");
    }
}
