package pdfproject.windowui.body;

import pdfproject.windowui.body.left.LeftPanel;
import pdfproject.windowui.body.right.RightPanel;
import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class BodyContentPanel extends JPanel {

    private final JPanel leftWrapper;
    private final JPanel rightWrapper;

    public BodyContentPanel() {
        setLayout(null); // We'll manually set bounds
        setBackground(ThemeColors.BACKGROUND);

        leftWrapper = new LeftPanel();
        rightWrapper = new RightPanel();

        add(leftWrapper);
        add(rightWrapper);

        // Listen for size changes to recalculate layout
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeSubPanels(getWidth(), getHeight());
            }
        });
    }

    private void resizeSubPanels(int totalWidth, int totalHeight) {
        int halfWidth = totalWidth / 2;

        leftWrapper.setBounds(0, 0, halfWidth, totalHeight);
        rightWrapper.setBounds(halfWidth, 0, totalWidth - halfWidth, totalHeight);
    }
}
