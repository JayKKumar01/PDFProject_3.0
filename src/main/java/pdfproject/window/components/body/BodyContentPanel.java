package pdfproject.window.components.body;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.components.body.left.LeftPanel;
import pdfproject.window.components.body.right.RightPanel;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class BodyContentPanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private final LeftPanel leftPanel;
    private final RightPanel rightPanel;

    public BodyContentPanel() {
        setLayout(null); // Manual layout
        setBackground(ThemeColors.BACKGROUND);

        leftPanel = new LeftPanel();
        rightPanel = new RightPanel();

        add(leftPanel);
        add(rightPanel);

        setupResizeListener();

        // register for theme changes and apply current theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
    }

    public void setTaskStateListener(TaskStateListener listener) {
        leftPanel.setTaskStateListener(listener);
    }

    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePanels(getWidth(), getHeight());
            }
        });
    }

    private void resizePanels(int width, int height) {
        int halfWidth = width / 2;
        leftPanel.setBounds(0, 0, halfWidth, height);
        rightPanel.setBounds(halfWidth, 0, width - halfWidth, height);
    }

    @Override
    public void onStart() {
        rightPanel.onStart();
    }

    @Override
    public void onStop() {
        rightPanel.onStop();
    }

    /**
     * ThemeManager.ThemeChangeListener implementation
     */
    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.BACKGROUND);
        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
