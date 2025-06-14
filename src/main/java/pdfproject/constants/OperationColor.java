package pdfproject.constants;

import java.awt.Color;

public final class OperationColor {
    private OperationColor() {}

    public static Color DELETED = Color.RED;
    public static Color ADDED = Color.GREEN;
    public static Color FONT_NAME = Color.MAGENTA;
    public static Color FONT_SIZE = Color.BLUE;
    public static Color FONT_STYLE = Color.CYAN;
    public static Color MULTIPLE = Color.BLACK;


    public static Color get(Operation operation) {
        if (operation == null) return MULTIPLE;
        switch (operation) {
            case FONT:
                return FONT_NAME;
            case SIZE:
                return FONT_SIZE;
            case STYLE:
                return FONT_STYLE;
            case ADDED:
                return ADDED;
            case DELETED:
                return DELETED;
            default:
                return MULTIPLE;
        }
    }

}
