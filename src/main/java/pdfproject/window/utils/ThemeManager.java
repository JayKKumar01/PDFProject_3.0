package pdfproject.window.utils;

import pdfproject.utils.AppSettings;
import pdfproject.window.core.Theme;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ThemeManager: central theme state. Notifies listeners on property "dark" when theme flips.
 * Persists choice using AppSettings on a background thread to avoid blocking EDT.
 */
public final class ThemeManager {
    private static final PropertyChangeSupport pcs = new PropertyChangeSupport(ThemeManager.class);
    private static final AtomicBoolean dark = new AtomicBoolean(false);
    private static volatile Theme currentTheme = Theme.LIGHT;

    // single-thread executor for persistence tasks
    private static final ExecutorService ioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "theme-persistence");
        t.setDaemon(true);
        return t;
    });

    // initialize from AppSettings (call once at startup)
    public static void initFromSettings(boolean defaultDark) {
        boolean loaded = AppSettings.loadTheme(defaultDark);
        dark.set(loaded);
        currentTheme = loaded ? Theme.DARK : Theme.LIGHT;
    }

    public static boolean isDarkMode() {
        return dark.get();
    }

    public static Theme getTheme() {
        return currentTheme;
    }

    /**
     * Set dark mode; no-op if same value. Notifies listeners on the calling thread,
     * but UI listeners should wrap UI updates in SwingUtilities.invokeLater for safety.
     */
    public static void setDarkMode(boolean value) {
        boolean old = dark.getAndSet(value);
        if (old == value) return; // avoid notifications if unchanged

        currentTheme = value ? Theme.DARK : Theme.LIGHT;

        // notify listeners (property name "dark" with old/new boolean)
        pcs.firePropertyChange("dark", old, value);

        // persist off-EDT
        ioExecutor.submit(() -> {
            try {
                AppSettings.saveTheme(value);
            } catch (Exception ignore) { /* don't block UI for persistence errors */ }
        });
    }

    public static void register(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public static void unregister(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    // call on app shutdown to stop executor (optional)
    public static void shutdown() {
        ioExecutor.shutdownNow();
    }
}
