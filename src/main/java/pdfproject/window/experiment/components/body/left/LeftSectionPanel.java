package pdfproject.window.experiment.components.body.left;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * LeftSectionPanel composes InputPanel (top) and LauncherPanel (bottom)
 * in an exact 50/50 layout and draws a horizontal divider in the middle.
 */
public class LeftSectionPanel extends JPanel implements PropertyChangeListener {

    private final InputPanel inputPanel;
    private final LauncherPanel launcherPanel;

    public LeftSectionPanel() {
        // exact halves top/bottom
        setLayout(new GridLayout(2, 1, 0, 0));

        inputPanel = new InputPanel();
        launcherPanel = new LauncherPanel();

        add(inputPanel);
        add(launcherPanel);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.bodyBg);
        // let children update themselves via their own ThemeManager-registration
        repaint();
    }

    /**
     * Draw the horizontal divider AFTER painting children so it appears above them.
     */
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        int y = getHeight() / 2;

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
            inputPanel.repaint();
            launcherPanel.repaint();
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }

    // Exposure methods to allow external wiring if needed
    public InputPanel getInputPanel() { return inputPanel; }
    public LauncherPanel getLauncherPanel() { return launcherPanel; }
}
