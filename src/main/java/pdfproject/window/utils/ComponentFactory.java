package pdfproject.window.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentFactory {

    public static JButton createStyledButton(String text, Color textColor, Color backgroundColor) {
        final JButton button = new JButton(text) {
            private boolean hovered = false;
            private boolean pressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();

                Color topColor = pressed ? darken(backgroundColor, 0.15f) : backgroundColor;
                Color bottomColor = pressed ? darken(backgroundColor, 0.25f) : brighten(backgroundColor, 0.05f);

                GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, 12, 12);

                if (hovered && !pressed && isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, 50)); // Hover overlay
                    g2.fillRoundRect(0, 0, width, height, 12, 12);
                }

                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Color borderColor = isEnabled() ? textColor : Color.GRAY;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Calculate dynamic width with font metrics and insets
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        Insets insets = button.getInsets();
        int textWidth = metrics.stringWidth(text);
        int padding = 30;
        int buttonWidth = textWidth + padding + insets.left + insets.right;
        int buttonHeight = metrics.getHeight() + 12;

        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        return button;
    }

    private static Color darken(Color color, float factor) {
        factor = Math.min(1, Math.max(0, factor));
        int r = (int) (color.getRed() * (1 - factor));
        int g = (int) (color.getGreen() * (1 - factor));
        int b = (int) (color.getBlue() * (1 - factor));
        return new Color(r, g, b, color.getAlpha());
    }

    private static Color brighten(Color color, float factor) {
        factor = Math.min(1, Math.max(0, factor));
        int r = (int) (color.getRed() + (255 - color.getRed()) * factor);
        int g = (int) (color.getGreen() + (255 - color.getGreen()) * factor);
        int b = (int) (color.getBlue() + (255 - color.getBlue()) * factor);
        return new Color(r, g, b, color.getAlpha());
    }
}
