package pdfproject.windowui.body;

import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class BodyContentPanel extends JPanel {

    private final LeftPanel leftPanel;
    private final RightPanel rightPanel;

    public BodyContentPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BACKGROUND);

        leftPanel = new LeftPanel();
        rightPanel = new RightPanel();

        // Combine left and right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5); // 50% width each
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }
}
