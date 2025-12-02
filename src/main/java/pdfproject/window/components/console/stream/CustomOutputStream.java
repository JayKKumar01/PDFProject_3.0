package pdfproject.window.components.console.stream;

import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class CustomOutputStream extends OutputStream {

    private final JTextPane textPane;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final Color textColor;

    public CustomOutputStream(JTextPane textPane, Color textColor) {
        this.textPane = textPane;
        this.textColor = textColor;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
        if (b == '\n') {
            flushBuffer();
        }
    }

    private void flushBuffer() throws IOException {
        String text = buffer.toString(StandardCharsets.UTF_8);
        buffer.reset();

        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = textPane.getStyledDocument();

            // Build attributes for this run
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, textColor);
            // mark error runs so theme switching can skip recoloring them
            boolean isError = ThemeColors.THEME_RED.equals(textColor);
            attrs.addAttribute("isError", isError);

            try {
                // Insert the text with attributes
                doc.insertString(doc.getLength(), text, attrs);
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
    }

    @Override
    public void close() throws IOException {
        flushBuffer();
    }
}
