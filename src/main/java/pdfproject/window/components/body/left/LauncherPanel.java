package pdfproject.window.components.body.left;

import pdfproject.Config;
import pdfproject.Launcher; // keep if you have this entrypoint, otherwise replace the call inside startOperation()
import pdfproject.window.core.Theme;
import pdfproject.window.utils.ThemeManager;
import pdfproject.window.utils.ValidationCenter;
import pdfproject.window.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * LauncherPanel with Start / Stop behavior wired in.
 * - runs Launcher.start(() -> stoppedByUser) on a background thread
 * - toggles Start/Stop buttons and handles cancellation
 * - notifies ValidationCenter on start/stop
 *
 * DPI-aware via UiScale for padding, gaps and fonts.
 */
public class LauncherPanel extends JPanel implements PropertyChangeListener {

    private final JButton startButton;
    private final JButton stopButton;

    // background execution state
    private ExecutorService executorService;
    private Future<?> taskFuture;
    private volatile boolean isRunning = false;
    private volatile boolean stoppedByUser = false;

    public LauncherPanel() {
        // GridBagLayout centers content both vertically and horizontally
        setLayout(new GridBagLayout());

        // DPI-aware padding
        int pad = UiScale.scaleInt(12);
        setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

        // Create scaled fonts (compact)
        Font btnFont = UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 11));

        startButton = new JButton("Start");
        startButton.setFont(btnFont);

        stopButton  = new JButton("Stop");
        stopButton.setFont(btnFont);

        // wire actions to start/stop methods
        startButton.addActionListener(this::onStartClicked);
        stopButton.addActionListener(this::onStopClicked);

        // initial state
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // DPI-aware gap for FlowLayout
        int gap = UiScale.scaleInt(16);
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, gap, 0));
        row.setOpaque(false);
        row.add(startButton);
        row.add(stopButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;

        add(row, gbc);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    // Action handlers
    private void onStartClicked(ActionEvent ignored) {
        startOperation();
    }

    private void onStopClicked(ActionEvent ignored) {
        stopOperation();
    }

    // --- Start / Stop behavior (background execution) ---

    private void startOperation() {
        if (isRunning) return;
        if (!isInputPathValid()) {
            // optional: alert user via dialog or console
            System.out.println("⚠ No input path selected. Operation cannot start.");
            return;
        }

        isRunning = true;
        stoppedByUser = false;
        toggleButtons(false);

        // notify global center that validation started
        ValidationCenter.notifyStart();

        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "launcher-worker");
            t.setDaemon(true);
            return t;
        });

        taskFuture = executorService.submit(() -> {
            try {
                System.out.println("✅ Operation started");
                // Keep same contract as your old code — replace if needed
                Launcher.start(() -> stoppedByUser);
            } catch (Exception ex) {
                System.err.println("⚠ Error during operation: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                // ensure UI updates happen on EDT
                SwingUtilities.invokeLater(this::notifyOperationFinished);
            }
        });
    }

    private void stopOperation() {
        if (!isRunning) return;

        System.out.println("🛑 Stop requested.");
        stoppedByUser = true;

        if (taskFuture != null && !taskFuture.isDone()) {
            taskFuture.cancel(true);
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        // notify global center that user requested stop
        ValidationCenter.notifyStop();

        // UI will be updated when the background task terminates (notifyOperationFinished)
    }

    private void notifyOperationFinished() {
        if (!isRunning) return;

        isRunning = false;
        toggleButtons(true);

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        System.out.println(stoppedByUser ? "🛑 Operation stopped by user." : "✅ Operation finished.");

        // final notify to global center that operation finished
        ValidationCenter.notifyStop();
    }

    private boolean isInputPathValid() {
        try {
            return Config.inputPath != null && !Config.inputPath.trim().isEmpty();
        } catch (Throwable t) {
            // If Config is not available for some reason, treat as invalid
            return false;
        }
    }

    private void toggleButtons(boolean startEnabled) {
        if (SwingUtilities.isEventDispatchThread()) {
            startButton.setEnabled(startEnabled);
            stopButton.setEnabled(!startEnabled);
        } else {
            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(startEnabled);
                stopButton.setEnabled(!startEnabled);
            });
        }
    }

    // --- theming & lifecycle ---

    private void applyTheme(Theme t) {
        if (t == null) return;
        setBackground(t.bodyBg);

        Color startBg = t.startButtonColor != null ? t.startButtonColor : t.usernameAccent;
        Color stopBg  = t.stopButtonColor  != null ? t.stopButtonColor  : t.headerText;

        startButton.setBackground(startBg);
        startButton.setForeground(Theme.readableForeground(startBg));
        startButton.setOpaque(true);

        stopButton.setBackground(stopBg);
        stopButton.setForeground(Theme.readableForeground(stopBg));
        stopButton.setOpaque(true);

        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);

        // ensure background thread is shutdown if panel removed
        if (taskFuture != null && !taskFuture.isDone()) {
            taskFuture.cancel(true);
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
