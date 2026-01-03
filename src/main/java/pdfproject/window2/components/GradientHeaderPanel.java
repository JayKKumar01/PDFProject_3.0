package pdfproject.window2.components;

import javax.swing.*;
import java.awt.*;

public class GradientHeaderPanel extends JPanel {

    private final Color start;
    private final Color end;

    public GradientHeaderPanel(Color start, Color end) {
        this.start = start;
        this.end = end;
        setLayout(new BorderLayout());
        setOpaque(false); // we paint everything ourselves
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint paint = new GradientPaint(
                0, 0, start,
                getWidth(), 0, end
        );

        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }
}
