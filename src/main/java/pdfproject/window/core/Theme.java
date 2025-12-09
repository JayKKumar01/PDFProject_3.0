package pdfproject.window.core;

import java.awt.Color;

/**
 * Single source of truth for theme + color tokens.
 * Minimal and explicit: only LIGHT and DARK instances are provided.
 */
public final class Theme {

    // -------------------------
    // Light theme tokens
    // -------------------------
    public static final Color HEADER_BG_LIGHT        = new Color(0xF1F6FF);
    public static final Color HEADER_TEXT_LIGHT      = new Color(0x1E5AFF);
    public static final Color USERNAME_ACCENT_LIGHT  = new Color(0x0A41C2);
    public static final Color BODY_BG_LIGHT          = new Color(0xFFFFFF);
    public static final Color BODY_TEXT_LIGHT        = new Color(0x1B3A73);
    public static final Color CONSOLE_BG_LIGHT       = new Color(0xF7F7FB);
    public static final Color CONSOLE_TEXT_LIGHT     = new Color(0x0B6EFD);

    // new start/stop button colors (light)
    public static final Color START_BUTTON_LIGHT     = new Color(0x0A84FF); // accent-ish blue
    public static final Color STOP_BUTTON_LIGHT      = new Color(0xFF3B30); // red/error

    // -------------------------
    // Dark theme tokens
    // -------------------------
    public static final Color HEADER_BG_DARK         = new Color(0x0F1720);
    public static final Color HEADER_TEXT_DARK       = new Color(0x7BE495);
    public static final Color USERNAME_ACCENT_DARK   = new Color(0x9BFFD1);
    public static final Color BODY_BG_DARK           = new Color(0x0B0E12);
    public static final Color BODY_TEXT_DARK         = new Color(0xD8EBD7);
    public static final Color CONSOLE_BG_DARK        = new Color(0x071018);
    public static final Color CONSOLE_TEXT_DARK      = new Color(0x9BE6C9);

    // new start/stop button colors (dark)
    public static final Color START_BUTTON_DARK      = new Color(0x7BE495);
    public static final Color STOP_BUTTON_DARK       = new Color(0xFF3B30);

    // -------------------------
    // Shared tokens
    // -------------------------
    public static final Color CONSOLE_ERROR          = new Color(0xFF3B30);

    // -------------------------
    // Slider (toggle) tokens grouped for clarity
    // Use ExperimentTheme.Slider.* to access.
    // -------------------------
    public static final class Slider {
        // Light
        public static final Color TRACK_ON_LIGHT    = new Color(0x4F7DFF);
        public static final Color TRACK_OFF_LIGHT   = new Color(0xDCE6FF);
        public static final Color KNOB_FILL_LIGHT   = Color.WHITE;
        public static final Color KNOB_BORDER_LIGHT = new Color(0xD0D6E8);

        // Dark
        public static final Color TRACK_ON_DARK     = new Color(0x6FEABB);
        public static final Color TRACK_OFF_DARK    = new Color(0x21313B);
        public static final Color KNOB_FILL_DARK    = new Color(0x0C1218);
        public static final Color KNOB_BORDER_DARK  = new Color(0x2B463E);

        private Slider() {}
    }

    // -------------------------
    // Lightweight instance view (keeps existing usage)
    // -------------------------
    public final Color headerBg;
    public final Color headerText;
    public final Color usernameAccent;
    public final Color bodyBg;
    public final Color bodyText;
    public final Color consoleBg;
    public final Color consoleText;

    // new per-theme button colors
    public final Color startButtonColor;
    public final Color stopButtonColor;

    private Theme(Color headerBg, Color headerText, Color usernameAccent,
                  Color bodyBg, Color bodyText,
                  Color consoleBg, Color consoleText,
                  Color startButtonColor, Color stopButtonColor) {
        this.headerBg = headerBg;
        this.headerText = headerText;
        this.usernameAccent = usernameAccent;
        this.bodyBg = bodyBg;
        this.bodyText = bodyText;
        this.consoleBg = consoleBg;
        this.consoleText = consoleText;
        this.startButtonColor = startButtonColor;
        this.stopButtonColor = stopButtonColor;
    }

    public static final Theme LIGHT = new Theme(
            HEADER_BG_LIGHT, HEADER_TEXT_LIGHT, USERNAME_ACCENT_LIGHT,
            BODY_BG_LIGHT, BODY_TEXT_LIGHT,
            CONSOLE_BG_LIGHT, CONSOLE_TEXT_LIGHT,
            START_BUTTON_LIGHT, STOP_BUTTON_LIGHT
    );

    public static final Theme DARK = new Theme(
            HEADER_BG_DARK, HEADER_TEXT_DARK, USERNAME_ACCENT_DARK,
            BODY_BG_DARK, BODY_TEXT_DARK,
            CONSOLE_BG_DARK, CONSOLE_TEXT_DARK,
            START_BUTTON_DARK, STOP_BUTTON_DARK
    );

    // -------------------------
    // Utilities
    // -------------------------
    /**
     * Return an accessible foreground color (white or near-black) for a background.
     */
    public static Color readableForeground(Color bg) {
        if (bg == null) return Color.BLACK;
        double lum = 0.2126 * linear(bg.getRed())
                + 0.7152 * linear(bg.getGreen())
                + 0.0722 * linear(bg.getBlue());
        return lum < 0.5 ? Color.WHITE : new Color(0x0D0D0D);
    }

    private static double linear(int channel) {
        double c = channel / 255.0;
        return (c <= 0.03928) ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}
