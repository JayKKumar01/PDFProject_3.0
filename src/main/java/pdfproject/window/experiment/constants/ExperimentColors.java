package pdfproject.window.experiment.constants;

import java.awt.Color;

public final class ExperimentColors {

    // -------------------------
    // Light mode tokens
    // -------------------------

    // Header
    public static final Color HEADER_BG_LIGHT      = new Color(0xF1F6FF);
    public static final Color HEADER_TEXT_LIGHT    = new Color(0x1E5AFF);

    // Username accent (LIGHT)
    public static final Color USERNAME_ACCENT_LIGHT = new Color(0x0A41C2); // deeper blue

    // Body
    public static final Color BODY_BG_LIGHT        = new Color(0xFFFFFF);
    public static final Color BODY_TEXT_LIGHT      = new Color(0x222222);

    // Console
    public static final Color CONSOLE_BG_LIGHT     = new Color(0xF7F7FB);
    public static final Color CONSOLE_TEXT_LIGHT   = new Color(0x0B6EFD);


    // -------------------------
    // Dark mode tokens
    // -------------------------

    // Header
    public static final Color HEADER_BG_DARK       = new Color(0x0F1720);
    public static final Color HEADER_TEXT_DARK     = new Color(0x7BE495);

    // Username accent (DARK)
    public static final Color USERNAME_ACCENT_DARK = new Color(0x9BFFD1); // bright mint

    // Body
    public static final Color BODY_BG_DARK         = new Color(0x0B0E12);
    public static final Color BODY_TEXT_DARK       = new Color(0xD8EBD7);

    // Console
    public static final Color CONSOLE_BG_DARK      = new Color(0x071018);
    public static final Color CONSOLE_TEXT_DARK    = new Color(0x9BE6C9);

    // -------------------------
    // Extra fixed color
    // -------------------------

    public static final Color CONSOLE_ERROR        = new Color(0xFF3B30);

    private ExperimentColors() { }
}
