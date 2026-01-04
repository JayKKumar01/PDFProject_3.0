package pdfproject.window2.components.content;

import pdfproject.Config;
import pdfproject.Launcher;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActionPanel extends JPanel {

    private final JButton startButton;
    private final JButton stopButton;

    private final JComponent[] panelsToDisable;

    private ExecutorService executor;
    private Future<?> task;

    private volatile boolean isRunning = false;
    private volatile boolean stoppedByUser = false;

    public ActionPanel(JComponent... panelsToDisable) {
        this.panelsToDisable = panelsToDisable;

        // 🔴 OUTER PANEL: stretches, no border
        setOpaque(false);
        setLayout(new GridBagLayout()); // perfect centering, no glue needed

        // =====================================================
        // INNER PANEL: content-wrapped, bordered
        // =====================================================
        JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        content.setOpaque(true);
        content.setBackground(ThemeManager.CONSOLE_BG);
        content.setBorder(new MatteBorder(
                3, 1, 0, 1,
                ThemeManager.ACCENT_PRIMARY
        ));

        startButton = new JButton("Start");
        stopButton  = new JButton("Stop");

        startButton.setBackground(ThemeManager.ACCENT_PRIMARY);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);

        stopButton.setBackground(ThemeManager.CONSOLE_ERROR);
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);

        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());

        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        content.add(startButton);
        content.add(stopButton);

        // GridBag centers content both axes
        add(content);
    }

    // =====================================================
    // Logic
    // =====================================================
    private void start() {
        if (isRunning) return;
        if (!isInputValid()) return;

        isRunning = true;
        stoppedByUser = false;

        toggleButtons(false);
        setPanelsEnabled(false);

        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "action-worker");
            t.setDaemon(true);
            return t;
        });

        task = executor.submit(() -> {
            try {
                Launcher.start(() -> stoppedByUser);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(this::onFinished);
            }
        });
    }

    private void stop() {
        if (!isRunning) return;

        stoppedByUser = true;

        if (task != null && !task.isDone()) {
            task.cancel(true);
        }

        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private void onFinished() {
        isRunning = false;

        toggleButtons(true);
        setPanelsEnabled(true);

        if (executor != null) {
            executor.shutdownNow();
        }
    }

    // =====================================================
    // Helpers
    // =====================================================
    private boolean isInputValid() {
        return Config.inputPath != null && !Config.inputPath.trim().isEmpty();
    }

    private void toggleButtons(boolean startEnabled) {
        startButton.setEnabled(startEnabled);
        stopButton.setEnabled(!startEnabled);
    }

    private void setPanelsEnabled(boolean enabled) {
        for (JComponent panel : panelsToDisable) {
            setComponentTreeEnabled(panel, enabled);
        }
    }

    private void setComponentTreeEnabled(Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                setComponentTreeEnabled(child, enabled);
            }
        }
    }
}
