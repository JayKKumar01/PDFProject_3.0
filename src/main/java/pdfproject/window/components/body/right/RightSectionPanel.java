package pdfproject.window.components.body.right;

import pdfproject.window.components.ValidationAwarePanel;
import pdfproject.window.components.body.right.color.ColorPanel;
import pdfproject.window.components.body.right.option.OptionPanel;
import pdfproject.window.components.common.SplitTwoPanel;
import pdfproject.window.core.Theme;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * RightSectionPanel — composes OptionPanel (20%) above ColorPanel (80%)
 * using the SplitTwoPanel (vertical, DPI-aware separator).
 */
public class RightSectionPanel extends ValidationAwarePanel implements PropertyChangeListener {

    private final OptionPanel optionPanel;
    private final ColorPanel colorPanel;

    // 20% for the option area, 80% for color area
    private static final double OPTION_RATIO = 0.19;

    public RightSectionPanel() {
        super(new BorderLayout());

        optionPanel = new OptionPanel();
        colorPanel = new ColorPanel();

        // vertical split: top | separator | bottom; 20% top, 80% bottom
        SplitTwoPanel split = new SplitTwoPanel(SplitTwoPanel.Orientation.VERTICAL, 1, OPTION_RATIO);
        split.setComponents(optionPanel, colorPanel);

        add(split, BorderLayout.CENTER);

        // initial theme apply will happen in addNotify, but do a best-effort now
        applyTheme(ThemeManager.getTheme());
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
        Color bg = t.bodyBg;
        setBackground(bg);
        // ensure children (if JComponents) use same body bg so seam is invisible
        if (optionPanel != null) optionPanel.setBackground(bg);
        if (colorPanel != null) colorPanel.setBackground(bg);
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // ensure EDT
        if (SwingUtilities.isEventDispatchThread()) applyTheme(ThemeManager.getTheme());
        else SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    // Accessors
    public OptionPanel getOptionPanel() { return optionPanel; }
    public ColorPanel getColorPanel() { return colorPanel; }
}
