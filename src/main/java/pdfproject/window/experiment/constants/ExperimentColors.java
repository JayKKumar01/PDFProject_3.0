package pdfproject.window.experiment.constants;

import java.awt.Color;

/**
 * Experiment color tokens (flat constants).
 *
 * Naming convention:
 *   <AREA>_<ROLE>_<MODE>
 *
 * 12 base tokens:
 *   Header (bg/text, light/dark) = 4
 *   Body   (bg/text, light/dark) = 4
 *   Console(bg/text, light/dark) = 4
 *
 * Additional extra tokens:
 *   CONSOLE_ERROR_COLOR (always red, independent of theme)
 */
public final class ExperimentColors {

    // -------------------------
    // Light mode tokens
    // -------------------------

    // Header
    public static final Color HEADER_BG_LIGHT   = new Color(0xF1F6FF);  // light bluish
    public static final Color HEADER_TEXT_LIGHT = new Color(0x1E5AFF);  // blue

    // Body
    public static final Color BODY_BG_LIGHT     = new Color(0xFFFFFF);  // white
    public static final Color BODY_TEXT_LIGHT   = new Color(0x222222);  // dark gray

    // Console
    public static final Color CONSOLE_BG_LIGHT   = new Color(0xF7F7FB); // very light
    public static final Color CONSOLE_TEXT_LIGHT = new Color(0x0B6EFD); // console blue


    // -------------------------
    // Dark mode tokens
    // -------------------------

    // Header
    public static final Color HEADER_BG_DARK   = new Color(0x0F1720);   // very dark
    public static final Color HEADER_TEXT_DARK = new Color(0x7BE495);   // green-ish

    // Body
    public static final Color BODY_BG_DARK     = new Color(0x0B0E12);   // near-black
    public static final Color BODY_TEXT_DARK   = new Color(0xD8EBD7);   // light green-ish

    // Console
    public static final Color CONSOLE_BG_DARK   = new Color(0x071018);  // dark teal
    public static final Color CONSOLE_TEXT_DARK = new Color(0x9BE6C9);  // muted green


    // -------------------------
    // Extra Colors (Fixed)
    // -------------------------

    /** Error text in console — NOT theme dependent */
    public static final Color CONSOLE_ERROR = new Color(0xFF3B30); // Apple red


    private ExperimentColors() { }
}
