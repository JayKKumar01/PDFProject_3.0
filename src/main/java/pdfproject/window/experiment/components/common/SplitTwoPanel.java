package pdfproject.window.experiment.components.common;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;
import pdfproject.window.experiment.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * SplitTwoPanel — manual layout, optimized.
 *
 * Horizontal: left | separator | right
 * Vertical:   top  | separator | bottom
 *
 * Separator thickness is specified in dp (logical pixels) and scaled via UiScale.
 * firstFraction in (0,1) determines the fraction for the first child; otherwise equal split.
 */
public final class SplitTwoPanel extends JPanel implements PropertyChangeListener {

    public enum Orientation { HORIZONTAL, VERTICAL }

    private final Orientation orientation;
    private final boolean isHorizontal;
    private final int thicknessDp;
    private final double firstFraction; // <=0 => equal split

    // small custom separator that fully paints its bounds to avoid seams
    private final JComponent separator;

    // children
    private Component first;
    private Component second;

    // caches
    private int lastSepPx = -1;
    private Color lastAppliedBg = null;

    public SplitTwoPanel(Orientation orientation, int thicknessDp, double firstFraction) {
        super(null); // manual layout
        this.orientation = orientation == null ? Orientation.HORIZONTAL : orientation;
        this.isHorizontal = this.orientation == Orientation.HORIZONTAL;
        this.thicknessDp = Math.max(1, thicknessDp);
        this.firstFraction = (firstFraction > 0 && firstFraction < 1) ? firstFraction : -1.0;

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

    public SplitTwoPanel() { this(Orientation.HORIZONTAL, 1, -1.0); }

    /**
     * Install two components (replaces existing ones).
     */
    public void setComponents(Component first, Component second) {
        if (this.first != null) remove(this.first);
        if (this.second != null) remove(this.second);
        if (separator.getParent() != null) remove(separator);

        this.first = first;
        this.second = second;

        if (first != null) add(first);
        add(separator);
        if (second != null) add(second);

        // reset cached bg so applyTheme will reapply to new children
        lastAppliedBg = null;

        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();
        final int x0 = insets.left;
        final int y0 = insets.top;
        final int totalW = Math.max(0, getWidth() - insets.left - insets.right);
        final int totalH = Math.max(0, getHeight() - insets.top - insets.bottom);

        if (totalW <= 0 || totalH <= 0) return;

        final int sepPx = sepPx();
        // ensure separator preferred size is synchronized when sepPx changes
        if (sepPx != lastSepPx) {
            updateSeparatorPreferred(sepPx);
            lastSepPx = sepPx;
        }

        if (isHorizontal) {
            final int remainingW = Math.max(0, totalW - sepPx);
            final int firstW = (firstFraction > 0) ? (int) Math.round(remainingW * firstFraction) : (remainingW / 2);
            final int secondW = remainingW - firstW;

            final int firstX = x0;
            final int sepX = firstX + firstW;
            final int secondX = sepX + sepPx;

            if (first != null) first.setBounds(firstX, y0, firstW, totalH);
            separator.setBounds(sepX, y0, sepPx, totalH);
            if (second != null) second.setBounds(secondX, y0, secondW, totalH);
        } else {
            final int remainingH = Math.max(0, totalH - sepPx);
            final int firstH = (firstFraction > 0) ? (int) Math.round(remainingH * firstFraction) : (remainingH / 2);
            final int secondH = remainingH - firstH;

            final int firstY = y0;
            final int sepY = firstY + firstH;
            final int secondY = sepY + sepPx;

            if (first != null) first.setBounds(x0, firstY, totalW, firstH);
            separator.setBounds(x0, sepY, totalW, sepPx);
            if (second != null) second.setBounds(x0, secondY, totalW, secondH);
        }
    }

    private int sepPx() {
        // small helper to compute DPI-scaled pixel thickness
        return Math.max(1, UiScale.scaleInt(thicknessDp));
    }

    private void updateSeparatorPreferred(int sepPx) {
        if (isHorizontal) {
            separator.setPreferredSize(new Dimension(sepPx, 1));
            separator.setMinimumSize(new Dimension(sepPx, 1));
            separator.setMaximumSize(new Dimension(sepPx, Integer.MAX_VALUE));
        } else {
            separator.setPreferredSize(new Dimension(1, sepPx));
            separator.setMinimumSize(new Dimension(1, sepPx));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, sepPx));
        }
        // do not call revalidate(); doLayout is driving positioning
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d1 = (first != null) ? first.getPreferredSize() : new Dimension(0, 0);
        Dimension d2 = (second != null) ? second.getPreferredSize() : new Dimension(0, 0);
        final int sep = sepPx();
        Insets ins = getInsets();

        if (isHorizontal) {
            final int h = Math.max(d1.height, d2.height);
            final int w = d1.width + sep + d2.width;
            return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
        } else {
            final int w = Math.max(d1.width, d2.width);
            final int h = d1.height + sep + d2.height;
            return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
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

    private void applyTheme(ExperimentTheme theme) {
        if (theme == null) return;

        final Color accent = theme.usernameAccent;
        separator.setBackground(accent);

        // apply body background to child JComponents only if it changed (few ops)
        final Color bodyBg = theme.bodyBg;
        if (bodyBg != lastAppliedBg) {
            if (first instanceof JComponent) ((JComponent) first).setBackground(bodyBg);
            if (second instanceof JComponent) ((JComponent) second).setBackground(bodyBg);
            lastAppliedBg = bodyBg;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (SwingUtilities.isEventDispatchThread()) {
            applyTheme(ThemeManager.getTheme());
        } else {
            SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
        }
    }
}
