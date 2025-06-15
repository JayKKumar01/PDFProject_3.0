package pdfproject.windowui.body.left;

import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class ButtonSectionPanel extends JPanel {

    public ButtonSectionPanel() {
        setBackground(ThemeColors.BACKGROUND);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(new JLabel("Button Section (e.g., actions, start, reset)"));
    }
}
