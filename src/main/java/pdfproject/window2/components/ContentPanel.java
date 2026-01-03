package pdfproject.window2.components;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {

    public ContentPanel() {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.CONTENT_BG);

        JLabel label = new JLabel("Content Area");
        label.setForeground(ThemeManager.CONTENT_TEXT);

        add(label);
    }
}
