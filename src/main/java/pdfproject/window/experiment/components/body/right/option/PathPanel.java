package pdfproject.window.experiment.components.body.right.option;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PathPanel extends JPanel implements PropertyChangeListener {

    public PathPanel() {
        setOpaque(true);
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
        ThemeManager.unregister(this);
        super.removeNotify();
    }

    private void applyTheme(ExperimentTheme t) {
        if (t != null) setBackground(t.bodyBg);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }
}
