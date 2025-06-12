package pdfproject.window;

import pdfproject.Config;
import pdfproject.Launcher;
import pdfproject.constants.AppPaths;
import pdfproject.constants.OperationColor;
import pdfproject.interfaces.LauncherListener;
import pdfproject.window.utils.CustomOutputStream;
import pdfproject.window.utils.Helper;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Window {
//    private final JTextArea console = new JTextArea();
    private final JTextArea console = new JTextArea();
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private JButton resetButton;

    private final Map<String, String> defaultColors = new HashMap<>();
    private final Map<String, JComboBox<String>> operationBoxes = new HashMap<>();

    private File lastDirectory = new File(AppPaths.DOWNLOAD_DIR);



    public Window(int h) {

        JFrame jFrame = createMainFrame();
        int w = h * 16 / 9;
        int mainHeight = h * 3 / 5;

        JPanel mainContent = createMainContentPanel(w, mainHeight);
        JScrollPane scrollPane = createConsoleScrollPane(w, h - mainHeight);
        setupConsole();

        JPanel browsePanel = createBrowsePanel();
        mainContent.add(browsePanel);

        JButton startBtn = createStartButton();
        startBtn.setBounds(40, 120, 200, 25);
        mainContent.add(startBtn);

        JPanel settingsPanel = createSettingsPanel();
        mainContent.add(settingsPanel);

        jFrame.getContentPane().add(mainContent, BorderLayout.CENTER);
        jFrame.getContentPane().add(scrollPane, BorderLayout.SOUTH);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);


    }
    private JFrame createMainFrame() {
        JFrame frame = new JFrame("PDFProject 3.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        return frame;
    }

    private JPanel createMainContentPanel(int w, int h) {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(245, 245, 245)); // Light gray
        panel.setPreferredSize(new Dimension(w, h));
        return panel;
    }

    private JScrollPane createConsoleScrollPane(int w, int h) {
        console.setBackground(Color.WHITE);
        console.setForeground(Color.DARK_GRAY);
        console.setEditable(false);
        console.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        redirectSystemOut();

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(w, h));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(245, 245, 245)); // Same as main
        panel.setBounds(0, 50, 300, 40);

        JButton browseButton = new JButton("Choose InputData");
        browseButton.setFocusable(false);
        browseButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel fileLabel = new JLabel("No File Chosen");
        fileLabel.setForeground(new Color(33, 33, 33));
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        browseButton.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog((Frame) null, "Select InputData", FileDialog.LOAD);
            fileDialog.setDirectory(lastDirectory.getAbsolutePath());
            fileDialog.setVisible(true);

            String filename = fileDialog.getFile();
            if (filename != null) {
                File selected = new File(fileDialog.getDirectory(), filename);
                Config.INPUT_PATH = selected.getAbsolutePath();
                fileLabel.setText(selected.getName());
                System.out.println(Config.INPUT_PATH);
                lastDirectory = selected.getParentFile();
            }
        });

        panel.add(browseButton);
        panel.add(fileLabel);
        return panel;
    }

    private JButton createStartButton() {
        JButton button = new JButton("Start Validation");
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(new Color(66, 133, 244));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> service.execute(() -> {
            if (!Config.INPUT_PATH.isEmpty()) {
                System.out.println("Launcher Started!");
                Launcher.start(new LauncherListener() {
                    @Override
                    public void onStart() {
                        SwingUtilities.invokeLater(() -> setUIEnabled(false));
                        System.out.println("Validation started...");
                    }

                    @Override
                    public void onFinish() {
                        SwingUtilities.invokeLater(() -> {
                            setUIEnabled(true);
                            checkResetButtonStatus();
                        });
                        System.out.println("Validation finished.");
                    }
                });
            }
        }));
        return button;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(400, 20, 450, 250);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel qualityLabel = new JLabel("Image Quality");
        qualityLabel.setForeground(Color.BLACK);
        qualityLabel.setBounds(10, 10, 100, 25);
        qualityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(qualityLabel);

        String[] qualities = {"Low", "Medium", "High"};
        JComboBox<String> qualityBox = getStringJComboBox(qualities);
        qualityBox.setSelectedItem("Medium");
        panel.add(qualityBox);

        JLabel colorLabel = new JLabel("Select Operation Colors:");
        colorLabel.setForeground(Color.BLACK);
        colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        colorLabel.setBounds(10, 50, 200, 25);
        panel.add(colorLabel);

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resetButton.setBackground(new Color(220, 53, 69));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBounds(220, 50, 80, 25);
        resetButton.setFocusable(false);
        resetButton.setEnabled(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder());
        panel.add(resetButton);

        // Default operation colors
        defaultColors.put("Deleted", "Red");
        defaultColors.put("Added", "Green");
        defaultColors.put("Font Name", "Magenta");
        defaultColors.put("Font Size", "Blue");
        defaultColors.put("Font Style", "Cyan");
        defaultColors.put("Multiple Operation", "Black");

        String[] colors = {
                "Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Cyan", "Magenta", "Gray", "Black",
                "White", "Pink", "Brown", "Teal", "Navy", "Maroon", "Olive", "Gold", "Silver", "Light Gray"
        };

        // Left
        operationBoxes.put("Deleted", addOperationColorSetting(panel, "Deleted", colors, 80, "Red", true));
        operationBoxes.put("Added", addOperationColorSetting(panel, "Added", colors, 110, "Green", true));
        operationBoxes.put("Font Name", addOperationColorSetting(panel, "Font Name", colors, 140, "Magenta", true));

        // Right
        operationBoxes.put("Font Size", addOperationColorSetting(panel, "Font Size", colors, 80, "Blue", false));
        operationBoxes.put("Font Style", addOperationColorSetting(panel, "Font Style", colors, 110, "Cyan", false));
        operationBoxes.put("Multiple Operation", addOperationColorSetting(panel, "Multiple Operation", colors, 140, "Black", false));

        resetButton.addActionListener(e -> {
            operationBoxes.forEach((op, box) -> box.setSelectedItem(defaultColors.get(op)));
            System.out.println("Operation colors reset to default.");
        });

        return panel;
    }

    private static JComboBox<String> getStringJComboBox(String[] values) {
        JComboBox<String> box = new JComboBox<>(values);
        box.setBounds(120, 10, 100, 25);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.addActionListener(e -> Helper.setImageQuality((String) box.getSelectedItem()));
        return box;
    }

    private JComboBox<String> addOperationColorSetting(JPanel panel, String operation, String[] colors, int y, String defaultColor, boolean isLeft) {
        int x = isLeft ? 10 : 230;

        JLabel label = new JLabel(operation + ":");
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setBounds(x, y, 100, 25);

        JComboBox<String> box = new JComboBox<>(colors);
        box.setSelectedItem(defaultColor);
        box.setBounds(x + 110, y, 100, 25);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        box.addActionListener(e -> {
            String selected = (String) box.getSelectedItem();
            Helper.setOperationColor(operation, Helper.getColorFromName(selected));
            System.out.println(operation + " color set to: " + selected);
            checkResetButtonStatus();
        });

        panel.add(label);
        panel.add(box);
        return box;
    }



    private void setupConsole() {
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }



    private void setUIEnabled(boolean enabled) {
        for (JComboBox<String> box : operationBoxes.values()) {
            box.setEnabled(enabled);
        }
        resetButton.setEnabled(enabled); // You may conditionally enable this later
    }




    private void checkResetButtonStatus() {
        for (Map.Entry<String, JComboBox<String>> entry : operationBoxes.entrySet()) {
            String operation = entry.getKey();
            String current = (String) entry.getValue().getSelectedItem();
            String expected = defaultColors.get(operation);

            if (!current.equals(expected)) {
                resetButton.setEnabled(true);
                return;
            }
        }
        resetButton.setEnabled(false);
    }





    private void redirectSystemOut() {
        PrintStream ps = new PrintStream(new CustomOutputStream(console), true, StandardCharsets.UTF_8);
        System.setOut(ps);
        System.setErr(ps);
    }

}
