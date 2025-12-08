package pdfproject.window.experiment.components.body.right.option;

import pdfproject.Config;
import pdfproject.utils.AppSettings;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;
import pdfproject.window.experiment.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class PathPanel extends JPanel implements PropertyChangeListener {

    private final JLabel titleLabel;
    private final JLabel pathLabel;
    private final JButton browseButton;
    private final JPanel bottomPanel;

    public PathPanel() {
        super(new BorderLayout());
        setOpaque(true);

        // -----------------------
        // DPI-aware padding
        // -----------------------
        int pad = UiScale.scaleInt(4);
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

        // -----------------------
        // UI elements with SMALL SCALED FONTS
        // -----------------------
        titleLabel = new JLabel("Output Images Path", SwingConstants.CENTER);
        titleLabel.setFont(
                UiScale.getScaledFont(new Font("Segoe UI", Font.BOLD, 10))   // SMALL
        );

        pathLabel = new JLabel(getCompactPath(Config.outputImagePath));
        pathLabel.setFont(
                UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 10))  // SMALL
        );

        browseButton = new JButton("Browse");
        browseButton.setFocusable(false);
        browseButton.setFont(
                UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 10))  // SMALL
        );
        browseButton.addActionListener(e -> openFolderDialog());

        bottomPanel = new JPanel(new BorderLayout(UiScale.scaleInt(4), 0));
        bottomPanel.add(pathLabel, BorderLayout.CENTER);
        bottomPanel.add(browseButton, BorderLayout.EAST);

        add(titleLabel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    // -----------------------
    // Folder chooser
    // -----------------------
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

    // -----------------------
    // Compact path formatter
    // -----------------------
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

    // -----------------------
    // Apply theme
    // -----------------------
    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;

        setBackground(t.bodyBg);
        bottomPanel.setBackground(t.bodyBg);

        titleLabel.setForeground(t.usernameAccent);
        pathLabel.setForeground(t.bodyText);

        browseButton.setBackground(t.startButtonColor);
        browseButton.setForeground(ExperimentTheme.readableForeground(t.startButtonColor));
        browseButton.setOpaque(true);

        repaint();
    }

    // -----------------------
    // Theme listener
    // -----------------------
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (SwingUtilities.isEventDispatchThread())
            applyTheme(ThemeManager.getTheme());
        else
            SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    @Override
    public void removeNotify() {
        ThemeManager.unregister(this);
        super.removeNotify();
    }

    public String getOutputPath() {
        return Config.outputImagePath;
    }
}
