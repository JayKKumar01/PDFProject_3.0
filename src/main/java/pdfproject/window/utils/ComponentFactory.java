package pdfproject.window.utils;

import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Component factory that produces theme-aware UI components.
 * Buttons created here automatically adapt when ThemeManager toggles dark mode.
 */
public class ComponentFactory {

    /**
     * Create a styled button.
     *
     * @param text           button text
     * @param textColorLight text color for LIGHT theme
     * @param bgColorLight   background color for LIGHT theme (used in gradient)
     * @return a theme-aware JButton
     */
    public static JButton createStyledButton(String text, Color textColorLight, Color bgColorLight) {
        ThemeAwareButton button = new ThemeAwareButton(text, textColorLight, bgColorLight);
        return button;
    }

    /**
     * Theme-aware button implementation.
     */
    private static class ThemeAwareButton extends JButton implements pdfproject.window.utils.ThemeManager.ThemeChangeListener {
        private boolean hovered = false;
        private boolean pressed = false;

        private final Color lightText;
        private final Color lightBg;

        // computed dark equivalents
        private final Color darkText;
        private final Color darkBg;

        ThemeAwareButton(String text, Color lightText, Color lightBg) {
            super(text);
            this.lightText = lightText;
            this.lightBg = lightBg;

            // compute dark equivalents using simple mapping rules (blue/white -> green/black)
            this.darkBg = computeDarkBackground(lightBg);
            this.darkText = computeDarkText(lightText, darkBg);

            init();
            // register for theme changes
            pdfproject.window.utils.ThemeManager.register(this);
            // apply initial theme state
            applyTheme(pdfproject.window.utils.ThemeManager.isDarkMode());
        }

        private void init() {
            // mouse listeners for hover/press visual state
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        hovered = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    pressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isEnabled()) {
                        pressed = true;
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });

            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setContentAreaFilled(false); // we'll paint background ourselves
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorderPainted(false);

            // Calculate dynamic size
            FontMetrics metrics = getFontMetrics(getFont());
            int textWidth = metrics.stringWidth(getText());
            int padding = 30;
            int buttonWidth = textWidth + padding;
            int buttonHeight = metrics.getHeight() + 12;
            setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int width = getWidth();
            int height = getHeight();

            Color bg = getCurrentBackground();
            // pressed / hover adjustments
            Color topColor = pressed ? darken(bg, 0.15f) : bg;
            Color bottomColor = pressed ? darken(bg, 0.25f) : brighten(bg, 0.05f);

            GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, width, height, 12, 12);

            if (hovered && !pressed && isEnabled()) {
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, width, height, 12, 12);
            }

            // Draw text (use button's foreground to allow LAF to handle disabled state)
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent();
            Color textCol = getCurrentTextColor();
            g2.setColor(textCol);
            int tx = (width - textW) / 2;
            int ty = (height + textH) / 2 - 2;
            g2.drawString(text, tx, ty);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Color borderColor = isEnabled() ? getCurrentTextColor() : Color.GRAY;
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
        }

        private Color getCurrentBackground() {
            return pdfproject.window.utils.ThemeManager.isDarkMode() ? darkBg : lightBg;
        }

        private Color getCurrentTextColor() {
            return pdfproject.window.utils.ThemeManager.isDarkMode() ? darkText : lightText;
        }

        private Color computeDarkBackground(Color lightBg) {
            // Heuristics mapping: if light background is white/blue-ish -> use THEME_GREEN for dark mode,
            // otherwise keep the same color.
            if (equalsColor(lightBg, ThemeColors.CONSOLE_TEXT_BG) ||
                    equalsColor(lightBg, Color.WHITE) ||
                    equalsColor(lightBg, ThemeColors.THEME_BLUE) ||
                    equalsColor(lightBg, ThemeColors.THEME_BLUE_LIGHT)) {
                return ThemeColors.THEME_GREEN;
            }
            if (equalsColor(lightBg, ThemeColors.THEME_GREEN)) {
                return ThemeColors.THEME_GREEN;
            }
            // fallback: preserve
            return lightBg;
        }

        private Color computeDarkText(Color lightText, Color targetDarkBg) {
            // If dark background is a strong color (green), use DARK_BACKGROUND as text color for contrast.
            if (equalsColor(targetDarkBg, ThemeColors.THEME_GREEN)) {
                return ThemeColors.DARK_BACKGROUND;
            }
            // If light text is a specific accent (blue), map to green in dark mode for accents.
            if (equalsColor(lightText, ThemeColors.THEME_BLUE)) {
                return ThemeColors.THEME_GREEN;
            }
            // fallback: preserve
            return lightText;
        }

        private static boolean equalsColor(Color a, Color b) {
            if (a == null || b == null) return false;
            return a.getRGB() == b.getRGB();
        }

        private static Color darken(Color color, float factor) {
            factor = Math.min(1, Math.max(0, factor));
            int r = (int) (color.getRed() * (1 - factor));
            int g = (int) (color.getGreen() * (1 - factor));
            int b = (int) (color.getBlue() * (1 - factor));
            return new Color(clamp(r), clamp(g), clamp(b), color.getAlpha());
        }

        private static Color brighten(Color color, float factor) {
            factor = Math.min(1, Math.max(0, factor));
            int r = (int) (color.getRed() + (255 - color.getRed()) * factor);
            int g = (int) (color.getGreen() + (255 - color.getGreen()) * factor);
            int b = (int) (color.getBlue() + (255 - color.getBlue()) * factor);
            return new Color(clamp(r), clamp(g), clamp(b), color.getAlpha());
        }

        private static int clamp(int v) {
            return Math.max(0, Math.min(255, v));
        }

        @Override
        public void onThemeChanged(boolean dark) {
            applyTheme(dark);
        }

        private void applyTheme(boolean dark) {
            // Update properties that Swing may depend on
            setForeground(dark ? darkText : lightText);
            // repaint to make gradient & text update
            repaint();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            // unregister listener to avoid leaks
            pdfproject.window.utils.ThemeManager.unregister(this);
        }
    }
}
