package pdfproject.window2.components.content;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class OutputQualityPanel extends JPanel {

    public OutputQualityPanel() {
        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(ThemeManager.CONTENT_BG);

        // Bottom separator
        setBorder(new MatteBorder(
                0, 0, 1, 0,
                ThemeManager.ACCENT_PRIMARY
        ));

        // TEMP
        add(new JLabel("Output & Quality", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
