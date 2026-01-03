package pdfproject.window2.components.content;

import pdfproject.Config;
import pdfproject.utils.AppSettings;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class OutputQualityPanel extends JPanel {

    private static final int[] DPI_VALUES = {100, 150, 200};

    private final JComboBox<String> qualityCombo;
    private final JLabel pathLabel;

    public OutputQualityPanel() {
        setOpaque(true);
        setLayout(new BorderLayout(24, 0));
        setBackground(ThemeManager.CONSOLE_BG);

        // =====================================================
        // LEFT : Image Quality (unchanged)
        // =====================================================
        JPanel qualityWrapper = new JPanel(new GridBagLayout());
        qualityWrapper.setOpaque(false);

        JPanel qualityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        qualityPanel.setOpaque(false);

        JLabel qualityLabel = new JLabel("Image Quality:");
        qualityLabel.setForeground(ThemeManager.CONTENT_TEXT);

        qualityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        qualityCombo.setFocusable(false);
        qualityCombo.setBackground(ThemeManager.CONSOLE_BG);
        qualityCombo.setForeground(ThemeManager.HEADER_TEXT);

        int index = indexForDpi(Config.renderDpi);
        if (index >= 0) qualityCombo.setSelectedIndex(index);

        qualityCombo.addActionListener(e -> {
            int i = qualityCombo.getSelectedIndex();
            if (i >= 0 && i < DPI_VALUES.length) {
                Config.renderDpi = DPI_VALUES[i];
            }
        });

        qualityPanel.add(qualityLabel);
        qualityPanel.add(qualityCombo);
        qualityWrapper.add(qualityPanel);

        // =====================================================
        // RIGHT : Output Path (FULL WIDTH, CORRECT ALIGNMENT)
        // =====================================================
        JPanel pathPanel = new JPanel(new GridBagLayout());
        pathPanel.setOpaque(false);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.weightx = 1;                 // 🔴 TAKE FULL WIDTH
        outer.weighty = 1;                 // 🔴 CENTER VERTICALLY
        outer.fill = GridBagConstraints.HORIZONTAL;
        outer.anchor = GridBagConstraints.CENTER;

        JPanel pathContent = new JPanel(new GridBagLayout());
        pathContent.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        // ---- Row 1: centered label ----
        JLabel pathTitle = new JLabel("Output Image Path", SwingConstants.CENTER);
        pathTitle.setForeground(ThemeManager.ACCENT_PRIMARY);
        pathTitle.setFont(pathTitle.getFont().deriveFont(Font.BOLD));

        c.gridy = 0;
        pathContent.add(pathTitle, c);

        // ---- Row 2: path LEFT, browse RIGHT ----
        JPanel pathRow = new JPanel(new BorderLayout(8, 0));
        pathRow.setOpaque(false);

        pathLabel = new JLabel(ellipsize(Config.outputImagePath));
        pathLabel.setForeground(ThemeManager.CONTENT_TEXT);
        pathLabel.setToolTipText(Config.outputImagePath);

        JButton browseButton = new JButton("Browse");
        browseButton.setFocusable(false);
        browseButton.setBackground(ThemeManager.ACCENT_PRIMARY);
        browseButton.setForeground(Color.BLACK);
        browseButton.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        browseButton.addActionListener(e -> openFolderDialog());

// ---- Wrapper to provide RIGHT margin (true margin, not padding) ----
        JPanel browseWrapper = new JPanel(new BorderLayout());
        browseWrapper.setOpaque(false);
        browseWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12)); // right margin
        browseWrapper.add(browseButton, BorderLayout.CENTER);

        pathRow.add(pathLabel, BorderLayout.CENTER);
        pathRow.add(browseWrapper, BorderLayout.EAST);


        c.gridy = 1;
        pathContent.add(pathRow, c);

        pathPanel.add(pathContent, outer);

        // =====================================================
        // Assemble
        // =====================================================
        add(qualityWrapper, BorderLayout.WEST);
        add(pathPanel, BorderLayout.CENTER);
    }

    // =====================================================
    // Folder chooser
    // =====================================================
    private void openFolderDialog() {
        if (!isEnabled()) return;

        JFileChooser chooser = new JFileChooser(Config.outputImagePath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Output Folder");
        chooser.setAcceptAllFileFilterUsed(false);

        Window window = SwingUtilities.getWindowAncestor(this);
        if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            if (dir != null) {
                Config.outputImagePath = dir.getAbsolutePath();
                pathLabel.setText(ellipsize(Config.outputImagePath));
                pathLabel.setToolTipText(Config.outputImagePath);
                AppSettings.saveOutputPath(Config.outputImagePath);
            }
        }
    }

    // =====================================================
    // Helpers
    // =====================================================
    private int indexForDpi(int dpi) {
        for (int i = 0; i < DPI_VALUES.length; i++) {
            if (DPI_VALUES[i] == dpi) return i;
        }
        return -1;
    }

    private String ellipsize(String text) {
        if (text == null) return "";
        int max = 80;
        if (text.length() <= max) return text;
        return "..." + text.substring(text.length() - max);
    }
}
