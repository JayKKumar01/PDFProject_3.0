package pdfproject.window.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Small global theme manager for the app.
 * Components can register as ThemeChangeListener to receive updates when theme changes.
 *
 * Note: This is intentionally minimal — extend as needed (e.g., persistence hooks).
 */
public final class ThemeManager {
    public interface ThemeChangeListener {
        /**
         * Called when theme changes. 'dark' == true means dark theme active.
         */
        void onThemeChanged(boolean dark);
    }

    private static final List<ThemeChangeListener> listeners = new CopyOnWriteArrayList<>();
    private static volatile boolean darkMode = false;

    private ThemeManager() {}

    public static boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Set global dark mode and notify listeners (if changed).
     */
    public static void setDarkMode(boolean dark) {
        boolean prev = darkMode;
        darkMode = dark;
        if (prev != dark) {
            for (ThemeChangeListener l : listeners) {
                try {
                    l.onThemeChanged(dark);
                } catch (Exception ex) {
                    // swallow to avoid affecting other listeners
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void register(ThemeChangeListener l) {
        if (l != null) listeners.add(l);
    }

    public static void unregister(ThemeChangeListener l) {
        listeners.remove(l);
    }
}
