package pdfproject.window.experiment.components.body.left;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Left half of the body area. Lightweight, themed panel intended to hold controls/settings.
 */
public class LeftSectionPanel extends JPanel implements PropertyChangeListener {

    private final JLabel titleLabel;

    public LeftSectionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        titleLabel = new JLabel("Left — Settings", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Example placeholder center area (you'll replace with real controls)
        JLabel placeholder = new JLabel("<html><i>controls go here</i></html>", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        add(titleLabel, BorderLayout.NORTH);
        add(placeholder, BorderLayout.CENTER);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.bodyBg);
        titleLabel.setForeground(t.headerText);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // ensure theme application happens on EDT
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
