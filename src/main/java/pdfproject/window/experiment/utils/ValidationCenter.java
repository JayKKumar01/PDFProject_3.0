package pdfproject.window.experiment.utils;

import javax.swing.*;

/**
 * Centralized notifier with only two events: onStart and onStop.
 * Any class can register one global listener.
 */
public final class ValidationCenter {

    private static ValidationListener listener;

    private ValidationCenter() {}

    /** Listener interface (only start / stop). */
    public interface ValidationListener {
        void onStart();
        void onStop();
    }

    /** Set the single global listener. */
    public static void setListener(ValidationListener l) {
        listener = l;
    }

    /** Fired by LauncherPanel or any trigger. */
    public static void notifyStart() {
        if (listener != null) {
            SwingUtilities.invokeLater(listener::onStart);
        }
    }

    /** Fired by LauncherPanel or any trigger. */
    public static void notifyStop() {
        if (listener != null) {
            SwingUtilities.invokeLater(listener::onStop);
        }
    }
}
