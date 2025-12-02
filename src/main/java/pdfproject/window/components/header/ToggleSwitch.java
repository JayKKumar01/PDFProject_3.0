package pdfproject.window.components.header;

import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable animated toggle switch component (thicker variant).
 * - Width: 72, Height: 36 (more substantial)
 * - Knob: 30
 * - API: addActionListener, removeActionListener, isDark, setDark, toggle
 */
public class ToggleSwitch extends JComponent {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    private static final int KNOB_SIZE = 17;
    private boolean dark;          // true => dark, false => light
    private float animPos = 0f;    // 0.0 (left) .. 1.0 (right)
    private Timer animTimer;
    private final int animMs = 220;

    private final List<ActionListener> actionListeners = new ArrayList<>();

    public ToggleSwitch(boolean initialDark) {
        this.dark = initialDark;
        this.animPos = initialDark ? 1f : 0f;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
        });
    }

    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        actionListeners.remove(l);
    }

    public boolean isDark() {
        return dark;
    }

    /**
     * Programmatically set dark mode (animates).
     */
    public void setDark(boolean targetDark) {
        if (this.dark == targetDark) return;
        animateTo(targetDark);
        this.dark = targetDark;
        // notify listeners
        ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, dark ? "dark" : "light");
        for (ActionListener al : new ArrayList<>(actionListeners)) al.actionPerformed(ev);
    }

    public void toggle() {
        setDark(!dark);
    }

    private void animateTo(boolean targetDark) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        final float start = animPos;
        final float end = targetDark ? 1f : 0f;
        final long startTs = System.currentTimeMillis();
        animTimer = new Timer(10, null);
        animTimer.addActionListener(e -> {
            float t = (float) (System.currentTimeMillis() - startTs) / animMs;
            if (t >= 1f) {
                animPos = end;
                animTimer.stop();
                repaint();
                return;
            }
            // smooth ease-out
            float s = (float) (1 - Math.pow(1 - t, 3));
            animPos = start + (end - start) * s;
            repaint();
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // track (rounded)
        int arc = HEIGHT;
        Color lightTrack = ThemeColors.TRACK_LIGHT;
        Color darkTrack = ThemeColors.TRACK_DARK;
        float p = animPos;
        Color trackCol = lerp(lightTrack, darkTrack, p);
        g2.setColor(trackCol);
        g2.fillRoundRect(0, 0, WIDTH, HEIGHT, arc, arc);

        // knob position
        // left padding = 4, right padding = 4, extra spacing to center knob nicely
        int available = WIDTH - KNOB_SIZE - 8; // 8 px total side padding
        int knobX = (int) (4 + available * animPos);
        int knobY = (HEIGHT - KNOB_SIZE) / 2;

        // knob shadow (slightly larger for heft)
        g2.setColor(ThemeColors.KNOB_SHADOW);
        g2.fillOval(knobX, knobY + 2, KNOB_SIZE, KNOB_SIZE);

        // knob fill (white on light, near-theme on dark)
        Color knobColor = lerp(ThemeColors.CONSOLE_TEXT_BG, ThemeColors.THEME_GREEN, p);
        g2.setColor(knobColor);
        g2.fillOval(knobX, knobY, KNOB_SIZE, KNOB_SIZE);

        g2.dispose();
    }

    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int gr = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, gr, bl);
    }
}
