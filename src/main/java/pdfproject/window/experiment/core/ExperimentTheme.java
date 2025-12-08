package pdfproject.window.experiment.core;

import java.awt.Color;

/**
 * Single source of truth for theme + color tokens.
 * Provides only two themes: LIGHT and DARK.
 */
public final class ExperimentTheme {

    // -------------------------
    // Light theme colors
    // -------------------------
    public static final Color HEADER_BG_LIGHT       = new Color(0xF1F6FF);
    public static final Color HEADER_TEXT_LIGHT     = new Color(0x1E5AFF);
    public static final Color USERNAME_ACCENT_LIGHT = new Color(0x0A41C2);
    public static final Color BODY_BG_LIGHT         = new Color(0xFFFFFF);
    public static final Color BODY_TEXT_LIGHT       = new Color(0x222222);
    public static final Color CONSOLE_BG_LIGHT      = new Color(0xF7F7FB);
    public static final Color CONSOLE_TEXT_LIGHT    = new Color(0x0B6EFD);

    // -------------------------
    // Dark theme colors
    // -------------------------
    public static final Color HEADER_BG_DARK        = new Color(0x0F1720);
    public static final Color HEADER_TEXT_DARK      = new Color(0x7BE495);
    public static final Color USERNAME_ACCENT_DARK  = new Color(0x9BFFD1);
    public static final Color BODY_BG_DARK          = new Color(0x0B0E12);
    public static final Color BODY_TEXT_DARK        = new Color(0xD8EBD7);
    public static final Color CONSOLE_BG_DARK       = new Color(0x071018);
    public static final Color CONSOLE_TEXT_DARK     = new Color(0x9BE6C9);

    // -------------------------
    // Shared additional token
    // -------------------------
    public static final Color CONSOLE_ERROR         = new Color(0xFF3B30);

    // -------------------------
    // Instance fields
    // -------------------------
    public final Color headerBg;
    public final Color headerText;
    public final Color usernameAccent;
    public final Color bodyBg;
    public final Color bodyText;
    public final Color consoleBg;
    public final Color consoleText;

    private ExperimentTheme(Color headerBg, Color headerText, Color usernameAccent,
                            Color bodyBg, Color bodyText,
                            Color consoleBg, Color consoleText) {

        this.headerBg = headerBg;
        this.headerText = headerText;
        this.usernameAccent = usernameAccent;
        this.bodyBg = bodyBg;
        this.bodyText = bodyText;
        this.consoleBg = consoleBg;
        this.consoleText = consoleText;
    }

    // -------------------------
    // Predefined themes
    // -------------------------
    public static final ExperimentTheme LIGHT = new ExperimentTheme(
            HEADER_BG_LIGHT,
            HEADER_TEXT_LIGHT,
            USERNAME_ACCENT_LIGHT,
            BODY_BG_LIGHT,
            BODY_TEXT_LIGHT,
            CONSOLE_BG_LIGHT,
            CONSOLE_TEXT_LIGHT
    );

    public static final ExperimentTheme DARK = new ExperimentTheme(
            HEADER_BG_DARK,
            HEADER_TEXT_DARK,
            USERNAME_ACCENT_DARK,
            BODY_BG_DARK,
            BODY_TEXT_DARK,
            CONSOLE_BG_DARK,
            CONSOLE_TEXT_DARK
    );

    // -------------------------
    // Utilities
    // -------------------------
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
