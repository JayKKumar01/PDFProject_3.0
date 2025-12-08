package pdfproject.window.experiment.components.body;

import pdfproject.window.experiment.core.ExperimentTheme;

import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;

/**
 * Draws a single vertical divider at a given horizontal ratio (0.0 - 1.0).
 * Painted after children because borders are painted in paintBorder().
 */
public class CenterDividerBorder implements Border {

    private final double ratio;      // 0.0..1.0 (0.5 = center)
    private final int inset;         // top/bottom inset for the line
    private final float strokeWidth; // 1.0f for crisp 1px line
    private final int alpha;         // 0..255 opacity

    public CenterDividerBorder(double ratio, int inset, float strokeWidth, int alpha) {
        this.ratio = Math.max(0.0, Math.min(1.0, ratio));
        this.inset = Math.max(0, inset);
        this.strokeWidth = Math.max(0.5f, strokeWidth);
        this.alpha = Math.max(0, Math.min(255, alpha));
    }

    public CenterDividerBorder(double ratio) {
        this(ratio, 8, 1f, 200);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int cx = x + (int) Math.round(width * ratio);

        Color bg = c.getBackground();
        Color fg = ExperimentTheme.readableForeground(bg);
        Color lineColor = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), alpha);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(strokeWidth));

            int top = y + inset;
            int bottom = y + height - inset;
            g2.drawLine(cx, top, cx, bottom);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
