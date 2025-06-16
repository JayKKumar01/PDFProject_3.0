package pdfproject.window.components.console;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.components.console.stream.CustomOutputStream;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintStream;

public class ConsolePanel extends JPanel implements TaskStateListener {

    private static final Font CONSOLE_FONT = new Font("Consolas", Font.PLAIN, 14);
    private final JTextPane consolePane;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.LAYOUT_BORDER);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        consolePane = createConsolePane();
        JScrollPane scrollPane = wrapInScrollPane(consolePane);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JTextPane createConsolePane() {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setFont(CONSOLE_FONT);
        pane.setBackground(ThemeColors.CONSOLE_TEXT_BG);
        return pane;
    }

    private JScrollPane wrapInScrollPane(JTextPane pane) {
        JScrollPane scrollPane = new JScrollPane(pane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return scrollPane;
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

    @Override
    public void onStart() {
        clear();
    }

    @Override
    public void onStop() {
        // Future behavior on stop can be added here
    }
}
