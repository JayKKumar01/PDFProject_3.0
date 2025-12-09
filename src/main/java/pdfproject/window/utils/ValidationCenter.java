package pdfproject.window.utils;

import javax.swing.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Centralized notifier with only two events: onStart and onStop.
 * Supports multiple listeners in a thread-safe way.
 *
 * Backwards-compat: setListener(l) will add(l); setListener(null) clears all listeners.
 * Prefer addListener/removeListener in new code.
 */
public final class ValidationCenter {

    private static final CopyOnWriteArrayList<ValidationListener> listeners = new CopyOnWriteArrayList<>();

    private ValidationCenter() {}

    /** Listener interface (only start / stop). */
    public interface ValidationListener {
        void onStart();
        void onStop();
    }

    /**
     * Backwards-compatible setter:
     * - if l != null -> add it to the listener set (idempotent)
     * - if l == null -> clear all listeners (keeps old behaviour of clearing the single listener)
     */
    @Deprecated
    public static void setListener(ValidationListener l) {
        if (l == null) {
            listeners.clear();
        } else {
            addListener(l);
        }
    }

    /** Register a listener (idempotent). */
    public static void addListener(ValidationListener l) {
        if (l != null) listeners.addIfAbsent(l);
    }

    /** Unregister a listener. */
    public static void removeListener(ValidationListener l) {
        if (l != null) listeners.remove(l);
    }

    /** Fired by LauncherPanel or any trigger. */
    public static void notifyStart() {
        if (listeners.isEmpty()) return;
        SwingUtilities.invokeLater(() -> {
            for (ValidationListener l : listeners) {
                try {
                    l.onStart();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    /** Fired by LauncherPanel or any trigger. */
    public static void notifyStop() {
        if (listeners.isEmpty()) return;
        SwingUtilities.invokeLater(() -> {
            for (ValidationListener l : listeners) {
                try {
                    l.onStop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }
}
