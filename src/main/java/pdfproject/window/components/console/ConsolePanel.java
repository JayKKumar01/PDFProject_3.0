package pdfproject.window.components.console;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.components.console.stream.CustomOutputStream;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.io.PrintStream;

public class ConsolePanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private static final Font CONSOLE_FONT = new Font("Consolas", Font.PLAIN, 14);

    private final JTextPane consolePane;
    private JScrollPane scrollPane;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        consolePane = createConsolePane();
        scrollPane = wrapInScrollPane(consolePane);

        add(scrollPane, BorderLayout.CENTER);

        ThemeManager.register(this);

        applyTheme(ThemeManager.isDarkMode());
        redirectSystemStreams();
    }

    private JTextPane createConsolePane() {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setFont(CONSOLE_FONT);
        return pane;
    }

    private JScrollPane wrapInScrollPane(JTextPane pane) {
        JScrollPane scroll = new JScrollPane(pane);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setOpaque(true);
        scroll.getViewport().setOpaque(true);
        return scroll;
    }

    public void setDynamicHeight(int height) {
        setPreferredSize(new Dimension(0, height));
        revalidate();
    }

    /**
     * Redirect stdout/stderr using theme-appropriate colors.
     */
    public void redirectSystemStreams() {
        boolean dark = ThemeManager.isDarkMode();

        Color outColor = dark ? ThemeColors.THEME_GREEN : ThemeColors.THEME_BLUE;
        Color errColor = ThemeColors.THEME_RED;

        System.setOut(new PrintStream(new CustomOutputStream(consolePane, outColor), true));
        System.setErr(new PrintStream(new CustomOutputStream(consolePane, errColor), true));
    }

    /**
     * Explicit override if needed.
     */
    public void redirectSystemStreams(Color normalColor) {
        System.setOut(new PrintStream(new CustomOutputStream(consolePane, normalColor), true));
        System.setErr(new PrintStream(new CustomOutputStream(consolePane, ThemeColors.THEME_RED), true));
    }

    public void clear() {
        consolePane.setText("");
    }

    /**
     * Apply theme colors to the visual elements.
     */
    private void applyTheme(boolean dark) {

        // Outer background — fixed to correct behavior
        setBackground(dark ? ThemeColors.DARK_LAYOUT_BORDER : ThemeColors.LAYOUT_BORDER);

        // Scroll pane
        scrollPane.setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.CONSOLE_BACKGROUND);
        scrollPane.getViewport().setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.CONSOLE_TEXT_BG);

        // Border — NOW THEME GREEN in DARK MODE (your request)
        scrollPane.setBorder(BorderFactory.createLineBorder(
                dark ? ThemeColors.THEME_GREEN : ThemeColors.CONSOLE_BORDER
        ));

        // Console background
        consolePane.setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.CONSOLE_TEXT_BG);

        revalidate();
        repaint();
    }

    @Override
    public void onStart() {
        clear();
    }

    @Override
    public void onStop() {
        // reserved
    }

    /**
     * Theme changed → recolor previous text + update streams + repaint visuals
     */
    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);

        StyledDocument doc = consolePane.getStyledDocument();
        int len = doc.getLength();
        int pos = 0;

        while (pos < len) {
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();

            boolean isError = Boolean.TRUE.equals(attrs.getAttribute("isError"));
            int start = elem.getStartOffset();
            int end = elem.getEndOffset();

            if (!isError) {
                SimpleAttributeSet newAttrs = new SimpleAttributeSet();
                StyleConstants.setForeground(newAttrs, dark ? ThemeColors.THEME_GREEN : ThemeColors.THEME_BLUE);
                doc.setCharacterAttributes(start, end - start, newAttrs, false);
            }

            pos = end;
        }

        redirectSystemStreams();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
