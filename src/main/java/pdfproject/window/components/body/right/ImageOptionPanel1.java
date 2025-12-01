package pdfproject.window.components.body.right;

import pdfproject.Config;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class ImageOptionPanel1 extends JPanel implements TaskStateListener {

    private static final String[] QUALITY_LEVELS = {"LOW", "MEDIUM", "HIGH"};
    private static final int[] DPI_VALUES = {100, 150, 200};

    private final JComboBox<String> qualityDropdown;

    public ImageOptionPanel1() {
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Image Quality:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.THEME_BLUE);

        qualityDropdown = new JComboBox<>(QUALITY_LEVELS);
        qualityDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qualityDropdown.setBackground(Color.WHITE);
        qualityDropdown.setForeground(Color.DARK_GRAY);
        qualityDropdown.setFocusable(false);

        qualityDropdown.addActionListener(e -> updateDpiFromSelection());

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.add(titleLabel);
        contentPanel.add(qualityDropdown);

        add(contentPanel);
    }

    private void updateDpiFromSelection() {
        int index = qualityDropdown.getSelectedIndex();
        Config.RENDER_DPI = DPI_VALUES[index];
        System.out.println("Image quality set to " + QUALITY_LEVELS[index] + " (" + DPI_VALUES[index] + " DPI)");
    }

    @Override
    public void onStart() {
        Helper.setEnabledRecursively(this, false);
    }

    @Override
    public void onStop() {
        Helper.setEnabledRecursively(this, true);
    }
}
