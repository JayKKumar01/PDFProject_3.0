package pdfproject.window.experiment.core;

import pdfproject.Config;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.experiment.components.BodyExperimentPanel;
import pdfproject.window.experiment.components.ConsoleExperimentPanel;
import pdfproject.window.experiment.components.header.HeaderExperimentPanel;
import pdfproject.window.experiment.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Window3: responsive, three-part app window with DPI-aware UI scaling.
 */
public final class Window3 {

    private final JFrame frame;
    private final HeaderExperimentPanel header;
    private final ConsoleExperimentPanel console;

    // percentages (header, console) — body is the remainder
    private static final double HEADER_RATIO = 0.10;
    private static final double CONSOLE_RATIO = 0.30;

    public Window3(int preferredHeight) {
        int preferredWidth = (int) (preferredHeight * (16.0 / 9));

        frame = new JFrame(Config.FRAME_NAME + " - Window3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(preferredWidth, preferredHeight);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(ThemeColors.BACKGROUND);
        frame.setLayout(new BorderLayout());

        // Initialize UiScale from frame's GraphicsConfiguration (best practice)
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        if (gc != null) UiScale.initFromGraphicsConfig(gc);
        else UiScale.initFromDefaultScreen();

        header = new HeaderExperimentPanel();
        BodyExperimentPanel body = new BodyExperimentPanel();
        console = new ConsoleExperimentPanel();

        // header at top, console at bottom, body center
        frame.add(header, BorderLayout.NORTH);
        frame.add(console, BorderLayout.SOUTH);
        frame.add(body, BorderLayout.CENTER);

        // compute initial sizes and set preferred sizes for north/south panels
        applyProportions(frame.getHeight());

        // on resize or display change, recompute
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Re-evaluate scale in case the window moved to another monitor with different DPI:
                GraphicsConfiguration newGc = frame.getGraphicsConfiguration();
                if (newGc != null) UiScale.initFromGraphicsConfig(newGc);

                applyProportions(frame.getHeight());
            }
        });

        frame.setVisible(true);
    }

    private void applyProportions(int totalHeight) {
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
