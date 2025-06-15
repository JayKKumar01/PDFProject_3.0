package pdfproject.windowui;

import pdfproject.windowui.core.Window;

public class Main {

    public static void main(String[] args) {
        Window window = new Window(540);
        testConsole();
    }

    private static void testConsole() {
        System.out.println("This is a standard output message (blue).");
        System.err.println("This is an error message (red).");
        System.out.println("Console is working as expected!");
    }
}
