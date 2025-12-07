package pdfproject.window;


import pdfproject.window.core.Window;

import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;

        // Use 75% of screen height
        int windowHeight = (int) (screenHeight * 0.75);

        new Window(windowHeight);
//        new Window2(windowHeight);


        testConsole();
    }

    private static void testConsole() {
        System.out.println("This is a standard output message (blue).");
        System.err.println("This is an error message (red).");
        System.out.println("Console is working as expected!");
    }
}
