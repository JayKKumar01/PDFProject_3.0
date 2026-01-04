package pdfproject.window.components.content;

import pdfproject.Config;
import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import java.util.Set;

public class InputAreaPanel extends JPanel {

    private static final int MAX_FILENAME_LENGTH = 40;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("xlsx", "json");

    private static String lastDirectoryPath = null;

    private final JLabel fileLabel;
    private final JButton chooseButton;

    public InputAreaPanel() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(ThemeManager.CONTENT_BG);

        // Bottom separator
//        setBorder(BorderFactory.createMatteBorder(
//                0, 0, 1, 0,
//                ThemeManager.ACCENT_SOFT
//        ));

        fileLabel = new JLabel("No input file selected");
        fileLabel.setForeground(ThemeManager.CONTENT_TEXT);

        chooseButton = new JButton("Choose File");
        chooseButton.setBackground(ThemeManager.ACCENT_PRIMARY);
        chooseButton.setForeground(Color.BLACK);
        chooseButton.setFocusPainted(false);

        chooseButton.addActionListener(e -> openFileDialog());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        row.setOpaque(false);
        row.add(chooseButton);
        row.add(fileLabel);

        add(row);

        setupFileDrop();
    }

    // ---------- File chooser ----------

    private void openFileDialog() {
        Window win = SwingUtilities.getWindowAncestor(this);

        if (win instanceof Frame frame) {
            FileDialog fd = new FileDialog(frame, "Select Input File", FileDialog.LOAD);
            fd.setFilenameFilter((dir, name) ->
                    name.toLowerCase().endsWith(".xlsx") || name.toLowerCase().endsWith(".json")
            );

            if (lastDirectoryPath != null) {
                fd.setDirectory(lastDirectoryPath);
            }

            fd.setVisible(true);

            String file = fd.getFile();
            String dir  = fd.getDirectory();
            if (file != null && dir != null) {
                processFile(new File(dir, file));
            }
            return;
        }

        JFileChooser chooser = new JFileChooser(lastDirectoryPath);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String n = f.getName().toLowerCase();
                return n.endsWith(".xlsx") || n.endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "Excel (.xlsx) and JSON (.json)";
            }
        });

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            processFile(chooser.getSelectedFile());
        }
    }

    // ---------- Drag & Drop ----------

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
                        for (File f : files) {
                            if (isValidFile(f)) {
                                processFile(f);
                                return;
                            }
                        }
                        showInvalidFileWarning();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, true);
    }

    // ---------- Helpers ----------

    private void processFile(File file) {
        if (!isValidFile(file)) {
            showInvalidFileWarning();
            return;
        }

        lastDirectoryPath = file.getParent();
        Config.inputPath = file.getAbsolutePath();

        fileLabel.setText(ellipsize(file.getName()));
        fileLabel.setToolTipText(file.getAbsolutePath());

        System.out.println("Selected input file: " + file.getAbsolutePath());
    }

    private boolean isValidFile(File file) {
        if (file == null || !file.isFile()) return false;
        String name = file.getName().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot == -1) return false;
        return ALLOWED_EXTENSIONS.contains(name.substring(dot + 1));
    }

    private String ellipsize(String text) {
        if (text.length() <= MAX_FILENAME_LENGTH) return text;
        return text.substring(0, MAX_FILENAME_LENGTH - 3) + "...";
    }

    private void showInvalidFileWarning() {
        JOptionPane.showMessageDialog(
                this,
                "Only .xlsx and .json files are supported.",
                "Invalid File",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
