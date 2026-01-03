package pdfproject.window2.components.header;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.HEADER_BG);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        add(new HeaderLeftPanel(), BorderLayout.WEST);
        add(new HeaderRightPanel(), BorderLayout.EAST);
    }
}
