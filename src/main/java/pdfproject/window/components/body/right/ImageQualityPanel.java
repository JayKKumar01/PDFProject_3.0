package pdfproject.window.components.body.right;

import pdfproject.Config;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class ImageQualityPanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private static final String[] QUALITY_LEVELS = {"LOW", "MEDIUM", "HIGH"};
    private static final int[] DPI_VALUES = {100, 150, 200};

    private final JComboBox<String> qualityDropdown;
    private final JLabel titleLabel;
    private final JPanel contentPanel;

    public ImageQualityPanel() {
        setLayout(new GridBagLayout());

        // border uses layout border color for line, will be swapped in applyTheme
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel = new JLabel("Image Quality:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.THEME_BLUE);

        qualityDropdown = new JComboBox<>(QUALITY_LEVELS);
        qualityDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qualityDropdown.setFocusable(false);

        // initial colors
        qualityDropdown.setBackground(ThemeColors.CONSOLE_TEXT_BG);
        qualityDropdown.setForeground(ThemeColors.TEXT_MUTED);

        qualityDropdown.addActionListener(e -> updateDpiFromSelection());

        contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        contentPanel.add(titleLabel);
        contentPanel.add(qualityDropdown);

        add(contentPanel);

        // register to theme manager
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
    }

    private void updateDpiFromSelection() {
        int index = qualityDropdown.getSelectedIndex();
        if (index < 0 || index >= DPI_VALUES.length) return;
        Config.renderDpi = DPI_VALUES[index];
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

    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.BACKGROUND);
        contentPanel.setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.BACKGROUND);

        // border color swap
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(dark ? ThemeColors.DARK_LAYOUT_BORDER : ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel.setForeground(dark ? ThemeColors.THEME_GREEN : ThemeColors.THEME_BLUE);

        // dropdown background/text for contrast
        qualityDropdown.setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.CONSOLE_TEXT_BG);
        qualityDropdown.setForeground(dark ? ThemeColors.DARK_TEXT_MUTED : ThemeColors.TEXT_MUTED);

        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
