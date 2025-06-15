package pdfproject.windowui.body;

import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {

    public RightPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.LAYOUT_BORDER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel view = new JPanel(new BorderLayout());
        view.setBackground(ThemeColors.BACKGROUND);

        add(view, BorderLayout.CENTER);
    }
}
