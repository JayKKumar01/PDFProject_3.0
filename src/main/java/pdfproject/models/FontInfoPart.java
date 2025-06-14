package pdfproject.models;

import java.awt.Color;

public class FontInfoPart {
    private final String text;
    private final Color color;

    public FontInfoPart(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
