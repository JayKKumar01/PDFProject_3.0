package pdfproject.windowui.body.left;

import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LeftPanel extends JPanel {

    private static final int PADDING = 10;
    private static final int DIVIDER_HEIGHT = 10;

    private final JPanel inputWrapper;
    private final JPanel launcherWrapper;
    private final JPanel dividerView;

    public LeftPanel() {
        setLayout(null); // Manual layout
        setBackground(ThemeColors.LAYOUT_BORDER);

        inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(ThemeColors.BACKGROUND);
        inputWrapper.add(new InputSectionPanel(), BorderLayout.CENTER);

        dividerView = new JPanel();
        dividerView.setBackground(ThemeColors.LAYOUT_BORDER);

        launcherWrapper = new JPanel(new BorderLayout());
        launcherWrapper.setBackground(ThemeColors.BACKGROUND);
        launcherWrapper.add(new LauncherSectionPanel(), BorderLayout.CENTER);

        add(inputWrapper);
        add(dividerView);
        add(launcherWrapper);

        // 🔁 Trigger child layout updates when this panel is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeSections(getWidth(), getHeight());
            }
        });
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        resizeSections(width, height); // Ensures child layout also happens on initial layout
    }

    private void resizeSections(int width, int height) {
        int contentWidth = width - 2 * PADDING;
        int contentHeight = height - 2 * PADDING - DIVIDER_HEIGHT;
        int halfHeight = contentHeight / 2;

        int launcherHeight = contentHeight - halfHeight;

        int inputY = PADDING;
        int dividerY = inputY + halfHeight;
        int launcherY = dividerY + DIVIDER_HEIGHT;

        inputWrapper.setBounds(PADDING, inputY, contentWidth, halfHeight);
        dividerView.setBounds(PADDING, dividerY, contentWidth, DIVIDER_HEIGHT);
        launcherWrapper.setBounds(PADDING, launcherY, contentWidth, launcherHeight);
    }
}
