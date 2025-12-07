package pdfproject.window.experiment.core;

import pdfproject.Config;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.experiment.components.BodyExperimentPanel;
import pdfproject.window.experiment.components.ConsoleExperimentPanel;
import pdfproject.window.experiment.components.HeaderExperimentPanel;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Window3: responsive, three-part app window.
 * - Header (NORTH) fixed percentage height
 * - Console (SOUTH) fixed percentage height
 * - Body (CENTER) fills remaining space
 *
 * Panels are normal Swing components that update themselves when ThemeManager fires events.
 */
public final class Window3 {

    private final JFrame frame;
    private final HeaderExperimentPanel header;
    private final BodyExperimentPanel body;
    private final ConsoleExperimentPanel console;

    // percentages (header, console) — body is the remainder
    private static final double HEADER_RATIO = 0.15;
    private static final double CONSOLE_RATIO = 0.20;

    public Window3(int preferredHeight) {
        int preferredWidth = (int) (preferredHeight * (16.0 / 9));

        frame = new JFrame(Config.FRAME_NAME + " - Window3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(preferredWidth, preferredHeight);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(ThemeColors.BACKGROUND);
        frame.setLayout(new BorderLayout());

        header = new HeaderExperimentPanel();
        body = new BodyExperimentPanel();
        console = new ConsoleExperimentPanel();

        // header at top, console at bottom, body center
        frame.add(header, BorderLayout.NORTH);
        frame.add(console, BorderLayout.SOUTH);
        frame.add(body, BorderLayout.CENTER);

        // compute initial sizes and set preferred sizes for north/south panels
        applyProportions(frame.getHeight());

        // on resize, recompute preferred sizes (no manual child positioning)
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyProportions(frame.getHeight());
            }
        });

        frame.setVisible(true);
    }

    private void applyProportions(int totalHeight) {
        int headerH = (int) Math.max(48, totalHeight * HEADER_RATIO); // ensure minimum
        int consoleH = (int) Math.max(64, totalHeight * CONSOLE_RATIO);

        header.setPreferredSize(new Dimension(0, headerH));
        console.setPreferredSize(new Dimension(0, consoleH));

        // revalidate only the top-level containers that changed
        header.revalidate();
        console.revalidate();
        // body will be resized by layout manager automatically
        frame.revalidate();
        frame.repaint();
    }
}
