package pdfproject.window2.components.content;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class AdvancedOptionsPanel extends JPanel {

    public AdvancedOptionsPanel() {
        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(ThemeManager.CONTENT_BG);

        // Bottom separator
        setBorder(new MatteBorder(
                0, 0, 1, 0,
                ThemeManager.ACCENT_PRIMARY
        ));

        // TEMP
        add(new JLabel("Advanced Options", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
