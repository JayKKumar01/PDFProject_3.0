package pdfproject.window.components.body.right.color;

import pdfproject.constants.OperationColor;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Helper {

    // Color name to Color mapping (preserves order for dropdown)
    private static final Map<String, Color> colorMap = new LinkedHashMap<>();

    static {
        colorMap.put("red", Color.RED);
        colorMap.put("green", Color.GREEN);
        colorMap.put("blue", Color.BLUE);
        colorMap.put("yellow", Color.YELLOW);
        colorMap.put("orange", Color.ORANGE);
        colorMap.put("purple", new Color(128, 0, 128));
        colorMap.put("cyan", Color.CYAN);
        colorMap.put("magenta", Color.MAGENTA);
        colorMap.put("gray", Color.GRAY);
        colorMap.put("light gray", Color.LIGHT_GRAY);
        colorMap.put("white", Color.WHITE);
        colorMap.put("pink", Color.PINK);
        colorMap.put("brown", new Color(139, 69, 19));
        colorMap.put("teal", new Color(0, 128, 128));
        colorMap.put("navy", new Color(0, 0, 128));
        colorMap.put("maroon", new Color(128, 0, 0));
        colorMap.put("olive", new Color(128, 128, 0));
        colorMap.put("gold", new Color(255, 215, 0));
        colorMap.put("silver", new Color(192, 192, 192));
        colorMap.put("black", Color.BLACK);
    }

    // ─────────────────────────────────────────────────────────────
    // Color Utility Methods
    // ─────────────────────────────────────────────────────────────

    public static Color getColorFromName(String name) {
        if (name == null) return Color.BLACK;
        return colorMap.getOrDefault(name.toLowerCase(), Color.BLACK);
    }

    public static String[] getAllColorNames() {
        return colorMap.keySet().stream()
                .map(Helper::capitalize)
                .toArray(String[]::new);
    }

    public static Map<String, Color> getAllColorMap() {
        return colorMap;
    }

    private static String getColorNameFromMap(Color color) {
        if (color == null) return "unknown";

        for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return "unknown"; // Should not happen if color came from the map
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    // ─────────────────────────────────────────────────────────────
    // Operation Color Setter
    // ─────────────────────────────────────────────────────────────

    public static void setOperationColor(String operation, Color color) {
        if (operation == null || color == null) return;

        String colorName = getColorNameFromMap(color);

        switch (operation) {
            case "Deleted" -> OperationColor.DELETED = color;
            case "Added" -> OperationColor.ADDED = color;
            case "Font Name" -> OperationColor.FONT_NAME = color;
            case "Font Size" -> OperationColor.FONT_SIZE = color;
            case "Font Style" -> OperationColor.FONT_STYLE = color;
            case "Multiple" -> OperationColor.MULTIPLE = color;
            default -> {
                System.out.println("Unknown operation: " + operation);
                return;
            }
        }

        System.out.println("Set " + operation + " color to " + capitalize(colorName));
    }
}
