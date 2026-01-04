package pdfproject.window.components.console;

import pdfproject.window.components.GradientHeaderPanel;
import pdfproject.window.components.console.stream.CustomOutputStream;
import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ConsolePanel extends JPanel {

    private final JTextPane textPane;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.CONSOLE_BG);

        textPane = createTextPane();

        add(createHeader(), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);

        hookSystemStreams();
    }

    // ---------------- Header ----------------

    private JComponent createHeader() {
        GradientHeaderPanel header = new GradientHeaderPanel(
                ThemeManager.ACCENT_PRIMARY,
                ThemeManager.ACCENT_SOFT
        );
        header.setPreferredSize(new Dimension(10, 30));

        JLabel title = new JLabel("CONSOLE");
        title.setFont(new Font("Impact", Font.BOLD, 20));
        title.setForeground(ThemeManager.CONSOLE_BG); // readable on green gradient
        title.setBorder(new EmptyBorder(0, 12, 0, 0));

        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ---------------- Text Pane ----------------

    private JTextPane createTextPane() {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(ThemeManager.CONSOLE_BG);
        pane.setForeground(ThemeManager.CONSOLE_TEXT);
        pane.setFont(new Font("Consolas", Font.PLAIN, 12));
        pane.setCaretColor(ThemeManager.CONSOLE_TEXT);
        pane.setBorder(new EmptyBorder(8, 10, 8, 10));
        return pane;
    }

    private JComponent createScrollPane() {
        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ThemeManager.CONSOLE_BG);
        return scroll;
    }

    // ---------------- Streams ----------------

    private void hookSystemStreams() {
        CustomOutputStream out = new CustomOutputStream(textPane);

        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(out.asErrorStream(), true, StandardCharsets.UTF_8));

        System.out.println("> Console ready.");
    }
}
