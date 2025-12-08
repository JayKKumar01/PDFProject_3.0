package pdfproject.window.experiment.components.header;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;
import pdfproject.window.experiment.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeaderExperimentPanel extends JPanel implements PropertyChangeListener {

    private final JLabel welcomePrefix;
    private final JLabel userNameLabel;
    private final JLabel timeLabel;
    private final JButton toggleBtn;

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public HeaderExperimentPanel() {
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

        // Left row: horizontal flow of welcome + username + dot + time
        JPanel leftRow = new JPanel(new FlowLayout(FlowLayout.LEFT, UiScale.scaleInt(8), 0));
        leftRow.setOpaque(false);
        leftRow.add(welcomePrefix);
        leftRow.add(userNameLabel);
        leftRow.add(new JLabel("·"));
        leftRow.add(timeLabel);

        // Wrap leftRow so it is vertically centered in the header
        add(centerVertically(leftRow), BorderLayout.WEST);

        // --- Toggle button on right ---
        toggleBtn = new JButton();
        toggleBtn.setFocusable(false);
        toggleBtn.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 12)));
        toggleBtn.addActionListener(this::onToggleClicked);

        JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, UiScale.scaleInt(8), 0));
        rightRow.setOpaque(false);
        rightRow.add(toggleBtn);

        // Wrap rightRow so it is vertically centered
        add(centerVertically(rightRow), BorderLayout.EAST);

        // Ensure the center area stays empty (or use it later)
        JPanel centerFiller = new JPanel();
        centerFiller.setOpaque(false);
        add(centerFiller, BorderLayout.CENTER);

        // Apply theme and register
        applyTheme(ThemeManager.getTheme(), ThemeManager.isDarkMode());
        ThemeManager.register(this);
    }

    private void onToggleClicked(ActionEvent ev) {
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
    }

    /**
     * Small helper that returns a JPanel using GridBagLayout which centers the provided child vertically.
     * The returned panel is transparent and expands to parent's height so centering works reliably.
     */
    private static JPanel centerVertically(JComponent child) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;      // allow horizontal expansion
        c.weighty = 1.0;      // allow vertical expansion so centering works
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        wrapper.add(child, c);
        return wrapper;
    }

    private void applyTheme(ExperimentTheme t, boolean dark) {
        if (t == null) return;

        setBackground(t.headerBg);

        welcomePrefix.setForeground(t.headerText);
        timeLabel.setForeground(t.headerText);
        userNameLabel.setForeground(t.usernameAccent);

        toggleBtn.setText(dark ? "Dark" : "Light");
        toggleBtn.setForeground(t.headerText);
        // Slightly darker background for the button to give contrast (but keep consistent)
        Color btnBg = t.headerBg == null ? UIManager.getColor("Panel.background") : t.headerBg.darker();
        toggleBtn.setBackground(btnBg);
        toggleBtn.setBorder(BorderFactory.createLineBorder(t.headerText));
        toggleBtn.setOpaque(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Re-apply theme on EDT
        SwingUtilities.invokeLater(() ->
                applyTheme(ThemeManager.getTheme(), ThemeManager.isDarkMode())
        );
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
