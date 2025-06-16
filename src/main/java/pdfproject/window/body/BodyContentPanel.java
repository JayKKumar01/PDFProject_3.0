package pdfproject.window.body;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.body.left.LeftPanel;
import pdfproject.window.body.right.RightPanel;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class BodyContentPanel extends JPanel {

    private final LeftPanel leftWrapper;
    private final RightPanel rightWrapper;

    public BodyContentPanel() {
        setLayout(null); // We'll manually set bounds
        setBackground(ThemeColors.BACKGROUND);

        leftWrapper = new LeftPanel(taskStateListener);
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
    private final TaskStateListener taskStateListener = new TaskStateListener() {
        @Override
        public void onStart() {
            rightWrapper.onStart();
        }

        @Override
        public void onStop() {
            rightWrapper.onStop();
        }
    };
}
