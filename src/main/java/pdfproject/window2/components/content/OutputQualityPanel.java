package pdfproject.window2.components.content;

import pdfproject.Config;
import pdfproject.utils.AppSettings;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;

public class OutputQualityPanel extends JPanel {

    // =====================================================
    // Layout constants (single source of truth)
    // =====================================================
    private static final int ROOT_H_GAP     = 10;
    private static final int SECTION_PAD_Y  = 8;
    private static final int SECTION_PAD_X  = 12;
    private static final int ROW_V_GAP      = 6;
    private static final int INLINE_H_GAP   = 8;
    private static final int BORDER = 1;

    private static final int[] DPI_VALUES = {100, 150, 200};

    private final JComboBox<String> qualityCombo;
    private final JLabel pathLabel;

    public OutputQualityPanel() {
        // Root paints CONTENT_BG so the horizontal gap is visible
        setOpaque(true);
        setBackground(ThemeManager.CONTENT_BG);
        setLayout(new BorderLayout(ROOT_H_GAP, 0));

        // =====================================================
        // LEFT : Image Quality (CONSOLE_BG)
        // =====================================================
        JPanel qualityWrapper = new JPanel(new GridBagLayout());
        qualityWrapper.setOpaque(true);
        qualityWrapper.setBackground(ThemeManager.CONSOLE_BG);
        qualityWrapper.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(
                        BORDER, 0,
                        BORDER, BORDER,
                        ThemeManager.ACCENT_PRIMARY
                ),
                BorderFactory.createEmptyBorder(
                        SECTION_PAD_Y, SECTION_PAD_X,
                        SECTION_PAD_Y, SECTION_PAD_X
                )
        ));

        JLabel qualityLabel = new JLabel("Image Quality:");
        qualityLabel.setForeground(ThemeManager.CONTENT_TEXT);

        qualityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        qualityCombo.setFocusable(false);
        qualityCombo.setBackground(ThemeManager.CONSOLE_BG);
        qualityCombo.setForeground(ThemeManager.HEADER_TEXT);

        int savedIndex = AppSettings.loadImageQualityIndex(indexForDpi(Config.renderDpi));
        if (savedIndex >= 0 && savedIndex < DPI_VALUES.length) {
            qualityCombo.setSelectedIndex(savedIndex);
            Config.renderDpi = DPI_VALUES[savedIndex];
        }

        qualityCombo.addActionListener(e -> {
            int i = qualityCombo.getSelectedIndex();
            if (i >= 0 && i < DPI_VALUES.length) {
                Config.renderDpi = DPI_VALUES[i];
                AppSettings.saveImageQualityIndex(i);
            }
        });

        qualityWrapper.add(qualityLabel);
        qualityWrapper.add(qualityCombo);

        // =====================================================
        // RIGHT : Output Path (CONSOLE_BG)
        // =====================================================
        JPanel pathPanel = new JPanel(new BorderLayout(0, ROW_V_GAP));
        pathPanel.setOpaque(true);
        pathPanel.setBackground(ThemeManager.CONSOLE_BG);
        pathPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(
                        BORDER, BORDER,
                        BORDER, 0,
                        ThemeManager.ACCENT_PRIMARY
                ),
                BorderFactory.createEmptyBorder(
                        SECTION_PAD_Y, SECTION_PAD_X,
                        SECTION_PAD_Y, SECTION_PAD_X
                )
        ));

        // ---- Title (centered) ----
        JLabel pathTitle = new JLabel("Output Image Path", SwingConstants.CENTER);
        pathTitle.setForeground(ThemeManager.ACCENT_PRIMARY);
        pathTitle.setFont(pathTitle.getFont().deriveFont(Font.BOLD));

        // ---- Path row (LEFT path, RIGHT button) ----
        JPanel pathRow = new JPanel(new BorderLayout(INLINE_H_GAP, 0));
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

        pathRow.add(pathLabel, BorderLayout.CENTER);
        pathRow.add(browseButton, BorderLayout.EAST);

        pathPanel.add(pathTitle, BorderLayout.NORTH);
        pathPanel.add(pathRow, BorderLayout.CENTER);

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
        JFileChooser chooser = new JFileChooser(Config.outputImagePath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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
