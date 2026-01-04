package pdfproject.window2.components.header;

import pdfproject.Config;
import pdfproject.utils.AppSettings;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class HeaderRightPanel extends JPanel {

    public HeaderRightPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JLabel label = new JLabel("Prodigy Validation");
        label.setForeground(ThemeManager.ACCENT_SOFT);
        label.putClientProperty("FlatLaf.style", "font: 13");

        boolean enabled = AppSettings.loadProdigyValidation(false);

        // ✅ initialize runtime config
        Config.isProdigyValidation = enabled;

        ToggleSwitch toggle = new ToggleSwitch(enabled);

        toggle.addItemListener(e -> {
            boolean selected = toggle.isSelected();

            // ✅ persist
            AppSettings.saveProdigyValidation(selected);

            // ✅ update runtime state
            Config.isProdigyValidation = selected;

            System.out.println(
                    "Prodigy Validation has been " + (selected ? "enabled." : "disabled.")
            );
        });

        add(label);
        add(toggle);
    }
}
