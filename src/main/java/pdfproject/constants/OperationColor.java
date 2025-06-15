package pdfproject.constants;

import java.awt.Color;

public final class OperationColor {
    private OperationColor() {}

    public static Color DELETED = Color.RED;
    public static Color ADDED = new Color(255,215,0);
    public static Color FONT_NAME = Color.MAGENTA;
    public static Color FONT_SIZE = Color.BLUE;
    public static Color FONT_STYLE = Color.CYAN;
    public static Color MULTIPLE = Color.BLACK;


    public static Color get(Operation operation) {
        if (operation == null) return MULTIPLE;
        return switch (operation) {
            case FONT -> FONT_NAME;
            case SIZE -> FONT_SIZE;
            case STYLE -> FONT_STYLE;
            case ADDED -> ADDED;
            case DELETED -> DELETED;
            default -> MULTIPLE;
        };
    }

}
