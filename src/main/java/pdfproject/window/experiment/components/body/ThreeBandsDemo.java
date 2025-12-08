package pdfproject.window.experiment.components.body;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * ThreeBandsDemo
 *
 * Demonstrates a zero-dependency GridBagLayout approach that enforces
 * the center band to be exactly 2% of the container width (rounded to pixels).
 *
 * Save as ThreeBandsDemo.java and run:
 *   javac ThreeBandsDemo.java
 *   java ThreeBandsDemo
 */
public class ThreeBandsDemo {

    // percent for center band (0.02 == 2%)
    private static final double BAND_PERCENT = 0.02;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3 Bands — center = 2%");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel main = createMainPanel();
            frame.setContentPane(main);

            frame.setSize(900, 500); // starting size
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Ensure band width is set right after showing
            SwingUtilities.invokeLater(() -> enforceBandWidth(main));
        });
    }

    private static JPanel createMainPanel() {
        // Left / Center / Right
        JPanel left = makeBandPanel("LEFT", new Color(0xAED581));
        JPanel center = makeBandPanel("CENTER (2%)", new Color(0xFFCC80));
        JPanel right = makeBandPanel("RIGHT", new Color(0x81D4FA));

        // Store center band in client property so we can find it from main panel later.
        JPanel main = new JPanel(new GridBagLayout());
        main.putClientProperty("centerBand", center);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);

        // LEFT: weightx = 1 (takes remaining space)
        c.gridx = 0;
        c.weightx = 1.0;
        main.add(left, c);

        // CENTER: weightx = 0 (we'll explicitly set preferredWidth)
        c.gridx = 1;
        c.weightx = 0.0;
        main.add(center, c);

        // RIGHT: weightx = 1 (symmetric)
        c.gridx = 2;
        c.weightx = 1.0;
        main.add(right, c);

        // Make center have a minimal preferred size initially
        center.setPreferredSize(new Dimension(1, 1));

        // Add resize listener to enforce exact 2% width on every resize/show
        main.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                enforceBandWidth(main);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                enforceBandWidth(main);
            }
        });

        return main;
    }

    // Utility to create a colored band with a centered label
    private static JPanel makeBandPanel(String text, Color bg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        p.add(label);
        p.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return p;
    }

    // Calculate exact pixel width for center band and update preferred size if changed.
    private static void enforceBandWidth(JPanel main) {
        Component center = (Component) main.getClientProperty("centerBand");
        if (center == null) return;

        int totalWidth = main.getWidth();
        if (totalWidth <= 0) return; // not laid out yet

        int bandWidth = Math.max(1, (int) Math.round(totalWidth * BAND_PERCENT));
        Dimension pref = center.getPreferredSize();

        // Only change if different to avoid unnecessary layout churn
        if (pref == null || pref.width != bandWidth || pref.height != main.getHeight()) {
            center.setPreferredSize(new Dimension(bandWidth, main.getHeight()));
            // Revalidate/repaint so GridBagLayout immediately picks up the new size
            main.revalidate();
            main.repaint();
        }
    }
}
