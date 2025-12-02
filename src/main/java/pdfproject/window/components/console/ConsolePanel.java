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

        // register to theme manager
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
        // ensure streams are set to initial theme colors
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
     * Default redirect — uses current theme to pick normal output color
     * (blue in light, green in dark). Error color remains red.
     */
    public void redirectSystemStreams() {
        boolean dark = ThemeManager.isDarkMode();

        Color outColor = dark ? ThemeColors.THEME_GREEN : ThemeColors.THEME_BLUE;
        Color errColor = ThemeColors.THEME_RED;

        System.setOut(new PrintStream(new CustomOutputStream(consolePane, outColor), true));
        System.setErr(new PrintStream(new CustomOutputStream(consolePane, errColor), true));
    }

    /**
     * Overload to explicitly set normal output color
     */
    public void redirectSystemStreams(Color normalColor) {
        System.setOut(new PrintStream(new CustomOutputStream(consolePane, normalColor), true));
        System.setErr(new PrintStream(new CustomOutputStream(consolePane, ThemeColors.THEME_RED), true));
    }

    public void clear() {
        consolePane.setText("");
    }

    /**
     * Apply theme colors to visuals (panel, scroll, background)
     */
    private void applyTheme(boolean dark) {

        // outer panel
        setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.LAYOUT_BORDER);

        // scroll pane
        scrollPane.setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.CONSOLE_BACKGROUND);
        scrollPane.getViewport().setBackground(dark ? ThemeColors.DARK_BACKGROUND : ThemeColors.CONSOLE_TEXT_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                dark ? ThemeColors.DARK_TEXT_MUTED : ThemeColors.CONSOLE_BORDER
        ));

        // text area background (foreground handled per-run)
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
     * ThemeManager listener — recolor previous non-error text and update streams.
     */
    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);

        // recolor existing non-error text runs
        StyledDocument doc = consolePane.getStyledDocument();
        int len = doc.getLength();
        int pos = 0;

        while (pos < len) {
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            Object isErrorAttr = attrs.getAttribute("isError");
            boolean isError = Boolean.TRUE.equals(isErrorAttr);

            int start = elem.getStartOffset();
            int end = elem.getEndOffset();
            int length = end - start;

            if (!isError && length > 0) {
                // apply new foreground for this range; merge with existing attributes
                SimpleAttributeSet newAttrs = new SimpleAttributeSet();
                StyleConstants.setForeground(newAttrs, dark ? ThemeColors.THEME_GREEN : ThemeColors.THEME_BLUE);
                // do not overwrite the isError marker or other attributes - merge instead
                doc.setCharacterAttributes(start, length, newAttrs, false);
            }

            pos = end;
        }

        // rebind streams so new writes use the new colors
        redirectSystemStreams();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
