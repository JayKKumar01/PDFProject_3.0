package pdfproject.window2.components;

import pdfproject.Config;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.HEADER_BG);

        // Padding only (no border)
        setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel(Config.FRAME_NAME);
        title.setForeground(ThemeManager.HEADER_TEXT);
        title.putClientProperty("FlatLaf.style", "font: 16");

        add(title, BorderLayout.WEST);
    }
}
