package pdfproject.window.components.header;

import pdfproject.window.components.controls.ThemeToggle;
import pdfproject.window.core.Theme;
import pdfproject.window.utils.ThemeManager;
import pdfproject.window.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeaderPanel extends JPanel implements PropertyChangeListener {

    private final JLabel welcomePrefix;
    private final JLabel userNameLabel;
    private final JLabel timeLabel;
    private final ThemeToggle themeToggle;

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public HeaderPanel() {
        super(new BorderLayout());
        setOpaque(true);

        // --- Username + time labels ---
        String raw = System.getProperty("user.name", "User").trim();
        String user = raw.isEmpty() ? "User" : Character.toUpperCase(raw.charAt(0)) + raw.substring(1);

        welcomePrefix = new JLabel("Welcome,");
        welcomePrefix.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 12)));

        userNameLabel = new JLabel(user);
        userNameLabel.setFont(UiScale.getScaledFont(new Font("Segoe UI Semibold", Font.BOLD, 14)));

        timeLabel = new JLabel(TIME_FORMAT.format(LocalDateTime.now()));
        timeLabel.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 10)));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, UiScale.scaleInt(8), 0, 0));

        // Left row: welcome + username + dot + time
        JPanel leftRow = new JPanel(new FlowLayout(FlowLayout.LEFT, UiScale.scaleInt(8), 0));
        leftRow.setOpaque(false);
        leftRow.add(welcomePrefix);
        leftRow.add(userNameLabel);
        JLabel dot = new JLabel("·");
        dot.setBorder(BorderFactory.createEmptyBorder(0, UiScale.scaleInt(4), 0, 0));
        leftRow.add(dot);
        leftRow.add(timeLabel);
        add(centerVertically(leftRow), BorderLayout.WEST);

        // Theme toggle (regular size) — use UiScale to pick height
        int baseHeight = UiScale.scaleInt(20);
        themeToggle = new ThemeToggle(baseHeight);
        themeToggle.setToolTipText("Toggle theme (Space)");
        themeToggle.setToggleState(ThemeManager.isDarkMode());

        themeToggle.setToggleListener(ThemeManager::setDarkMode);

        JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, UiScale.scaleInt(8), 0));
        rightRow.setOpaque(false);
        rightRow.add(themeToggle);
        add(centerVertically(rightRow), BorderLayout.EAST);

        // center filler
        JPanel centerFiller = new JPanel();
        centerFiller.setOpaque(false);
        add(centerFiller, BorderLayout.CENTER);

        // apply theme & register
        Theme t = ThemeManager.getTheme();
        applyTheme(t, ThemeManager.isDarkMode());
        ThemeManager.register(this);
    }

    private static JPanel centerVertically(JComponent child) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0; c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        wrapper.add(child, c);
        return wrapper;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            Theme t = ThemeManager.getTheme();
            applyTheme(t, ThemeManager.isDarkMode());
        });
    }

    private void applyTheme(Theme t, boolean dark) {
        if (t == null) return;
        setBackground(t.headerBg);
        welcomePrefix.setForeground(t.headerText);
        timeLabel.setForeground(t.headerText);
        userNameLabel.setForeground(t.usernameAccent);

        // update toggle colors
        themeToggle.updateTheme(t, dark);
        themeToggle.setToggleState(dark);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
        themeToggle.dispose();
    }
}
