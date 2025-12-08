package pdfproject.window.experiment.components.body.right;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ColorPanel — placeholder panel for color pickers / palettes.
 * Minimal implementation and registered with ThemeManager so it follows theming.
 */
public class ColorPanel extends JPanel implements PropertyChangeListener {

    public ColorPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setOpaque(true);

        // intentionally empty for now — add color controls later

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;
        setBackground(t.bodyBg);
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void removeNotify() {
        ThemeManager.unregister(this);
        super.removeNotify();
    }
}
