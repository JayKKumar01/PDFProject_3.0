package pdfproject.windowui.body;

import pdfproject.windowui.body.left.LauncherSectionPanel;
import pdfproject.windowui.body.left.InputSectionPanel;
import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {

    public LeftPanel() {
        setLayout(new GridLayout(2, 1, 0, 10)); // 2 rows, 10px vertical gap
        setBackground(ThemeColors.LAYOUT_BORDER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        InputSectionPanel inputSectionPanel = new InputSectionPanel();
        LauncherSectionPanel launcherSectionPanel = new LauncherSectionPanel();

        // Make sure these panels stretch to fill their 50% height
        inputSectionPanel.setPreferredSize(null);
        launcherSectionPanel.setPreferredSize(null);

        add(inputSectionPanel);
        add(launcherSectionPanel);
    }
}
