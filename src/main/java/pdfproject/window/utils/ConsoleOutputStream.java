package pdfproject.window.utils;

import java.io.*;
import javax.swing.*;

public class ConsoleOutputStream extends PrintStream {
    private JEditorPane editorPane;

    public ConsoleOutputStream(JEditorPane editorPane) {
        super(new ByteArrayOutputStream());
        this.editorPane = editorPane;
    }

    @Override
    public void println(String x) {
        String currentText = editorPane.getText();
        int bodyEndIndex = currentText.lastIndexOf("</body>");
        if (bodyEndIndex != -1) {
            String newText = currentText.substring(0, bodyEndIndex)
                    + "<br>" + x + "</body></html>";
            editorPane.setText(newText);
        }
    }

    @Override
    public void print(String s) {
        String currentText = editorPane.getText();
        int bodyEndIndex = currentText.lastIndexOf("</body>");
        if (bodyEndIndex != -1) {
            String newText = currentText.substring(0, bodyEndIndex)
                    + s + "</body></html>";
            editorPane.setText(newText);
        }
    }
}
