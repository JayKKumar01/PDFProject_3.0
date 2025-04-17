package pdfproject.window.utils;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {
    private final JEditorPane console;

    public ConsoleOutputStream(JEditorPane console) {
        this.console = console;
    }

    @Override
    public void write(int b) throws IOException {
        SwingUtilities.invokeLater(() -> {
            try {
                String text = String.valueOf((char) b);
                String currentText = console.getText();
                console.setText(currentText + text);
                // Optionally, you can wrap the text in a style tag to ensure it's white
                console.setText("<html><body style='font-family:Segoe UI Emoji; font-size:14px; color:white;'>" + currentText + text + "</body></html>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
