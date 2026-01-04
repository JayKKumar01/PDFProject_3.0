package pdfproject.window.components.header;

import pdfproject.models.UserTimeRecord;
import pdfproject.services.UserTimeRecordService;
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

        // Create model
        UserTimeRecord record = new UserTimeRecord(
                System.getProperty("user.name", "User"),
                LocalDateTime.now()
        );

        // Future DB save point
        UserTimeRecordService service = new UserTimeRecordService();
        service.save(record);

        JLabel welcomeLabel = new JLabel("Welcome,");
        welcomeLabel.setForeground(ThemeManager.ACCENT_PRIMARY);
        welcomeLabel.putClientProperty("FlatLaf.style", "font: 15");

        JLabel userLabel = new JLabel(record.getUsername());
        userLabel.setForeground(ThemeManager.USERNAME_HIGHLIGHT);
        userLabel.putClientProperty("FlatLaf.style", "font: 15 bold");

        JLabel separator = new JLabel("•");
        separator.setForeground(ThemeManager.CONTENT_TEXT);

        JLabel timeLabel = new JLabel(
                record.getTimestamp().format(FORMATTER)
        );
        timeLabel.setForeground(ThemeManager.ACCENT_SOFT);
        timeLabel.putClientProperty("FlatLaf.style", "font: 11");

        add(welcomeLabel);
        add(userLabel);
        add(separator);
        add(timeLabel);
    }
}
