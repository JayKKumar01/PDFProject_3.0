package pdfproject.window.experiment.components;


import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Simple header with left title and right toggle button.
 * Toggle flips ThemeManager.isDarkMode() and ThemeManager persists it off-EDT.
 */
public class HeaderExperimentPanel extends JPanel implements PropertyChangeListener {

    private final JLabel title;
    private final JButton toggleBtn;

    public HeaderExperimentPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);

        title = new JLabel("Header (Window3)", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        toggleBtn = new JButton();
        toggleBtn.setFocusable(false);
        toggleBtn.addActionListener(this::onToggleClicked);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        right.setOpaque(false);
        right.add(toggleBtn);

        add(title, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        // initial theme apply
        applyTheme(ThemeManager.getTheme(), ThemeManager.isDarkMode());

        // register for updates (ThemeManager will be initialized before UI)
        ThemeManager.register(this);
    }

    private void onToggleClicked(ActionEvent ev) {
        // flip theme — ThemeManager ensures no-op if unchanged and persists off-EDT
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
    }

    private void applyTheme(ExperimentTheme theme, boolean isDark) {
        setBackground(theme.headerBg);
        title.setForeground(theme.headerText);

        toggleBtn.setText(isDark ? "Dark" : "Light");
        toggleBtn.setBackground(theme.headerBg.darker());
        toggleBtn.setForeground(theme.headerText);
        toggleBtn.setBorder(BorderFactory.createLineBorder(theme.headerText));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // property name is "dark" — update UI on EDT
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme(), ThemeManager.isDarkMode()));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
