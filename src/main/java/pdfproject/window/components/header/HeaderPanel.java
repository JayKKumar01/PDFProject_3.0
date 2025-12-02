package pdfproject.window.components.header;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.utils.AppSettings;
import pdfproject.window.components.body.right.Helper;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HeaderPanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private final ToggleSwitch toggleSwitch;
    private final JLabel toggleLabel;

    private final JLabel openedTimeLabel; // NEW

    // made fields so applyTheme can update them
    private final JPanel inner;
    private final JLabel welcomeText;
    private final JLabel nameText;

    private final List<ActionListener> listeners = new ArrayList<>();

    private final DateTimeFormatter dtf =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public HeaderPanel() {

        // OUTER PANEL
        setLayout(new BorderLayout());
        setBackground(ThemeColors.LAYOUT_BORDER);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // INNER
        inner = new JPanel(new BorderLayout());
        inner.setBackground(ThemeColors.BACKGROUND);
        inner.setBorder(new EmptyBorder(10, 14, 10, 14));

        // USERNAME
        String rawName = System.getProperty("user.name", "User");
        String displayedName = formatName(rawName);

        // LEFT (HORIZONTAL SINGLE LINE)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        welcomeText = new JLabel("Welcome,");
        welcomeText.setForeground(ThemeColors.THEME_BLUE);
        welcomeText.setFont(new Font("Segoe UI", Font.BOLD, 18));

        nameText = new JLabel(displayedName);
        nameText.setForeground(ThemeColors.TEXT_STRONG);
        nameText.setFont(new Font("Segoe UI", Font.PLAIN, 17));

        // OPENED TIME (set immediately)
        openedTimeLabel = new JLabel();
        openedTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        openedTimeLabel.setForeground(ThemeColors.TEXT_MUTED);
        openedTimeLabel.setBorder(new EmptyBorder(0, 4, 0, 0));

        // Set initial time right here
        setOpenedTime(LocalDateTime.now());

        left.add(welcomeText);
        left.add(nameText);
        left.add(new JLabel("·"));
        left.add(openedTimeLabel);

        // RIGHT
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        toggleLabel = new JLabel();
        toggleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // color will be set by applyTheme when ThemeManager state is applied

        boolean savedDark = AppSettings.loadTheme(false);
        // set initial ThemeManager state to saved value
        ThemeManager.setDarkMode(savedDark);

        toggleSwitch = new ToggleSwitch(savedDark);
        toggleSwitch.addActionListener(e -> {
            boolean nowDark = toggleSwitch.isDark();
            toggleLabel.setText(nowDark ? "Dark" : "Light");
            AppSettings.saveTheme(nowDark);
            // update global theme; ThemeManager will notify all registered components
            ThemeManager.setDarkMode(nowDark);

            // keep legacy behavior: notify local listeners for toggle specifically
            ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, nowDark ? "dark" : "light");
            for (ActionListener al : new ArrayList<>(listeners)) al.actionPerformed(ev);
        });

        toggleLabel.setText(toggleSwitch.isDark() ? "Dark" : "Light");

        right.add(toggleLabel);
        right.add(toggleSwitch);

        inner.add(left, BorderLayout.WEST);
        inner.add(right, BorderLayout.EAST);

        add(inner, BorderLayout.CENTER);

        // register to ThemeManager to receive theme changes
        ThemeManager.register(this);

        // apply initial theme to header
        applyTheme(ThemeManager.isDarkMode());
    }

    /**
     * Set opened time (you can call this again if needed)
     */
    public void setOpenedTime(LocalDateTime time) {
        if (time == null) {
            openedTimeLabel.setText("");
            openedTimeLabel.setToolTipText(null);
            return;
        }
        String txt = dtf.format(time);
        openedTimeLabel.setText(txt);
        openedTimeLabel.setToolTipText(txt);
    }

    public void setOpenedTime(String text) {
        openedTimeLabel.setText(text);
        openedTimeLabel.setToolTipText(text);
    }

    public void addToggleListener(ActionListener l) { listeners.add(l); }
    public void removeToggleListener(ActionListener l) { listeners.remove(l); }
    public boolean isDark() { return toggleSwitch.isDark(); }

    private String formatName(String name) {
        if (name == null || name.isBlank()) return "User";
        name = name.trim().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public void onStart() { Helper.setEnabledRecursively(this, false); }

    @Override
    public void onStop() { Helper.setEnabledRecursively(this, true); }

    /**
     * Called by ThemeManager when the theme changes.
     */
    @Override
    public void onThemeChanged(boolean dark) {
        // ensure toggle state is consistent with global state (avoid loops if already set)
        if (toggleSwitch.isDark() != dark) {
            // update without firing ThemeManager again — use setDark which notifies action listeners.
            // but to avoid double-calling ThemeManager.setDarkMode, we simply set the switch visually.
            toggleSwitch.setDark(dark);
        }
        applyTheme(dark);
    }

    /**
     * Update header UI to reflect the given theme.
     */
    private void applyTheme(boolean dark) {
        if (dark) {
            setBackground(ThemeColors.DARK_LAYOUT_BORDER);
            inner.setBackground(ThemeColors.DARK_BACKGROUND);

            welcomeText.setForeground(ThemeColors.THEME_GREEN); // accent in dark = green
            nameText.setForeground(ThemeColors.DARK_TEXT_STRONG);
            openedTimeLabel.setForeground(ThemeColors.DARK_TEXT_MUTED);
            // toggle label should be green in dark
            toggleLabel.setForeground(ThemeColors.THEME_GREEN);
        } else {
            setBackground(ThemeColors.LAYOUT_BORDER);
            inner.setBackground(ThemeColors.BACKGROUND);

            welcomeText.setForeground(ThemeColors.THEME_BLUE);
            nameText.setForeground(ThemeColors.TEXT_STRONG);
            openedTimeLabel.setForeground(ThemeColors.TEXT_MUTED);
            // toggle label should be blue in light
            toggleLabel.setForeground(ThemeColors.THEME_BLUE);
        }

        // repaint to reflect immediate changes
        revalidate();
        repaint();
    }
}
