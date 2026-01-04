package pdfproject.window.components.header;

import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeaderLeftPanel extends JPanel {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public HeaderLeftPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));

        String userName = System.getProperty("user.name", "User");

        JLabel welcomeLabel = new JLabel("Welcome,");
        welcomeLabel.setForeground(ThemeManager.ACCENT_SOFT);
        welcomeLabel.putClientProperty("FlatLaf.style", "font: 15");

        JLabel userLabel = new JLabel(userName);
        userLabel.setForeground(ThemeManager.USERNAME_HIGHLIGHT);
        userLabel.putClientProperty("FlatLaf.style", "font: 15 bold");

        JLabel separator = new JLabel("•");
        separator.setForeground(ThemeManager.CONTENT_TEXT);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(FORMATTER));
        timeLabel.setForeground(ThemeManager.CONTENT_TEXT);
        timeLabel.putClientProperty("FlatLaf.style", "font: 11");

        add(welcomeLabel);
        add(userLabel);
        add(separator);
        add(timeLabel);
    }
}
