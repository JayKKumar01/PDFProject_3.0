package pdfproject.windowui.body;

import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {

    public RightPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BACKGROUND);

        // Placeholder for now
        add(new JLabel("Right Panel Content"), BorderLayout.CENTER);
    }
}
