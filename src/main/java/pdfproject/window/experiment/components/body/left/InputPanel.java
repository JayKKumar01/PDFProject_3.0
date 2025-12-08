package pdfproject.window.experiment.components.body.left;

import pdfproject.Config;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Input panel: file chooser + drag-and-drop. Theme-aware and registers with ThemeManager.
 */
public class InputPanel extends JPanel implements PropertyChangeListener {

    private static final int MAX_FILENAME_LENGTH = 40;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("xlsx", "json");
    private static String lastDirectoryPath = null;

    private final JLabel fileLabel;
    private final JButton browseButton;
    private final JPanel centerPanel;

    public InputPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        fileLabel = createFileLabel();
        browseButton = createBrowseButton();

        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(browseButton);
        centerPanel.add(fileLabel);

        add(centerPanel);

        setupFileDrop();

        // theme hookup
        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private JLabel createFileLabel() {
        JLabel label = new JLabel("No input data");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JButton createBrowseButton() {
        JButton btn = new JButton("Choose File");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.addActionListener(e -> openFileDialog());
        btn.setOpaque(true);
        return btn;
    }

    private void openFileDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(parent, "Select Input File", FileDialog.LOAD);
        fd.setFilenameFilter((dir, name) -> {
            String n = name.toLowerCase();
            return n.endsWith(".xlsx") || n.endsWith(".json");
        });

        if (lastDirectoryPath != null) {
            fd.setDirectory(lastDirectoryPath);
        }

        fd.setVisible(true);
        File sel = getSelectedFile(fd);

        if (sel != null) {
            if (isValidFile(sel)) {
                handleSelectedFile(sel);
            } else {
                showInvalidFileWarning();
            }
        }
    }

    private File getSelectedFile(FileDialog dialog) {
        String name = dialog.getFile();
        String dir = dialog.getDirectory();
        return (name != null && dir != null) ? new File(dir, name) : null;
    }

    private void setupFileDrop() {
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = event.getTransferable();
                    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        files.stream()
                                .filter(InputPanel.this::isValidFile)
                                .findFirst()
                                .ifPresentOrElse(
                                        InputPanel.this::handleSelectedFile,
                                        InputPanel.this::showInvalidFileWarning
                                );
                    } else {
                        event.rejectDrop();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, true);
    }

    private void handleSelectedFile(File file) {
        if (file == null) return;
        lastDirectoryPath = file.getParent();
        try {
            Config.inputPath = file.getAbsolutePath();
        } catch (Throwable ignored) {
            // keep UI functional even if Config isn't present/writable
        }

        fileLabel.setText(ellipsize(file.getName()));
        fileLabel.setToolTipText(file.getAbsolutePath());
        System.out.println("Selected Input Data: \"" + file.getName() + "\"");
    }

    private boolean isValidFile(File file) {
        if (!file.isFile()) return false;
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot == -1 || dot == name.length() - 1) return false;
        String ext = name.substring(dot + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    private String ellipsize(String text) {
        if (text == null) return "";
        return (text.length() <= MAX_FILENAME_LENGTH) ? text : text.substring(0, MAX_FILENAME_LENGTH - 3) + "...";
    }

    private void showInvalidFileWarning() {
        JOptionPane.showMessageDialog(
                this,
                "Only .xlsx and .json files are supported.",
                "Invalid File Type",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Apply theme using ExperimentTheme instance.
     * Called on initialization and when ThemeManager notifies via propertyChange.
     */
    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;
        setBackground(t.bodyBg);
        centerPanel.setBackground(t.bodyBg);

        Color accent = t.usernameAccent != null ? t.usernameAccent : t.headerText;
        Color accentText = ExperimentTheme.readableForeground(accent);

        fileLabel.setForeground(accent);
        browseButton.setBackground(accent);
        browseButton.setForeground(accentText);
        browseButton.setOpaque(true);

        revalidate();
        repaint();
    }

    /* PropertyChangeListener for ThemeManager */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // ThemeManager fires property "dark" (boolean old/new) — update theme on EDT
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
