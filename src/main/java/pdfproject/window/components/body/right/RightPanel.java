package pdfproject.window.components.body.right;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;

public class RightPanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private static final int PADDING = 10;
    private static final int GAP = 10;
    private static final double IMAGE_QUALITY_RATIO = 0.3;

    private final ImageOptionPanel imageOptionPanel;
    private final CustomColorPanel customColorPanel;

    public RightPanel() {
        setLayout(null);
        setBackground(ThemeColors.LAYOUT_BORDER);

        imageOptionPanel = new ImageOptionPanel();
        customColorPanel = new CustomColorPanel();

        add(imageOptionPanel);
        add(customColorPanel);

        // register & apply initial theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
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

        imageOptionPanel.setBounds(xPos, yImage, contentWidth, imageHeight);
        customColorPanel.setBounds(xPos, yColor, contentWidth, colorHeight);
    }

    @Override
    public void onStart() {
        imageOptionPanel.onStart();
        customColorPanel.onStart();
    }

    @Override
    public void onStop() {
        imageOptionPanel.onStop();
        customColorPanel.onStop();
    }

    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        setBackground(dark ? ThemeColors.DARK_LAYOUT_BORDER : ThemeColors.LAYOUT_BORDER);

        // children (ImageOptionPanel and CustomColorPanel) already handle their own theme changes

        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
