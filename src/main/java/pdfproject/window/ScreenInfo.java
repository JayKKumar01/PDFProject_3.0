package pdfproject.window;

import java.awt.*;

public class ScreenInfo {

    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = screenSize.width;
        int height = screenSize.height;

        System.out.println("Screen Width  : " + width + " px");
        System.out.println("Screen Height : " + height + " px");
    }
}
