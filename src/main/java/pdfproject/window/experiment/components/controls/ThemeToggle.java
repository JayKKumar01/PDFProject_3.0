package pdfproject.window.experiment.components.controls;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.Objects;

/**
 * Simple, theme-aware sliding pill toggle without animation.
 * - Caller should pass a baseHeight already scaled via UiScale.scaleInt(...)
 * - Toggling snaps immediately and repaints the component.
 */
public class ThemeToggle extends JComponent implements MouseListener, KeyListener, FocusListener, ComponentListener {
    @Serial
    private static final long serialVersionUID = 1L;

    // sizing (caller passes scaled height)
    private final int baseHeight;
    private int widthPx, padding, knobDiameter;
    private float knobRange; // float used for position calc

    // state (no animation)
    private boolean toggled = false;

    // theme colors
    private Color trackOn, trackOff, knobFill, knobBorder;

    // listener
    private ToggleListener toggleListener;

    public ThemeToggle(int baseHeight) {
        // baseHeight must be scaled by caller (UiScale.scaleInt(...))
        this.baseHeight = Math.max(14, baseHeight);

        setFocusable(true);
        setOpaque(false);

        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(this);
        addComponentListener(this);

        computeMetrics();
        Dimension pref = new Dimension(widthPx, this.baseHeight);
        setPreferredSize(pref);
        setMinimumSize(pref);
        setMaximumSize(pref);

        setLightDefaults();
    }

    private void computeMetrics() {
        int h = baseHeight;
        padding = Math.max(2, Math.max(2, h / 6));
        knobDiameter = h - padding * 2;
        widthPx = Math.max((int) (h * 2.2), knobDiameter + padding * 2 + UiScale.scaleInt(10));
        knobRange = (float) (widthPx - padding * 2 - knobDiameter);
    }

    private void setLightDefaults() {
        trackOn = ExperimentTheme.Slider.TRACK_ON_LIGHT;
        trackOff = ExperimentTheme.Slider.TRACK_OFF_LIGHT;
        knobFill = ExperimentTheme.Slider.KNOB_FILL_LIGHT;
        knobBorder = ExperimentTheme.Slider.KNOB_BORDER_LIGHT;
    }

    /**
     * Update theme colors and repaint.
     */
    public void updateTheme(ExperimentTheme theme, boolean dark) {
        Objects.requireNonNull(theme);
        if (dark) {
            trackOn = ExperimentTheme.Slider.TRACK_ON_DARK;
            trackOff = ExperimentTheme.Slider.TRACK_OFF_DARK;
            knobFill = ExperimentTheme.Slider.KNOB_FILL_DARK;
            knobBorder = ExperimentTheme.Slider.KNOB_BORDER_DARK;
        } else {
            trackOn = ExperimentTheme.Slider.TRACK_ON_LIGHT;
            trackOff = ExperimentTheme.Slider.TRACK_OFF_LIGHT;
            knobFill = ExperimentTheme.Slider.KNOB_FILL_LIGHT;
            knobBorder = ExperimentTheme.Slider.KNOB_BORDER_LIGHT;
        }
        repaint();
    }

    /** Snap immediately to provided state (no animation). */
    public void setToggleState(boolean darkOn) {
        toggled = darkOn;
        repaint();
    }

    public void dispose() {
        removeMouseListener(this);
        removeKeyListener(this);
        removeFocusListener(this);
        removeComponentListener(this);
    }

    private void toggle() {
        toggled = !toggled;
        repaint();
        if (toggleListener != null) toggleListener.onToggled(toggled);
    }

    private int computeKnobX(float pos) {
        // pos = 0 or 1 (no animation), knobRange may be float but we round final pixel
        return padding + Math.round(knobRange * pos);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int compH = getHeight();
            int trackH = baseHeight;
            int y = (compH - trackH) / 2;
            int trackW = widthPx;
            int trackX = 0;

            float pos = toggled ? 1f : 0f;
            Color trackColor = blend(trackOff, trackOn, pos);
            g2.setPaint(trackColor);
            g2.fillRoundRect(trackX, y, trackW, trackH, trackH, trackH);

            int knobX = computeKnobX(pos);
            int knobY = (compH - knobDiameter) / 2;

            // subtle shadow
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
            g2.setColor(Color.black);
            g2.fillOval(knobX, knobY + 1, knobDiameter, knobDiameter);
            g2.setComposite(AlphaComposite.SrcOver);

            // knob
            g2.setColor(knobFill);
            g2.fillOval(knobX, knobY, knobDiameter, knobDiameter);

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(knobBorder);
            g2.drawOval(knobX, knobY, knobDiameter, knobDiameter);

            if (isFocusOwner()) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(trackX + 1, y + 1, trackW - 2, trackH - 2, trackH, trackH);
            }
        } finally {
            g2.dispose();
        }
    }

    private static Color blend(Color a, Color b, float t) {
        float it = 1f - t;
        int r = Math.round(a.getRed() * it + b.getRed() * t);
        int g = Math.round(a.getGreen() * it + b.getGreen() * t);
        int bl = Math.round(a.getBlue() * it + b.getBlue() * t);
        int alpha = Math.round(a.getAlpha() * it + b.getAlpha() * t);
        return new Color(r, g, bl, alpha);
    }

    // Mouse
    @Override public void mouseClicked(MouseEvent e) { toggle(); requestFocusInWindow(); }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
    @Override public void mouseExited(MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }

    // Keys
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
            toggle();
            e.consume();
        }
    }
    @Override public void keyReleased(KeyEvent e) {}

    // Focus
    @Override public void focusGained(FocusEvent e) { repaint(); }
    @Override public void focusLost(FocusEvent e) { repaint(); }

    // ComponentListener
    @Override public void componentResized(ComponentEvent e) {
        computeMetrics();
        Dimension pref = new Dimension(widthPx, baseHeight);
        setPreferredSize(pref);
        revalidate();
        repaint();
    }
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) {}
    @Override public void componentHidden(ComponentEvent e) {}

    // Toggle listener
    public interface ToggleListener { void onToggled(boolean darkOn); }
    public void setToggleListener(ToggleListener l) { this.toggleListener = l; }
}
