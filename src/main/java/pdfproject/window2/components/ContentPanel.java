package pdfproject.window2.components;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ContentPanel extends JPanel {

    public ContentPanel() {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.CONTENT_BG);

        // Thin accent border around content
        setBorder(new MatteBorder(
                1, 1, 0, 1,
                ThemeManager.ACCENT_PRIMARY
        ));

        JLabel label = new JLabel("Content Area");
        label.setForeground(ThemeManager.CONTENT_TEXT);

        add(label);
    }
}
