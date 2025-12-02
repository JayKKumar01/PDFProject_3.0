package pdfproject.window.constants;

import java.awt.*;

public final class ThemeColors {
    private ThemeColors() {}

    /* ----------------------------------------------------
     *  LIGHT THEME  (Current)
     * ---------------------------------------------------- */

    // Backgrounds
    public static final Color BACKGROUND = new Color(245, 247, 250);           // #F5F7FA
    public static final Color LAYOUT_BORDER = new Color(232, 240, 253);        // #E8F0FD
    public static final Color CONSOLE_BACKGROUND = new Color(224, 227, 231);   // #E0E3E7
    public static final Color CONSOLE_TEXT_BG = Color.WHITE;                   // #FFFFFF
    public static final Color CONSOLE_BORDER = new Color(200, 200, 200);       // #C8C8C8 scroll border

    // Primary / semantic
    public static final Color THEME_BLUE = new Color(25, 118, 210);            // #1976D2
    public static final Color THEME_BLUE_LIGHT = new Color(174, 215, 255);     // #AED7FF
    public static final Color THEME_GREEN = new Color(76, 175, 80);            // #4CAF50
    public static final Color THEME_RED = new Color(211, 47, 47);              // #D32F2F

    // Text
    public static final Color TEXT_STRONG = new Color(45, 55, 72);             // #2D3748
    public static final Color TEXT_MUTED = new Color(125, 125, 125);           // #7D7D7D
    public static final Color TEXT_MUTED_ALT = new Color(120, 120, 120);       // #787878

    // Toggle / switch
    public static final Color TRACK_LIGHT = new Color(220, 224, 230);          // #DCE0E6
    public static final Color TRACK_DARK = new Color(70, 78, 88);              // #464E58
    public static final Color KNOB_SHADOW = new Color(0, 0, 0, 55);



    /* ----------------------------------------------------
     *  DARK THEME  (New)
     *  Black + Green palette
     * ---------------------------------------------------- */

    // Dark Backgrounds
    public static final Color DARK_BACKGROUND = new Color(20, 20, 20);         // deep blackish
    public static final Color DARK_LAYOUT_BORDER = new Color(40, 70, 40);      // very dark green border

    // Text
    public static final Color DARK_TEXT_STRONG = new Color(230, 255, 230);     // soft green-white
    public static final Color DARK_TEXT_MUTED = new Color(150, 170, 150);      // muted green-gray
    public static final Color DARK_TEXT_MUTED_ALT = new Color(120, 140, 120);  // secondary muted green

    // Toggle Track (dark mode)
    public static final Color TRACK_DARKER = new Color(30, 30, 30);            // black track (start)

    // Toggle knob base color (dark mode)
    public static final Color KNOB_DARK = new Color(100, 150, 100);            // greenish knob start



    /* ----------------------------------------------------
     * Utility
     * ---------------------------------------------------- */

    public static Color fromHex(String hex) {
        return Color.decode(hex.startsWith("#") ? hex : ("#" + hex));
    }
}
