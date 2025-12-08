package pdfproject.window.experiment.components.body;

import pdfproject.window.experiment.components.body.left.LeftSectionPanel;
import pdfproject.window.experiment.components.body.right.RightSectionPanel;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Exact 50/50 left/right using GridLayout(1,2).
 * A vertical line is painted exactly at the middle and ABOVE the children.
 */
public class BodyExperimentPanel extends JPanel implements PropertyChangeListener {

    private final LeftSectionPanel left;
    private final RightSectionPanel right;

    public BodyExperimentPanel() {
        // GridLayout guarantees exact halves
        setLayout(new GridLayout(1, 2, 0, 0));

        left = new LeftSectionPanel();
        right = new RightSectionPanel();

        add(left);
        add(right);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.bodyBg);
        repaint();
    }

    /**
     * We override paintChildren() so the line is drawn ABOVE
     * the child panels, making it always visible.
     */
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        int x = getWidth() / 2;

        // readableForeground ensures good contrast on both light and dark themes
        Color fg = ExperimentTheme.readableForeground(getBackground());
        Color lineColor = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 200); // more visible opacity

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // crisp 1px vertical line → no antialiasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(1f));

            // draw the line centered in the panel
            g2.drawLine(x, 8, x, getHeight() - 8);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            applyTheme(ThemeManager.getTheme());
            left.repaint();
            right.repaint();
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
