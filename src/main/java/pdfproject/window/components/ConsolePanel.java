package pdfproject.window.components;

import pdfproject.window.core.Theme;
import pdfproject.window.stream.CustomOutputStream;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ConsolePanel extends JPanel implements PropertyChangeListener {
    @Serial
    private static final long serialVersionUID = 1L;

    private final JTextPane consolePane;
    private final JScrollPane scrollPane;

    // Gradient label (we paint gradient only for this)
    private final GradientLabel consoleLabel;

    private final CustomOutputStream customStream;
    private final PrintStream savedOut;
    private final PrintStream savedErr;

    public ConsolePanel() {
        super(new BorderLayout());
        setOpaque(true);

        // --- Gradient header label ---
        consoleLabel = new GradientLabel("CONSOLE");
        consoleLabel.setFont(new Font("Impact", Font.BOLD, 22));
        consoleLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));
        // ensure gradient repaints when the label is resized
        consoleLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                consoleLabel.repaint();
            }
        });
        add(consoleLabel, BorderLayout.NORTH);

        // --- TEXT PANE ---
        consolePane = new JTextPane();
        consolePane.setEditable(false);
        consolePane.setFont(new Font("Consolas", Font.PLAIN, 13));
        consolePane.setOpaque(true); // keep console background solid (gradient only on label)

        scrollPane = new JScrollPane(consolePane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // --- STREAMS ---
        customStream = new CustomOutputStream(consolePane);

        savedOut = System.out;
        savedErr = System.err;

        System.setOut(createPrintStream(customStream));
        System.setErr(createPrintStream(customStream.asErrorStream()));

        // Apply initial theme and register
        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private PrintStream createPrintStream(OutputStream os) {
        try {
            return new PrintStream(os, true, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new PrintStream(os);
        }
    }

    /**
     * Apply theme to panel, console and label.
     * For the label gradient we pick two theme tokens (headerText + usernameAccent) as gradient endpoints.
     * If any token is missing, we fall back to reasonable defaults.
     */
    private void applyTheme(Theme t) {
        if (t == null) return;

        // Panel + console background / text
        setBackground(Objects.requireNonNullElse(t.bodyBg, getBackground()));
        consolePane.setBackground(Objects.requireNonNullElse(t.consoleBg, consolePane.getBackground()));
        consolePane.setForeground(Objects.requireNonNullElse(t.consoleText, consolePane.getForeground()));
        scrollPane.getViewport().setBackground(consolePane.getBackground());

        // Label gradient: prefer headerText (start) -> usernameAccent (end). Fall back if null.
        Color start = t.headerText != null ? t.headerText : t.headerBg != null ? t.headerBg : new Color(0x1E5AFF);
        Color end = t.usernameAccent != null ? t.usernameAccent : start.darker();

        consoleLabel.setGradientColors(start, end);

        // Pick an accessible label text color based on the gradient midpoint luminance
        Color fg = pickReadableForeground(midColor(start, end));
        consoleLabel.setForeground(fg);

        // repaint only (no heavy revalidate)
        consoleLabel.repaint();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Theme change -> apply on EDT
        SwingUtilities.invokeLater(() -> {
            Theme theme = ThemeManager.getTheme();
            applyTheme(theme);
            try {
                customStream.recolor(theme);
            } catch (Throwable ignored) {
            }
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // restore original system streams
        if (savedOut != null) System.setOut(savedOut);
        if (savedErr != null) System.setErr(savedErr);

        ThemeManager.unregister(this);
    }

    // Utility: compute an approximate midpoint color between two colors
    private static Color midColor(Color a, Color b) {
        int r = (a.getRed() + b.getRed()) / 2;
        int g = (a.getGreen() + b.getGreen()) / 2;
        int bl = (a.getBlue() + b.getBlue()) / 2;
        int alpha = (a.getAlpha() + b.getAlpha()) / 2;
        return new Color(r, g, bl, alpha);
    }

    // Utility: choose black or white foreground for readability using luminance
    private static Color pickReadableForeground(Color bg) {
        double lum = (0.2126 * srgb(bg.getRed()) + 0.7152 * srgb(bg.getGreen()) + 0.0722 * srgb(bg.getBlue()));
        // If luminance is dark, pick white; otherwise pick near-black.
        return lum < 0.5 ? Color.WHITE : new Color(0x0D0D0D);
    }

    // sRGB linearization helper (maps 0-255 to 0..1 gamma-corrected)
    private static double srgb(int channel) {
        double c = channel / 255.0;
        return (c <= 0.03928) ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    /**
     * Simple JLabel subclass that paints a vertical gradient inside its bounds.
     * It does not fill the whole panel — only the label area.
     */
    private static class GradientLabel extends JLabel {
        private Color topColor = new Color(0x1E5AFF);
        private Color bottomColor = topColor.darker();

        public GradientLabel(String text) {
            super(text);
            setOpaque(false); // we paint custom background for label only
        }

        public void setGradientColors(Color top, Color bottom) {
            if (top != null) this.topColor = top;
            if (bottom != null) this.bottomColor = bottom;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // paint gradient background limited to label bounds
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // If zero-sized, skip painting
                if (w > 0 && h > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, topColor, 0, h, bottomColor);
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, w, h);
                }

                // Then draw the label text on top
                super.paintComponent(g2);
            } finally {
                g2.dispose();
            }
        }

        // Ensure text is drawn opaque on top of our gradient
        @Override
        public void setForeground(Color fg) {
            super.setForeground(fg);
            repaint();
        }
    }
}
