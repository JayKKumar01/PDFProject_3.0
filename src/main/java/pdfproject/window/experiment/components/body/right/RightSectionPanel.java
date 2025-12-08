package pdfproject.window.experiment.components.body.right;

import pdfproject.window.experiment.components.ValidationAwarePanel;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * RightSectionPanel with OptionPanel (20%) and ColorPanel (80%).
 * Uses GridBagLayout with weighty to achieve proportional sizing.
 * Divider is drawn at the 20% mark and painted after children so it appears above them.
 */
public class RightSectionPanel extends ValidationAwarePanel implements PropertyChangeListener {

    private final OptionPanel optionPanel;
    private final ColorPanel colorPanel;

    private static final double OPTION_RATIO = 0.20;

    public RightSectionPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        optionPanel = new OptionPanel();
        colorPanel = new ColorPanel();

        // OptionPanel (20%)
        gbc.gridy = 0;
        gbc.weighty = OPTION_RATIO;
        add(optionPanel, gbc);

        // ColorPanel (80%)
        gbc.gridy = 1;
        gbc.weighty = 1.0 - OPTION_RATIO;
        add(colorPanel, gbc);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;
        setBackground(t.bodyBg);
        // children update themselves; repaint to reflect background change
        repaint();
    }

    /**
     * Draw the horizontal divider AFTER painting children so it appears above them.
     * Divider is positioned at OPTION_RATIO of the height.
     */
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        int y = (int) (getHeight() * OPTION_RATIO);

        Color fg = ExperimentTheme.readableForeground(getBackground());
        Color lineColor = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 180);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // crisp 1px horizontal line
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(1f));

            // inset left/right to match typical padding
            int inset = 12;
            g2.drawLine(inset, y, getWidth() - inset, y);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            applyTheme(ThemeManager.getTheme());
            optionPanel.repaint();
            colorPanel.repaint();
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }

    // Exposure methods to allow external wiring if needed
    public OptionPanel getOptionPanel() { return optionPanel; }
    public ColorPanel getColorPanel() { return colorPanel; }
}
