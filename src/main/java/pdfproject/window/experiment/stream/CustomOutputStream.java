package pdfproject.window.experiment.stream;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * CustomOutputStream that writes to a JTextPane's StyledDocument.
 * Error color is taken from ExperimentColors.CONSOLE_ERROR (theme-independent).
 */
public final class CustomOutputStream extends OutputStream {

    private final JTextPane textPane;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);

    // Error color is fixed (always uses ExperimentColors.CONSOLE_ERROR).
    private static final Color ERROR_COLOR = ExperimentTheme.CONSOLE_ERROR;

    public CustomOutputStream(JTextPane textPane) {
        this.textPane = textPane;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        buffer.write(b);
        if (b == '\n') flushBuffer();
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        int start = off;
        for (int i = off; i < off + len; i++) {
            if (b[i] == '\n') {
                buffer.write(b, start, i - start + 1);
                flushBuffer();
                start = i + 1;
            }
        }
        if (start < off + len) buffer.write(b, start, off + len - start);
    }

    @Override
    public synchronized void flush() throws IOException {
        if (buffer.size() > 0) flushBuffer();
    }

    @Override
    public synchronized void close() throws IOException {
        flush();
    }

    public OutputStream asErrorStream() {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                synchronized (CustomOutputStream.this) {
                    buffer.write(b);
                    if (b == '\n') flushBuffer(true);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                synchronized (CustomOutputStream.this) {
                    int start = off;
                    for (int i = off; i < off + len; i++) {
                        if (b[i] == '\n') {
                            buffer.write(b, start, i - start + 1);
                            flushBuffer(true);
                            start = i + 1;
                        }
                    }
                    if (start < off + len) buffer.write(b, start, off + len - start);
                }
            }

            @Override
            public void flush() throws IOException {
                synchronized (CustomOutputStream.this) {
                    if (buffer.size() > 0) flushBuffer(true);
                }
            }

            @Override
            public void close() throws IOException {
                flush();
            }
        };
    }

    private void flushBuffer() {
        flushBuffer(false);
    }

    private void flushBuffer(boolean asError) {
        final String text = buffer.toString(StandardCharsets.UTF_8);
        buffer.reset();
        if (text.isEmpty()) return;

        final Color normalColor = ThemeManager.getTheme().consoleText;
        final Color runColor = asError ? ERROR_COLOR : normalColor;

        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, runColor);
            attrs.addAttribute("isError", asError ? Boolean.TRUE : Boolean.FALSE);

            try {
                doc.insertString(doc.getLength(), text, attrs);
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Recolor all existing non-error runs in the console to match the supplied theme's consoleText.
     */
    public void recolor(ExperimentTheme theme) {
        if (theme == null) return;

        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = textPane.getStyledDocument();
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
                    StyleConstants.setForeground(newAttrs, theme.consoleText);
                    newAttrs.addAttribute("isError", Boolean.FALSE);
                    doc.setCharacterAttributes(start, end - start, newAttrs, false);
                }
                pos = end;
            }
        });
    }
}
