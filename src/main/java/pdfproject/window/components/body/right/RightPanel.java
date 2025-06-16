package pdfproject.window.components.body.right;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;

public class RightPanel extends JPanel implements TaskStateListener {

    private final ImageQualityPanel imageQualityPanel;
    private final CustomColorPanel customColorPanel;

    private static final int PADDING = 10;
    private static final int GAP = 10;

    public RightPanel() {
        setLayout(null); // Manual layout for precise height ratios
        setBackground(ThemeColors.LAYOUT_BORDER);

        imageQualityPanel = new ImageQualityPanel();
        customColorPanel = new CustomColorPanel();

        add(imageQualityPanel);
        add(customColorPanel);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        int availableHeight = height - 2 * PADDING - GAP;
        int imageHeight = (int) (availableHeight * 0.3);
        int colorHeight = availableHeight - imageHeight;

        int contentWidth = width - 2 * PADDING;
        int left = PADDING;

        imageQualityPanel.setBounds(left, PADDING, contentWidth, imageHeight);
        customColorPanel.setBounds(left, PADDING + imageHeight + GAP, contentWidth, colorHeight);
    }

    @Override
    public void onStart() {
        imageQualityPanel.onStart();
        customColorPanel.onStart();
    }

    @Override
    public void onStop() {
        imageQualityPanel.onStop();
        customColorPanel.onStop();
    }
}
