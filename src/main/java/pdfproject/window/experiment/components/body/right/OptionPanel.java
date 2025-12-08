package pdfproject.window.experiment.components.body.right;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * OptionPanel — placeholder panel for option controls.
 * Minimal implementation: wired to ThemeManager so it will update with theme changes.
 */
public class OptionPanel extends JPanel implements PropertyChangeListener {

    public OptionPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setOpaque(true);

        // intentionally empty for now — add controls later

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
