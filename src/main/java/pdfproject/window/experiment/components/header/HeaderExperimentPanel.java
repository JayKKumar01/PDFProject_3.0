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
        setLayout(new BorderLayout());
        setOpaque(true);

        // ---------------------------
        // USERNAME FORMAT
        // ---------------------------
        String raw = System.getProperty("user.name", "User").trim();
        String user = raw.isEmpty()
                ? "User"
                : Character.toUpperCase(raw.charAt(0)) + raw.substring(1);

        welcomePrefix = new JLabel("Welcome,");
        welcomePrefix.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 12)));

        userNameLabel = new JLabel(user);
        userNameLabel.setFont(UiScale.getScaledFont(new Font("Segoe UI Semibold", Font.BOLD, 14)));

        timeLabel = new JLabel(TIME_FORMAT.format(LocalDateTime.now()));
        timeLabel.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 10)));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, UiScale.scaleInt(10), 0, 0));

        // ---------------------------
        // LEFT ROW (one line)
        // ---------------------------
        JPanel leftRow = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                UiScale.scaleInt(8),
                0
        ));
        leftRow.setOpaque(false);

        leftRow.add(welcomePrefix);
        leftRow.add(userNameLabel);
        leftRow.add(new JLabel("·"));
        leftRow.add(timeLabel);

        // wrapper = apply vertical centering
        JPanel leftWrapper = new JPanel();
        leftWrapper.setOpaque(false);
        leftWrapper.setLayout(new BoxLayout(leftWrapper, BoxLayout.Y_AXIS));
        leftWrapper.add(Box.createVerticalGlue());
        leftWrapper.add(leftRow);
        leftWrapper.add(Box.createVerticalGlue());

        add(leftWrapper, BorderLayout.WEST);

        // ---------------------------
        // Right toggle button (centered)
        // ---------------------------
        toggleBtn = new JButton();
        toggleBtn.setFocusable(false);
        toggleBtn.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 12)));
        toggleBtn.addActionListener(this::onToggleClicked);

        JPanel rightRow = new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                UiScale.scaleInt(8),
                0
        ));
        rightRow.setOpaque(false);
        rightRow.add(toggleBtn);

        JPanel rightWrapper = new JPanel();
        rightWrapper.setOpaque(false);
        rightWrapper.setLayout(new BoxLayout(rightWrapper, BoxLayout.Y_AXIS));
        rightWrapper.add(Box.createVerticalGlue());
        rightWrapper.add(rightRow);
        rightWrapper.add(Box.createVerticalGlue());

        add(rightWrapper, BorderLayout.EAST);

        // Apply theme
        applyTheme(ThemeManager.getTheme(), ThemeManager.isDarkMode());
        ThemeManager.register(this);
    }

    private void onToggleClicked(ActionEvent ev) {
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
    }

    private void applyTheme(ExperimentTheme t, boolean dark) {
        setBackground(t.headerBg);

        welcomePrefix.setForeground(t.headerText);
        timeLabel.setForeground(t.headerText);
        userNameLabel.setForeground(t.usernameAccent);

        toggleBtn.setText(dark ? "Dark" : "Light");
        toggleBtn.setForeground(t.headerText);
        toggleBtn.setBackground(t.headerBg.darker());
        toggleBtn.setBorder(BorderFactory.createLineBorder(t.headerText));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
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
