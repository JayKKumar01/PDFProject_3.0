package pdfproject.window.experiment.components;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BodyExperimentPanel extends JPanel implements PropertyChangeListener {

    private final JLabel label;

    public BodyExperimentPanel() {
        setLayout(new BorderLayout());
        label = new JLabel("Body (Window3)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(label, BorderLayout.CENTER);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.bodyBg);
        label.setForeground(t.bodyText);
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
