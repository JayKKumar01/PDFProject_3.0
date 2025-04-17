package pdfproject.window.utils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class CustomOutputStream extends OutputStream {
    private final JTextArea textArea;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;

    }

    @Override
    public void write(int b) throws IOException {
        // Collect the byte into buffer
        buffer.write(b);
        if (b == '\n') {
            flushBuffer();
        }
    }

    private void flushBuffer() throws IOException {
        // Convert buffered bytes into a full string using UTF-8
        String text = buffer.toString(StandardCharsets.UTF_8);
        buffer.reset();

        SwingUtilities.invokeLater(() -> {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getDocument().getLength());
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
