package pdfproject.window.components.body.left;

import pdfproject.Config;
import pdfproject.Launcher;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ComponentFactory;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LauncherSectionPanel extends JPanel implements ThemeManager.ThemeChangeListener {

    private final JButton startButton;
    private final JButton stopButton;
    private final JPanel buttonPanel;

    private ExecutorService executorService;
    private Future<?> validationTask;
    private boolean isValidationRunning = false;
    private boolean stoppedByUser = false;

    private TaskStateListener taskStateListener;

    public LauncherSectionPanel() {
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BACKGROUND);

        // NOTE: createStyledButton(textColorLight, bgColorLight)
        // We want: light theme -> green button with white text.
        startButton = ComponentFactory.createStyledButton("Start Validation",
                ThemeColors.CONSOLE_TEXT_BG, ThemeColors.THEME_GREEN);

        stopButton = ComponentFactory.createStyledButton("Stop Validation",
                ThemeColors.CONSOLE_TEXT_BG, ThemeColors.THEME_RED);

        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startValidation());
        stopButton.addActionListener(e -> stopValidation());

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(ThemeColors.BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        add(buttonPanel);

        // Register with ThemeManager and apply initial theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
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
        if (Config.inputPath == null || Config.inputPath.trim().isEmpty()) {
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

    /**
     * ThemeManager.ThemeChangeListener implementation
     */
    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);
    }

    /**
     * Apply theme for this panel only (light: blue/white, dark: green/black)
     */
    private void applyTheme(boolean dark) {
        if (dark) {
            setBackground(ThemeColors.DARK_BACKGROUND);
            buttonPanel.setBackground(ThemeColors.DARK_BACKGROUND);

            // Buttons are theme-aware via ComponentFactory; ensure readable override where needed
            startButton.setForeground(ThemeColors.DARK_BACKGROUND);
            startButton.setBackground(ThemeColors.THEME_GREEN);
            startButton.setOpaque(true);

            stopButton.setForeground(ThemeColors.DARK_BACKGROUND);
            stopButton.setBackground(ThemeColors.THEME_RED);
            stopButton.setOpaque(true);
        } else {
            setBackground(ThemeColors.BACKGROUND);
            buttonPanel.setBackground(ThemeColors.BACKGROUND);

            startButton.setForeground(ThemeColors.CONSOLE_TEXT_BG);
            startButton.setBackground(ThemeColors.THEME_GREEN);
            startButton.setOpaque(true);

            stopButton.setForeground(ThemeColors.CONSOLE_TEXT_BG);
            stopButton.setBackground(ThemeColors.THEME_RED);
            stopButton.setOpaque(true);
        }

        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
