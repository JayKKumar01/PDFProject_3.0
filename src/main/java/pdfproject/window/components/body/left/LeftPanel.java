package pdfproject.window.components.body.left;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LeftPanel extends JPanel implements ThemeManager.ThemeChangeListener {

    private static final int PADDING = 10;
    private static final int DIVIDER_HEIGHT = 10;

    private final JPanel inputWrapper;
    private final JPanel dividerView;
    private final JPanel launcherWrapper;
    private final LauncherSectionPanel launcherSectionPanel;

    public LeftPanel() {
        setLayout(null);
        setBackground(ThemeColors.LAYOUT_BORDER);

        inputWrapper = createWrapper(new InputSectionPanel());
        dividerView = createDivider();
        launcherSectionPanel = new LauncherSectionPanel();
        launcherWrapper = createWrapper(launcherSectionPanel);

        add(inputWrapper);
        add(dividerView);
        add(launcherWrapper);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeSections(getWidth(), getHeight());
            }
        });

        // Register for theme changes and apply current theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
    }

    public void setTaskStateListener(TaskStateListener taskStateListener) {
        launcherSectionPanel.setTaskStateListener(taskStateListener);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        resizeSections(width, height);
    }

    private JPanel createWrapper(JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(ThemeColors.LAYOUT_BORDER);
        return divider;
    }

    private void resizeSections(int width, int height) {
        int usableWidth = width - 2 * PADDING;
        int usableHeight = height - 2 * PADDING - DIVIDER_HEIGHT;
        int inputHeight = usableHeight / 2;
        int launcherHeight = usableHeight - inputHeight;

        int inputY = PADDING;
        int dividerY = inputY + inputHeight;
        int launcherY = dividerY + DIVIDER_HEIGHT;

        inputWrapper.setBounds(PADDING, inputY, usableWidth, inputHeight);
        dividerView.setBounds(PADDING, dividerY, usableWidth, DIVIDER_HEIGHT);
        launcherWrapper.setBounds(PADDING, launcherY, usableWidth, launcherHeight);
    }

    /**
     * ThemeManager.ThemeChangeListener implementation
     */
    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        if (dark) {
            setBackground(ThemeColors.DARK_LAYOUT_BORDER);
            inputWrapper.setBackground(ThemeColors.DARK_BACKGROUND);
            launcherWrapper.setBackground(ThemeColors.DARK_BACKGROUND);
            dividerView.setBackground(ThemeColors.DARK_LAYOUT_BORDER);
        } else {
            setBackground(ThemeColors.LAYOUT_BORDER);
            inputWrapper.setBackground(ThemeColors.BACKGROUND);
            launcherWrapper.setBackground(ThemeColors.BACKGROUND);
            dividerView.setBackground(ThemeColors.LAYOUT_BORDER);
        }
        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
