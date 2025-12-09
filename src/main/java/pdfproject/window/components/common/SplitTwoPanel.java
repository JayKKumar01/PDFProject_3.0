package pdfproject.window.components.common;

import pdfproject.window.core.Theme;
import pdfproject.window.utils.ThemeManager;
import pdfproject.window.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * SplitTwoPanel — manual layout, optimized, ratio aware.
 *
 * Horizontal: left | separator | right
 * Vertical:   top  | separator | bottom
 *
 * firstFraction =
 *   -1.0  => equal split
 *   0.0–1.0 => fraction of remaining space for first component
 */
public final class SplitTwoPanel extends JPanel implements PropertyChangeListener {

    public enum Orientation { HORIZONTAL, VERTICAL }

    private final Orientation orientation;
    private final boolean isHorizontal;
    private final int thicknessDp;
    private double firstFraction; // now mutable (supports ratio changes)

    private final JComponent separator;
    private Component first, second;

    private int lastSepPx = -1;
    private Color lastAppliedBg = null;

    public SplitTwoPanel(Orientation orientation, int thicknessDp, double firstFraction) {
        super(null); // manual layout
        this.orientation = orientation == null ? Orientation.HORIZONTAL : orientation;
        this.isHorizontal = this.orientation == Orientation.HORIZONTAL;
        this.thicknessDp = Math.max(1, thicknessDp);
        setFraction(firstFraction);

        separator = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        separator.setOpaque(true);
        separator.setBorder(null);
    }

    /** Default: horizontal equal split. */
    public SplitTwoPanel() { this(Orientation.HORIZONTAL, 1, -1.0); }

    /** Set ratio dynamically. */
    public void setFraction(double fraction) {
        if (fraction > 0 && fraction < 1) this.firstFraction = fraction;
        else this.firstFraction = -1.0; // equal halves
        revalidate();
        repaint();
    }

    /** Install components. */
    public void setComponents(Component first, Component second) {
        if (this.first != null) remove(this.first);
        if (this.second != null) remove(this.second);
        if (separator.getParent() != null) remove(separator);

        this.first = first;
        this.second = second;

        if (first != null) add(first);
        add(separator);
        if (second != null) add(second);

        lastAppliedBg = null;
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        Insets ins = getInsets();
        final int x0 = ins.left;
        final int y0 = ins.top;
        final int totalW = Math.max(0, getWidth() - ins.left - ins.right);
        final int totalH = Math.max(0, getHeight() - ins.top - ins.bottom);

        if (totalW <= 0 || totalH <= 0) return;

        final int sepPx = sepPx();
        if (sepPx != lastSepPx) {
            updateSeparatorPreferred(sepPx);
            lastSepPx = sepPx;
        }

        if (isHorizontal) {
            final int remaining = Math.max(0, totalW - sepPx);
            final int firstW = (firstFraction > 0) ? (int) Math.round(remaining * firstFraction) : remaining / 2;
            final int secondW = remaining - firstW;

            if (first != null) first.setBounds(x0, y0, firstW, totalH);
            separator.setBounds(x0 + firstW, y0, sepPx, totalH);
            if (second != null) second.setBounds(x0 + firstW + sepPx, y0, secondW, totalH);

        } else {
            final int remaining = Math.max(0, totalH - sepPx);
            final int firstH = (firstFraction > 0) ? (int) Math.round(remaining * firstFraction) : remaining / 2;
            final int secondH = remaining - firstH;

            if (first != null) first.setBounds(x0, y0, totalW, firstH);
            separator.setBounds(x0, y0 + firstH, totalW, sepPx);
            if (second != null) second.setBounds(x0, y0 + firstH + sepPx, totalW, secondH);
        }
    }

    private int sepPx() {
        return Math.max(1, UiScale.scaleInt(thicknessDp));
    }

    private void updateSeparatorPreferred(int px) {
        if (isHorizontal) {
            separator.setPreferredSize(new Dimension(px, 1));
            separator.setMinimumSize(new Dimension(px, 1));
            separator.setMaximumSize(new Dimension(px, Integer.MAX_VALUE));
        } else {
            separator.setPreferredSize(new Dimension(1, px));
            separator.setMinimumSize(new Dimension(1, px));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, px));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d1 = (first != null) ? first.getPreferredSize() : new Dimension(0, 0);
        Dimension d2 = (second != null) ? second.getPreferredSize() : new Dimension(0, 0);
        final int sep = sepPx();
        Insets i = getInsets();

        if (isHorizontal) {
            return new Dimension(
                    d1.width + sep + d2.width + i.left + i.right,
                    Math.max(d1.height, d2.height) + i.top + i.bottom
            );
        } else {
            return new Dimension(
                    Math.max(d1.width, d2.width) + i.left + i.right,
                    d1.height + sep + d2.height + i.top + i.bottom
            );
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }

    private void applyTheme(Theme t) {
        if (t == null) return;
        separator.setBackground(t.usernameAccent);

        Color bg = t.bodyBg;
        if (bg != lastAppliedBg) {
            if (first instanceof JComponent j1) j1.setBackground(bg);
            if (second instanceof JComponent j2) j2.setBackground(bg);
            lastAppliedBg = bg;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }
}
