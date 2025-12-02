package pdfproject.window.components.body.left;

import pdfproject.Config;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

public class InputSectionPanel extends JPanel {

    private static final int MAX_FILENAME_LENGTH = 40;
    private static String lastDirectoryPath;

    private final JLabel fileLabel;

    public InputSectionPanel() {
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BACKGROUND);

        fileLabel = createFileLabel();
        JButton browseButton = createBrowseButton();

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        centerPanel.setBackground(ThemeColors.BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(browseButton);
        centerPanel.add(fileLabel);

        add(centerPanel);
        setupFileDrop();
    }

    private JLabel createFileLabel() {
        JLabel label = new JLabel("No input data");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(ThemeColors.THEME_BLUE);
        return label;
    }

    private JButton createBrowseButton() {
        JButton button = ComponentFactory.createStyledButton(
                "Choose File",
                ThemeColors.THEME_BLUE,
                new Color(174, 215, 255)
        );
        button.addActionListener(e -> openFileDialog());
        return button;
    }

    private void openFileDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        FileDialog fileDialog = new FileDialog(parentFrame, "Select Input File", FileDialog.LOAD);
        fileDialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".xlsx"));

        if (lastDirectoryPath != null) {
            fileDialog.setDirectory(lastDirectoryPath);
        }

        fileDialog.setVisible(true);
        File selectedFile = getSelectedFile(fileDialog);

        if (selectedFile != null) {
            if (isValidExcelFile(selectedFile)) {
                handleSelectedFile(selectedFile);
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
                                .filter(InputSectionPanel.this::isValidExcelFile)
                                .findFirst()
                                .ifPresentOrElse(
                                        InputSectionPanel.this::handleSelectedFile,
                                        InputSectionPanel.this::showInvalidFileWarning
                                );
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, true);
    }

    private void handleSelectedFile(File file) {
        lastDirectoryPath = file.getParent();
        Config.inputPath = file.getAbsolutePath();
        fileLabel.setText(ellipsize(file.getName()));
        fileLabel.setToolTipText(file.getAbsolutePath());
        System.out.println("Selected Input Data: \"" + file.getName() + "\"");
    }

    private boolean isValidExcelFile(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".xlsx");
    }

    private String ellipsize(String text) {
        return (text.length() <= MAX_FILENAME_LENGTH)
                ? text
                : text.substring(0, MAX_FILENAME_LENGTH - 3) + "...";
    }

    private void showInvalidFileWarning() {
        JOptionPane.showMessageDialog(
                this,
                "Only .xlsx files are supported.",
                "Invalid File Type",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
