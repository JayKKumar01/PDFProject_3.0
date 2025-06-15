package pdfproject.windowui.utils;

import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentFactory {

    public static JButton createStyledButton(String text) {
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

                Color topColor = pressed ? new Color(210, 220, 235)
                                         : new Color(240, 248, 255);
                Color bottomColor = pressed ? new Color(200, 210, 225)
                                            : new Color(220, 230, 245);

                GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, 12, 12);

                if (hovered && !pressed) {
                    g2.setColor(new Color(255, 255, 255, 80)); // Hover overlay
                    g2.fillRoundRect(0, 0, width, height, 12, 12);
                }

                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeColors.THEME_BLUE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(ThemeColors.THEME_BLUE);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 36));

        return button;
    }
}
