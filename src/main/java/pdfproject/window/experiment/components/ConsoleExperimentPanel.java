package pdfproject.window.experiment.components;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.stream.CustomOutputStream;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ConsoleExperimentPanel extends JPanel implements PropertyChangeListener {

    private final JTextPane consolePane;
    private final JScrollPane scrollPane;

    private final JLabel consoleLabel;  // <-- NEW bold label

    private final CustomOutputStream customStream;
    private final PrintStream savedOut;
    private final PrintStream savedErr;

    public ConsoleExperimentPanel() {
        setLayout(new BorderLayout());

        // --- NEW HEADER LABEL ---
        consoleLabel = new JLabel("CONSOLE");
        consoleLabel.setFont(new Font("Impact", Font.PLAIN, 22)); // <-- HARD + WIDE
        consoleLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));
        add(consoleLabel, BorderLayout.NORTH);



        // --- TEXT PANE ---
        consolePane = new JTextPane();
        consolePane.setEditable(false);
        consolePane.setFont(new Font("Consolas", Font.PLAIN, 13));

        scrollPane = new JScrollPane(consolePane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // --- STREAMS ---
        customStream = new CustomOutputStream(consolePane);

        savedOut = System.out;
        savedErr = System.err;

        PrintStream outPrintStream = createPrintStream(customStream);
        PrintStream errPrintStream = createPrintStream(customStream.asErrorStream());

        System.setOut(outPrintStream);
        System.setErr(errPrintStream);

        // Apply initial theme and register
        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private PrintStream createPrintStream(OutputStream os) {
        try {
            return new PrintStream(os, true, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new PrintStream(os);
        }
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.consoleBg);

        consoleLabel.setForeground(t.consoleText);   // <-- NEW label styling
        consolePane.setBackground(t.consoleBg);
        consolePane.setForeground(t.consoleText);
        scrollPane.getViewport().setBackground(t.consoleBg);

        revalidate();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Visual + Recolor text
        SwingUtilities.invokeLater(() -> {
            ExperimentTheme theme = ThemeManager.getTheme();
            applyTheme(theme);
            customStream.recolor(theme);
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // restore original system streams
        if (savedOut != null) System.setOut(savedOut);
        if (savedErr != null) System.setErr(savedErr);

        ThemeManager.unregister(this);
    }
}
