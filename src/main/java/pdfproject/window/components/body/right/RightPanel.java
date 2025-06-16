package pdfproject.window.components.body.right;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;

public class RightPanel extends JPanel implements TaskStateListener {

    private static final int PADDING = 10;
    private static final int GAP = 10;
    private static final double IMAGE_QUALITY_RATIO = 0.3;

    private final ImageQualityPanel imageQualityPanel;
    private final CustomColorPanel customColorPanel;

    public RightPanel() {
        setLayout(null);
        setBackground(ThemeColors.LAYOUT_BORDER);

        imageQualityPanel = new ImageQualityPanel();
        customColorPanel = new CustomColorPanel();

        add(imageQualityPanel);
        add(customColorPanel);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        layoutComponents(width, height);
    }

    private void layoutComponents(int width, int height) {
        int availableHeight = height - 2 * PADDING - GAP;
        int imageHeight = (int) (availableHeight * IMAGE_QUALITY_RATIO);
        int colorHeight = availableHeight - imageHeight;
        int contentWidth = width - 2 * PADDING;

        int xPos = PADDING;
        int yImage = PADDING;
        int yColor = yImage + imageHeight + GAP;

        imageQualityPanel.setBounds(xPos, yImage, contentWidth, imageHeight);
        customColorPanel.setBounds(xPos, yColor, contentWidth, colorHeight);
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
