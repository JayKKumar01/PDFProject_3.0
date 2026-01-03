package pdfproject.window2.components.header;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class HeaderRightPanel extends JPanel {

    public HeaderRightPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JLabel label = new JLabel("Prodigy Validation");
        label.setForeground(ThemeManager.HEADER_TEXT);
        label.putClientProperty("FlatLaf.style", "font: 13");

        ToggleSwitch toggle = new ToggleSwitch(true);

        add(label);
        add(toggle);
    }
}
