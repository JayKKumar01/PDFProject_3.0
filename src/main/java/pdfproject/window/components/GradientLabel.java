package pdfproject.window.components;

import javax.swing.*;
import java.awt.*;

public class GradientLabel extends JLabel {

    private final Color start;
    private final Color end;

    public GradientLabel(String text, Color start, Color end) {
        super(text);
        this.start = start;
        this.end = end;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        int textHeight = fm.getAscent();
        int textWidth = fm.stringWidth(getText());

        GradientPaint paint = new GradientPaint(
                0, 0, start,
                textWidth, 0, end
        );

        g2.setPaint(paint);
        g2.setFont(getFont());
        g2.drawString(getText(), 0, textHeight);

        g2.dispose();
    }
}
