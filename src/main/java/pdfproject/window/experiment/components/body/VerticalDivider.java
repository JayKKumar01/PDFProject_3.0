package pdfproject.window.experiment.components.body;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A simple themed vertical line divider.
 */
public class VerticalDivider extends JPanel implements PropertyChangeListener {

    public VerticalDivider() {
        setPreferredSize(new Dimension(1, 1));
        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        // A very subtle divider using readableForeground at ~30% opacity
        Color fg = ExperimentTheme.readableForeground(t.bodyBg);
        Color line = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 60); // translucent

        setBackground(line);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
