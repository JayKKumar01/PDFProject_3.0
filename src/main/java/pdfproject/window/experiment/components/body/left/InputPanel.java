package pdfproject.window.experiment.components.body.left;

import pdfproject.Config;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;
import pdfproject.window.experiment.components.ValidationAwarePanel;

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
 * Validation enabling/disabling is handled by ValidationAwarePanel (single-listener ValidationCenter).
 */
public class InputPanel extends ValidationAwarePanel implements PropertyChangeListener {

    private static final int MAX_FILENAME_LENGTH = 40;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("xlsx", "json");
    private static String lastDirectoryPath = null;

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private final JLabel fileLabel;
    private final JButton browseButton;
    private final JPanel centerPanel;

    public InputPanel() {
        super(new BorderLayout());
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setOpaque(true);

        fileLabel = createFileLabel();
        browseButton = createBrowseButton();

        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(browseButton);
        centerPanel.add(fileLabel);

        add(centerPanel);

        setupFileDrop();

        // theme hookup (we unregister in removeNotify)
        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private JLabel createFileLabel() {
        JLabel label = new JLabel("No input data");
        label.setFont(LABEL_FONT);
        return label;
    }

    private JButton createBrowseButton() {
        JButton btn = new JButton("Choose File");
        btn.setFont(BUTTON_FONT);
        btn.addActionListener(e -> openFileDialog());
        btn.setOpaque(true);
        // Avoid heavy UI operations on EDT action handler — just show picker
        return btn;
    }

    /**
     * Open a file selection dialog. Prefer AWT FileDialog with a fallback to JFileChooser.
     * Save lastDirectoryPath for next time.
     */
    private void openFileDialog() {
        // get window ancestor (may be null in some contexts)
        Window win = SwingUtilities.getWindowAncestor(this);

        // Try AWT FileDialog first (native look). If ancestor is null or FileDialog fails, fallback.
        if (win instanceof Frame) {
            FileDialog fd = new FileDialog((Frame) win, "Select Input File", FileDialog.LOAD);
            fd.setFilenameFilter((dir, name) -> {
                String n = name.toLowerCase();
                return n.endsWith(".xlsx") || n.endsWith(".json");
            });

            if (lastDirectoryPath != null) {
                fd.setDirectory(lastDirectoryPath);
            }

            fd.setVisible(true);
            File sel = getSelectedFile(fd);
            if (sel == null) {
                // nothing selected — fallback not necessary
                return;
            }
            processSelectedFile(sel);
            return;
        }

        // Fallback: JFileChooser (safe when no Frame ancestor)
        JFileChooser chooser = new JFileChooser(lastDirectoryPath);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".xlsx") || name.endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "Excel (.xlsx) and JSON (.json)";
            }
        });

        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File sel = chooser.getSelectedFile();
            processSelectedFile(sel);
        }
    }

    private File getSelectedFile(FileDialog dialog) {
        String name = dialog.getFile();
        String dir = dialog.getDirectory();
        return (name != null && dir != null) ? new File(dir, name) : null;
    }

    private void processSelectedFile(File sel) {
        if (sel == null) return;
        if (isValidFile(sel)) {
            handleSelectedFile(sel);
        } else {
            showInvalidFileWarning();
        }
    }

    /**
     * Setup drag & drop — accept the first valid file dropped.
     */
    private void setupFileDrop() {
        // keep a reference to DropTarget if you need to remove it later (not necessary here)
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = event.getTransferable();
                    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        // find first valid file
                        for (File f : files) {
                            if (isValidFile(f)) {
                                processSelectedFile(f);
                                return;
                            }
                        }
                        // none valid
                        showInvalidFileWarning();
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
        // best-effort write to Config; swallow errors so UI remains responsive
        try {
            Config.inputPath = file.getAbsolutePath();
        } catch (Throwable ignored) {}

        fileLabel.setText(ellipsize(file.getName()));
        fileLabel.setToolTipText(file.getAbsolutePath());
        System.out.println("Selected Input Data: \"" + file.getName() + "\"");
    }

    private boolean isValidFile(File file) {
        if (file == null || !file.isFile()) return false;
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot == -1 || dot == name.length() - 1) return false;
        String ext = name.substring(dot + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    private String ellipsize(String text) {
        if (text == null) return "";
        if (text.length() <= MAX_FILENAME_LENGTH) return text;
        return text.substring(0, MAX_FILENAME_LENGTH - 3) + "...";
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
        // ThemeManager fires property changes — update on EDT
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    /* Lifecycle: unregister theme listener on removal. Validation listener lifecycle is handled by ValidationAwarePanel. */
    @Override
    public void removeNotify() {
        ThemeManager.unregister(this);
        super.removeNotify();
    }
}
