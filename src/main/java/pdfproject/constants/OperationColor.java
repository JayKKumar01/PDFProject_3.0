package pdfproject.constants;

import java.awt.Color;

public final class OperationColor {

    private OperationColor() {}

    // Default immutable colors
    public static final Color DEF_DELETED    = Color.RED;
    public static final Color DEF_ADDED      = new Color(255, 215, 0);
    public static final Color DEF_FONT_NAME  = Color.MAGENTA;
    public static final Color DEF_FONT_SIZE  = Color.BLUE;
    public static final Color DEF_FONT_STYLE = Color.CYAN;
    public static final Color DEF_MULTIPLE   = Color.BLACK;

    // Mutable current colors (start with defaults)
    public static Color DELETED    = DEF_DELETED;
    public static Color ADDED      = DEF_ADDED;
    public static Color FONT_NAME  = DEF_FONT_NAME;
    public static Color FONT_SIZE  = DEF_FONT_SIZE;
    public static Color FONT_STYLE = DEF_FONT_STYLE;
    public static Color MULTIPLE   = DEF_MULTIPLE;

    public static Color get(Operation operation) {
        if (operation == null) return MULTIPLE;
        return switch (operation) {
            case FONT   -> FONT_NAME;
            case SIZE   -> FONT_SIZE;
            case STYLE  -> FONT_STYLE;
            case ADDED  -> ADDED;
            case DELETED-> DELETED;
            default     -> MULTIPLE;
        };
    }
}
