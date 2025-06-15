package pdfproject.windowui.body.right;

import pdfproject.Config;
import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class ImageQualityPanel extends JPanel {

    private static final String[] QUALITY_LEVELS = {"LOW", "MEDIUM", "HIGH"};
    private static final int[] DPI_VALUES = {100, 150, 200};

    public ImageQualityPanel() {
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Image Quality:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.THEME_BLUE);

        JComboBox<String> qualityDropdown = new JComboBox<>(QUALITY_LEVELS);
        qualityDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qualityDropdown.setBackground(Color.WHITE);
        qualityDropdown.setForeground(Color.DARK_GRAY);
        qualityDropdown.setFocusable(false);

        // Set default
        qualityDropdown.setSelectedIndex(0);
        Config.RENDER_DPI = DPI_VALUES[0];
        System.out.println("Image quality set to LOW (" + DPI_VALUES[0] + " DPI)");

        qualityDropdown.addActionListener(e -> {
            int index = qualityDropdown.getSelectedIndex();
            Config.RENDER_DPI = DPI_VALUES[index];
            System.out.println("Image quality set to " + QUALITY_LEVELS[index] + " (" + DPI_VALUES[index] + " DPI)");
        });

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.add(titleLabel);
        contentPanel.add(qualityDropdown);

        add(contentPanel); // Centered inside GridBagLayout
    }
}
