package pdfproject.windowui.components;

import pdfproject.windowui.components.stream.CustomOutputStream;
import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintStream;

public class ConsolePanel extends JPanel {

    private final JTextPane consolePane;

    private static final Font CONSOLE_FONT = new Font("Consolas", Font.PLAIN, 14);

    public ConsolePanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.CONSOLE_BACKGROUND);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        consolePane = new JTextPane();
        consolePane.setEditable(false);
        consolePane.setFont(CONSOLE_FONT);
        consolePane.setBackground(ThemeColors.CONSOLE_TEXT_BG);

        JScrollPane scrollPane = new JScrollPane(consolePane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setDynamicHeight(int height) {
        setPreferredSize(new Dimension(0, height));
        revalidate();
    }

    public void redirectSystemStreams() {
        System.setOut(new PrintStream(new CustomOutputStream(consolePane, ThemeColors.THEME_BLUE), true));
        System.setErr(new PrintStream(new CustomOutputStream(consolePane, ThemeColors.THEME_RED), true));
    }

    public void clear() {
        consolePane.setText("");
    }
}
