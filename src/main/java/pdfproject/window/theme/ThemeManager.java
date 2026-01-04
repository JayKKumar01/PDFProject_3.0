package pdfproject.window.theme;

import java.awt.*;

public final class ThemeManager {

    private ThemeManager() {}

    // Backgrounds
    public static final Color HEADER_BG  = new Color(0x121820);
    public static final Color CONTENT_BG = new Color(0x161E27);
    public static final Color CONSOLE_BG = new Color(0x0D1117);

    // Text
    public static final Color HEADER_TEXT  = new Color(0xE6EDF3);
    public static final Color CONTENT_TEXT = new Color(0xB6C2CF);
    public static final Color CONSOLE_TEXT = new Color(0x9BE6C9);

    // ===== Accents =====
    public static final Color ACCENT_PRIMARY = new Color(0x7BE495); // main green
    public static final Color ACCENT_SOFT    = new Color(0x9BFFD1); // lighter green

    public static final Color CONSOLE_ERROR          = new Color(0xFF3B30);

    // Toggle
    public static final Color TOGGLE_OFF_BG = new Color(0x2A3441);
    public static final Color TOGGLE_ON_BG  = ACCENT_PRIMARY;
    public static final Color TOGGLE_KNOB   = new Color(0xE6EDF3);

    // User highlight
    public static final Color USERNAME_HIGHLIGHT = new Color(0xF6A04D);


}
