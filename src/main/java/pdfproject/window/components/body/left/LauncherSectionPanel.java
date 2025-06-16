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

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(startButton);
        contentPanel.add(stopButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(contentPanel, gbc);
    }

    private void startValidation() {
        if (isValidationRunning) return;

        if (Config.INPUT_PATH == null || Config.INPUT_PATH.trim().isEmpty()) {
            System.out.println("⚠ No input path selected. Validation cannot start.");
            return;
        }

        isValidationRunning = true;
        if (taskStateListener != null) {
            taskStateListener.onStart();
        }
        stoppedByUser = false;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        executorService = Executors.newSingleThreadExecutor();
        validationTask = executorService.submit(() -> {
            try {
                System.out.println("✅ Validation started");
                Launcher.start(stopListener);
            } catch (Exception ex) {
                System.out.println("⚠ Error during validation: " + ex.getMessage());
            } finally {
                SwingUtilities.invokeLater(this::notifyValidationFinished);
            }
        });
    }

    private void stopValidation() {
        if (!isValidationRunning || executorService == null || executorService.isShutdown()) return;

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
        if (taskStateListener != null) {
            taskStateListener.onStop();
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        if (stoppedByUser) {
            System.out.println("🛑 Validation stopped by user.");
        } else {
            System.out.println("🛑 Validation finished.");
        }
    }
    public void setTaskStateListener(TaskStateListener taskStateListener){
        this.taskStateListener = taskStateListener;
    }
    private final StopListener stopListener = () -> stoppedByUser;
}
