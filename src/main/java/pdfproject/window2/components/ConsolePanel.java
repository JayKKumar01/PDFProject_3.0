package pdfproject.window2.components;

import pdfproject.window2.stream.CustomOutputStream;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ConsolePanel extends JPanel {

    private final JTextPane textPane;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.CONSOLE_BG);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(ThemeManager.CONSOLE_BG);
        textPane.setForeground(ThemeManager.CONSOLE_TEXT);
        textPane.setFont(new Font("Consolas", Font.PLAIN, 12));

        // ✅ Padding inside console
        textPane.setBorder(new EmptyBorder(8, 10, 8, 10));

        // Important for dark themes
        textPane.setCaretColor(ThemeManager.CONSOLE_TEXT);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ThemeManager.CONSOLE_BG);

        add(scroll, BorderLayout.CENTER);

        hookSystemStreams();
    }

    private void hookSystemStreams() {
        StyledDocument doc = textPane.getStyledDocument();
        CustomOutputStream cos = new CustomOutputStream(textPane);

        System.setOut(new PrintStream(cos, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(cos.asErrorStream(), true, StandardCharsets.UTF_8));

        System.out.println("> Console ready...");
    }
}
