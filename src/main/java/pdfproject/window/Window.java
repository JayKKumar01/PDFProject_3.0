package pdfproject.window;

import pdfproject.Config;
import pdfproject.Launcher;
import pdfproject.constants.AppPaths;
import pdfproject.constants.OperationColor;
import pdfproject.interfaces.LauncherListener;
import pdfproject.window.utils.CustomOutputStream;

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
        panel.setBackground(new Color(70, 70, 70));
        panel.setPreferredSize(new Dimension(w, h));
        return panel;
    }

    private JScrollPane createConsoleScrollPane(int w, int h) {
        console.setBackground(new Color(40, 40, 40));
        console.setForeground(Color.WHITE);
        console.setEditable(false);
        console.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        redirectSystemOut();

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(w, h));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private void setupConsole() {
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(70, 70, 70));
        panel.setBounds(0, 50, 300, 40);

        JButton browseButton = new JButton("Choose InputData");
        browseButton.setFocusable(false);

        JLabel fileLabel = new JLabel("No File Chosen");
        fileLabel.setForeground(Color.WHITE);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
//                testEmojiConsoleOutput();

            }
        });


        panel.add(browseButton);
        panel.add(fileLabel);
        return panel;
    }

    private void testEmojiConsoleOutput() {
        System.out.println("▶️ Starting process...");
        System.out.println("⚠️ Warning: Mismatch in page count.");
        System.out.println("📝 Beginning validation...");
        System.out.println("📄 Page details shown here.");
        System.out.println("🔍 Validating alignment...");
        System.out.println("🧠 Validating content...");
        System.out.println("✅ Validation complete.");
        System.out.println("✔️ All pages validated successfully.");
    }


    private void setUIEnabled(boolean enabled) {
        for (JComboBox<String> box : operationBoxes.values()) {
            box.setEnabled(enabled);
        }
        resetButton.setEnabled(enabled); // You may conditionally enable this later
    }


    private JButton createStartButton() {
        JButton button = new JButton("Start Validation");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!Config.INPUT_PATH.isEmpty()) {
                            System.out.println("Launcher Started!");
                            Launcher.start(new LauncherListener() {
                                @Override
                                public void onStart() {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setUIEnabled(false);
                                        }
                                    });
                                    System.out.println("Validation started...");
                                }

                                @Override
                                public void onFinish() {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setUIEnabled(true);
                                            checkResetButtonStatus(); // Recheck reset status after task
                                        }
                                    });
                                    System.out.println("Validation finished.");
                                }
                            });

                        }
                    }
                });
            }
        });
        return button;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(400, 20, 450, 250);
        panel.setBackground(new Color(60, 60, 60));
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        String[] qualities = {"Low", "Medium", "High"};


        JLabel qualityLabel = new JLabel("Image Quality");
        qualityLabel.setForeground(Color.WHITE);
        qualityLabel.setBounds(10, 10, 100, 25);

        JComboBox<String> qualityBox = getStringJComboBox(qualities);
        qualityBox.setSelectedItem(qualities[1]);
        panel.add(qualityLabel);
        panel.add(qualityBox);

        String[] colors = {
                "Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Cyan", "Magenta", "Gray", "Black",
                "White", "Pink", "Brown", "Teal", "Navy", "Maroon", "Olive", "Gold", "Silver", "Light Gray"
        };

        JLabel colorLabel = new JLabel("Select Operation Colors:");
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setBounds(10, 50, 200, 25);
        panel.add(colorLabel);

        resetButton = new JButton("Reset");
        resetButton.setBounds(220, 50, 80, 25);
        resetButton.setFocusable(false);
        panel.add(resetButton);

        resetButton.setEnabled(false);

        // Default operation colors
        defaultColors.put("Deleted", "Red");
        defaultColors.put("Added", "Green");
        defaultColors.put("Font Name", "Magenta");
        defaultColors.put("Font Size", "Blue");
        defaultColors.put("Font Style", "Cyan");
        defaultColors.put("Multiple Operation", "Black");

        // Left column
        operationBoxes.put("Deleted", addOperationColorSetting(panel, "Deleted", colors, 80, "Red", true));
        operationBoxes.put("Added", addOperationColorSetting(panel, "Added", colors, 110, "Green", true));
        operationBoxes.put("Font Name", addOperationColorSetting(panel, "Font Name", colors, 140, "Magenta", true));

        // Right column
        operationBoxes.put("Font Size", addOperationColorSetting(panel, "Font Size", colors, 80, "Blue", false));
        operationBoxes.put("Font Style", addOperationColorSetting(panel, "Font Style", colors, 110, "Cyan", false));
        operationBoxes.put("Multiple Operation", addOperationColorSetting(panel, "Multiple Operation", colors, 140, "Black", false));

        // Reset button functionality
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operationBoxes.get("Deleted").setSelectedItem("Red");
                operationBoxes.get("Added").setSelectedItem("Green");
                operationBoxes.get("Font Name").setSelectedItem("Magenta");
                operationBoxes.get("Font Size").setSelectedItem("Blue");
                operationBoxes.get("Font Style").setSelectedItem("Cyan");
                operationBoxes.get("Multiple Operation").setSelectedItem("Black");

                System.out.println("Operation colors reset to default.");
            }
        });

        return panel;
    }

    private static JComboBox<String> getStringJComboBox(String[] qualities) {
        JComboBox<String> qualityBox = new JComboBox<>(qualities);
        qualityBox.setBounds(120, 10, 100, 25);

        qualityBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedQuality = (String) qualityBox.getSelectedItem();

                switch (selectedQuality) {
                    case "Low":
                        Config.RENDER_DPI = 72;
                        break;
                    case "Medium":
                        Config.RENDER_DPI = 100;
                        break;
                    case "High":
                        Config.RENDER_DPI = 200;
                        break;
                }

                System.out.println("Image Quality set to: " + selectedQuality + " (" + Config.RENDER_DPI + " DPI)");
            }
        });
        return qualityBox;
    }

    private JComboBox<String> addOperationColorSetting(JPanel panel, String operation, String[] colors, int y, String defaultColor, boolean isLeft) {
        int x = isLeft ? 10 : 230;

        JLabel label = new JLabel(operation + ":");
        label.setForeground(Color.WHITE);
        label.setBounds(x, y, 100, 25);

        JComboBox<String> box = new JComboBox<>(colors);
        box.setSelectedItem(defaultColor);
        box.setBounds(x + 110, y, 100, 25);

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedColor = (String) box.getSelectedItem();
                Color color = getColorFromName(selectedColor);


                switch (operation) {
                    case "Deleted":
                        OperationColor.DELETED = color;
                        break;
                    case "Added":
                        OperationColor.ADDED = color;
                        break;
                    case "Font Name":
                        OperationColor.FONT_NAME = color;
                        break;
                    case "Font Size":
                        OperationColor.FONT_SIZE = color;
                        break;
                    case "Font Style":
                        OperationColor.FONT_STYLE = color;
                        break;
                    case "Multiple Operation":
                        OperationColor.MULTIPLE = color;
                        break;
                }

                System.out.println(operation + " color set to: " + selectedColor);
                checkResetButtonStatus(); // Call this on every change
            }
        });

        panel.add(label);
        panel.add(box);
        return box;
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


    private Color getColorFromName(String name) {
        switch (name.toLowerCase()) {
            case "red":
                return Color.RED;
            case "green":
                return Color.GREEN;
            case "blue":
                return Color.BLUE;
            case "yellow":
                return Color.YELLOW;
            case "orange":
                return Color.ORANGE;
            case "purple":
                return new Color(128, 0, 128);
            case "cyan":
                return Color.CYAN;
            case "magenta":
                return Color.MAGENTA;
            case "gray":
                return Color.GRAY;
            case "white":
                return Color.WHITE;
            case "pink":
                return Color.PINK;
            case "brown":
                return new Color(139, 69, 19);
            case "teal":
                return new Color(0, 128, 128);
            case "navy":
                return new Color(0, 0, 128);
            case "maroon":
                return new Color(128, 0, 0);
            case "olive":
                return new Color(128, 128, 0);
            case "gold":
                return new Color(255, 215, 0);
            case "silver":
                return new Color(192, 192, 192);
            case "light gray":
                return Color.LIGHT_GRAY;
            default:
                return Color.BLACK;
        }
    }


    private void redirectSystemOut() {
        PrintStream ps = new PrintStream(new CustomOutputStream(console), true, StandardCharsets.UTF_8);
        System.setOut(ps);
        System.setErr(ps);
    }

}
