package pdfproject.window.components.body;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.components.body.left.LeftPanel;
import pdfproject.window.components.body.right.RightPanel;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class BodyContentPanel extends JPanel implements TaskStateListener{

    private final LeftPanel leftWrapper;
    private final RightPanel rightWrapper;

    public void setTaskStateListener(TaskStateListener taskStateListener){
        leftWrapper.setTaskStateListener(taskStateListener);
    }

    public BodyContentPanel(){
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

    @Override
    public void onStart() {
        rightWrapper.onStart();
    }

    @Override
    public void onStop() {
        rightWrapper.onStop();
    }
}
