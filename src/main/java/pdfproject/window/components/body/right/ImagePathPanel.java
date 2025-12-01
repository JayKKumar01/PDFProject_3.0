package pdfproject.window.components.body.right;

import pdfproject.Config;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.utils.AppSettings;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImagePathPanel extends JPanel implements TaskStateListener {

    private final JLabel pathLabel;
    public ImagePathPanel(){
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.LAYOUT_BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Output Images Path",SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.THEME_BLUE);
        add(titleLabel,BorderLayout.NORTH);

        pathLabel = new JLabel(getCompactPath(Config.outputImagePath));
        pathLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));
        pathLabel.setForeground(Color.DARK_GRAY);

        JButton browseButton = new JButton("Browse");
        browseButton.setFont(new Font("Segoe UI",Font.PLAIN,11));
        browseButton.setFocusable(false);
        browseButton.addActionListener(e -> openFolderDialog());

        JPanel bottomPanel = new JPanel(new BorderLayout(10,0));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.add(pathLabel, BorderLayout.CENTER);
        bottomPanel.add(browseButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

    }

    private void openFolderDialog() {
        JFileChooser chooser = new JFileChooser(Config.outputImagePath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Output Folder");
        chooser.setAcceptAllFileFilterUsed(false);

        Window window = SwingUtilities.getWindowAncestor(this);

        int result = chooser.showOpenDialog(window);
        if (result == JFileChooser.APPROVE_OPTION){
            File selectedDir = chooser.getSelectedFile();
            if (selectedDir != null){
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
        while (rootFile.getParentFile() != null){
            rootFile = rootFile.getParentFile();
            folderCount++;
        }
        String drive = rootFile.getAbsolutePath();

        if (drive.endsWith("\\") && drive.length() > 3){
            drive = drive.substring(0, drive.length() - 1);
        }

        return drive + "\\" + "..\\".repeat(Math.max(0,folderCount-1)) + lastFolder;
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
