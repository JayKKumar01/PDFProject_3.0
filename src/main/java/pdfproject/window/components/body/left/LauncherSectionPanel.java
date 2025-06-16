package pdfproject.window.components.body.left;

import pdfproject.Config;
import pdfproject.Launcher;
import pdfproject.interfaces.StopListener;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LauncherSectionPanel extends JPanel {

    private final JButton startButton;
    private final JButton stopButton;

    private ExecutorService executorService;
    private Future<?> validationTask;
    private boolean isValidationRunning = false;
    private boolean stoppedByUser = false;

    private TaskStateListener taskStateListener;

    public LauncherSectionPanel() {
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BACKGROUND);

        startButton = ComponentFactory.createStyledButton("Start Validation", new Color(76, 175, 80), Color.WHITE);
        stopButton = ComponentFactory.createStyledButton("Stop Validation", new Color(244, 67, 54), Color.WHITE);
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startValidation());
        stopButton.addActionListener(e -> stopValidation());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(ThemeColors.BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        add(buttonPanel);
    }

    private void startValidation() {
        if (isValidationRunning || !isInputPathValid()) return;

        isValidationRunning = true;
        stoppedByUser = false;
        toggleButtons(false);
        notifyTaskStart();

        executorService = Executors.newSingleThreadExecutor();
        validationTask = executorService.submit(() -> {
            try {
                System.out.println("✅ Validation started");
                Launcher.start(() -> stoppedByUser);
            } catch (Exception ex) {
                System.out.println("⚠ Error during validation: " + ex.getMessage());
            } finally {
                SwingUtilities.invokeLater(this::notifyValidationFinished);
            }
        });
    }

    private void stopValidation() {
        if (!isValidationRunning || executorService == null) return;

        System.out.println("🛑 Stop requested.");
        stoppedByUser = true;

        if (validationTask != null && !validationTask.isDone()) {
            validationTask.cancel(true);
        }

        executorService.shutdownNow();
    }

    private void notifyValidationFinished() {
        if (!isValidationRunning) return;

        isValidationRunning = false;
        toggleButtons(true);
        notifyTaskStop();

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        System.out.println(stoppedByUser ? "🛑 Validation stopped by user." : "🛑 Validation finished.");
    }

    private boolean isInputPathValid() {
        if (Config.INPUT_PATH == null || Config.INPUT_PATH.trim().isEmpty()) {
            System.out.println("⚠ No input path selected. Validation cannot start.");
            return false;
        }
        return true;
    }

    private void toggleButtons(boolean startEnabled) {
        startButton.setEnabled(startEnabled);
        stopButton.setEnabled(!startEnabled);
    }

    private void notifyTaskStart() {
        if (taskStateListener != null) {
            taskStateListener.onStart();
        }
    }

    private void notifyTaskStop() {
        if (taskStateListener != null) {
            taskStateListener.onStop();
        }
    }

    public void setTaskStateListener(TaskStateListener taskStateListener) {
        this.taskStateListener = taskStateListener;
    }
}
