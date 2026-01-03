package pdfproject.window2.core;

import pdfproject.Config;
import pdfproject.window2.components.ContentPanel;
import pdfproject.window2.components.ConsolePanel;
import pdfproject.window2.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    // ---- Height ratios ----
    private static final float HEADER_RATIO  = 0.08f;
    private static final float CONTENT_RATIO = 0.72f;
    private static final float CONSOLE_RATIO = 0.20f;

    public Window(int height) {
        setTitle(Config.FRAME_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Rectangle usable = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int width = Math.max(800, (int) (usable.width * 0.75));
        setSize(width, height);
        setLocationRelativeTo(null);

        // ---- Calculate heights ----
        int headerHeight  = Math.round(height * HEADER_RATIO);
        int contentHeight = Math.round(height * CONTENT_RATIO);
        int consoleHeight = Math.round(height * CONSOLE_RATIO);

        // ---- Panels ----
        HeaderPanel header = new HeaderPanel();
        header.setPreferredSize(new Dimension(10, headerHeight));

        ContentPanel content = new ContentPanel();
        content.setPreferredSize(new Dimension(10, contentHeight));

        ConsolePanel console = new ConsolePanel();
        console.setPreferredSize(new Dimension(10, consoleHeight));

        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(console, BorderLayout.SOUTH);

        setVisible(true);
    }
}
