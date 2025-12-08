package pdfproject.window.experiment.components.body.left;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Minimal empty launcher panel (clean, theme-aware, ready to add controls later).
 */
public class LauncherPanel extends JPanel implements PropertyChangeListener {

    private final JLabel titleLabel;

    public LauncherPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 6));

        titleLabel = new JLabel("Launcher", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(titleLabel, BorderLayout.NORTH);

        // Placeholder empty space
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        add(empty, BorderLayout.CENTER);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.bodyBg);
        titleLabel.setForeground(t.headerText);
        repaint();
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
