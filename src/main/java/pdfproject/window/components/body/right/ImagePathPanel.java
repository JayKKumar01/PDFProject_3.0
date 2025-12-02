package pdfproject.window.components.body.right;

import pdfproject.Config;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.utils.AppSettings;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ComponentFactory;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImagePathPanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private final JLabel pathLabel;
    private final JLabel titleLabel;
    private final JButton browseButton;
    private final JPanel bottomPanel;

    public ImagePathPanel() {
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel = new JLabel("Output Images Path", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.THEME_BLUE);
        add(titleLabel, BorderLayout.NORTH);

        pathLabel = new JLabel(getCompactPath(Config.outputImagePath));
        pathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pathLabel.setForeground(ThemeColors.TEXT_MUTED);

        // Use theme-aware button factory: textColorLight, bgColorLight
        browseButton = ComponentFactory.createStyledButton("Browse", ThemeColors.THEME_BLUE, ThemeColors.THEME_BLUE_LIGHT);
        browseButton.setFocusable(false);
        browseButton.addActionListener(e -> openFolderDialog());

        bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.add(pathLabel, BorderLayout.CENTER);
        bottomPanel.add(browseButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // register and apply initial theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
    }

    private void openFolderDialog() {
        JFileChooser chooser = new JFileChooser(Config.outputImagePath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Output Folder");
        chooser.setAcceptAllFileFilterUsed(false);

        Window window = SwingUtilities.getWindowAncestor(this);

        int result = chooser.showOpenDialog(window);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            if (selectedDir != null) {
                Config.outputImagePath = selectedDir.getAbsolutePath();
                pathLabel.setText(getCompactPath(Config.outputImagePath));
                System.out.println(Config.outputImagePath);
                AppSettings.saveOutputPath(Config.outputImagePath);
            }
        }
    }

    private String getCompactPath(String fullPath) {

        if (fullPath == null || fullPath.isEmpty()) return "";

        File file = new File(fullPath);
        String lastFolder = file.getName();

        File rootFile = file;
        int folderCount = 0;
        while (rootFile.getParentFile() != null) {
            rootFile = rootFile.getParentFile();
            folderCount++;
        }
        String drive = rootFile.getAbsolutePath();

        if (drive.endsWith("\\") && drive.length() > 3) {
            drive = drive.substring(0, drive.length() - 1);
        }

        return drive + "\\" + "..\\".repeat(Math.max(0, folderCount - 1)) + lastFolder;
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
        bottomPanel.setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.BACKGROUND);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(dark ? ThemeColors.DARK_LAYOUT_BORDER : ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel.setForeground(dark ? ThemeColors.THEME_GREEN : ThemeColors.THEME_BLUE);
        pathLabel.setForeground(dark ? ThemeColors.DARK_TEXT_MUTED : ThemeColors.TEXT_MUTED);

        // The button from ComponentFactory will adapt itself via ThemeManager; still ensure contrast
        if (dark) {
            browseButton.setBackground(ThemeColors.THEME_GREEN);
            browseButton.setForeground(ThemeColors.DARK_BACKGROUND);
            browseButton.setOpaque(true);
        } else {
            browseButton.setBackground(ThemeColors.THEME_BLUE);
            browseButton.setForeground(ThemeColors.CONSOLE_TEXT_BG);
            browseButton.setOpaque(true);
        }

        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
