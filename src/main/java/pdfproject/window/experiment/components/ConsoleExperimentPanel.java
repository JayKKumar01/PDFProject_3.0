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

/**
 * Console panel that wires System.out/System.err to a CustomOutputStream.
 * Recolors existing console text on theme changes.
 */
public class ConsoleExperimentPanel extends JPanel implements PropertyChangeListener {

    private final JTextPane consolePane;
    private final JScrollPane scrollPane;

    private final CustomOutputStream customStream;
    private final PrintStream savedOut;
    private final PrintStream savedErr;

    public ConsoleExperimentPanel() {
        setLayout(new BorderLayout());

        consolePane = new JTextPane();
        consolePane.setEditable(false);
        consolePane.setFont(new Font("Consolas", Font.PLAIN, 13));
        scrollPane = new JScrollPane(consolePane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        // Create and wire streams
        customStream = new CustomOutputStream(consolePane);

        // preserve original streams so we can restore them later
        savedOut = System.out;
        savedErr = System.err;

        PrintStream outPrintStream = createPrintStream(customStream);
        PrintStream errPrintStream = createPrintStream(customStream.asErrorStream());

        System.setOut(outPrintStream);
        System.setErr(errPrintStream);

        // Apply initial theme and register listener
        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private PrintStream createPrintStream(OutputStream os) {
        try {
            return new PrintStream(os, true, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // fallback (shouldn't happen)
            return new PrintStream(os);
        }
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.consoleBg);
        consolePane.setBackground(t.consoleBg);
        consolePane.setForeground(t.consoleText);
        // scroll viewport background
        scrollPane.getViewport().setBackground(t.consoleBg);

        revalidate();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // update visuals and recolor existing text
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
