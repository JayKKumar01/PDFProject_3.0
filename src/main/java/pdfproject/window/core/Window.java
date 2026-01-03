package pdfproject.window.core;

import pdfproject.Config;
import pdfproject.window.components.body.BodyPanel;
import pdfproject.window.components.ConsolePanel;
import pdfproject.window.components.header.HeaderPanel;
import pdfproject.window.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Window3: responsive, three-part app window with DPI-aware UI scaling.
 */
public final class Window {

    private final JFrame frame;
    private final HeaderPanel header;
    private final ConsolePanel console;

    // percentages (header, console) — body is the remainder
    private static final double HEADER_RATIO = 0.10;
    private static final double CONSOLE_RATIO = 0.30;

    public Window(int preferredHeight) {
        int preferredWidth = (int) (preferredHeight * (16.0 / 9));

        frame = new JFrame(Config.FRAME_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(preferredWidth, preferredHeight);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Theme.HEADER_BG_LIGHT);
        frame.setLayout(new BorderLayout());

        // Initialize UiScale from frame's GraphicsConfiguration (best practice)
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        if (gc != null) UiScale.initFromGraphicsConfig(gc);
        else UiScale.initFromDefaultScreen();

        header = new HeaderPanel();
        BodyPanel body = new BodyPanel();
        console = new ConsolePanel();

        // header at top, console at bottom, body center
        frame.add(header, BorderLayout.NORTH);
        frame.add(console, BorderLayout.SOUTH);
        frame.add(body, BorderLayout.CENTER);

        // on resize or display change, recompute
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Re-evaluate scale in case the window moved to another monitor with different DPI:
                GraphicsConfiguration newGc = frame.getGraphicsConfiguration();
                if (newGc != null) UiScale.initFromGraphicsConfig(newGc);

                applyProportions();
            }
        });

        // show frame first so insets / content sizes are valid, then apply proportions
        frame.setVisible(true);

        // Ensure initial proportioning uses real content height (after frame is realized)
        applyProportions();
    }

    /**
     * Compute available content height and set preferred sizes for header/console.
     * Uses content pane height when possible; otherwise computes from frame height minus insets.
     */
    private void applyProportions() {
        int totalHeight;

        // Prefer content pane height (already excludes insets). If it's 0 (not realized), fallback.
        int contentH = frame.getContentPane().getHeight();
        if (contentH > 0) {
            totalHeight = contentH;
        } else {
            Insets insets = frame.getInsets();
            totalHeight = frame.getHeight() - insets.top - insets.bottom;
        }

        // Defensive: if still non-positive, fallback to frame height as last resort
        if (totalHeight <= 0) totalHeight = Math.max(1, frame.getHeight());

        // Use UiScale to ensure minimum sizes scale with DPI
        int minHeader = UiScale.scaleInt(48);
        int minConsole = UiScale.scaleInt(64);

        int headerH = Math.max(minHeader, Math.round((float) totalHeight * (float) HEADER_RATIO));
        int consoleH = Math.max(minConsole, Math.round((float) totalHeight * (float) CONSOLE_RATIO));

        header.setPreferredSize(new Dimension(0, headerH));
        console.setPreferredSize(new Dimension(0, consoleH));

        header.revalidate();
        console.revalidate();
        frame.revalidate();
        frame.repaint();
    }
}
