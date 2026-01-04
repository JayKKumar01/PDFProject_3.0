package pdfproject.window.components.header;

import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.HEADER_BG);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        add(wrapVertically(new HeaderLeftPanel()), BorderLayout.WEST);
        add(wrapVertically(new HeaderRightPanel()), BorderLayout.EAST);
    }

    /**
     * Wraps a component so it is vertically centered.
     */
    private JComponent wrapVertically(JComponent child) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(child);
        return wrapper;
    }
}
