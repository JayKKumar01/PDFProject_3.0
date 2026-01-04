package pdfproject.window.core;

import pdfproject.Config;
import pdfproject.window.components.content.ContentPanel;
import pdfproject.window.components.ConsolePanel;
import pdfproject.window.components.header.HeaderPanel;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    // ---- Height ratios ----
    private static final float HEADER_RATIO  = 0.1f;
    private static final float CONTENT_RATIO = 0.65f;

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
        int headerHeight  = Math.max(64, Math.round(height * HEADER_RATIO));
        int contentHeight = Math.round(height * CONTENT_RATIO);

        int consoleHeight = Math.max(
                120,
                height - headerHeight - contentHeight
        );

        // ---- Panels ----
        HeaderPanel header = new HeaderPanel();
        header.setPreferredSize(new Dimension(10, headerHeight));

        ContentPanel content = new ContentPanel(contentHeight);
        content.setPreferredSize(new Dimension(10, contentHeight));

        ConsolePanel console = new ConsolePanel();
        console.setPreferredSize(new Dimension(10, consoleHeight));

        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(console, BorderLayout.SOUTH);

        setVisible(true);
    }
}
