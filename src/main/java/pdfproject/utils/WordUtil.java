package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
import pdfproject.constants.Operation;
import pdfproject.models.WordInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordUtil {

    public static boolean isWordInfoSame(WordInfo wordInfo1, WordInfo wordInfo2) {
        List<TextPosition> positions1 = wordInfo1.getTextPositions();
        List<TextPosition> positions2 = wordInfo2.getTextPositions();

        if (positions1.size() != positions2.size()) return false;

        for (int i = 0; i < positions1.size(); i++) {
            if (!isFontInfoSame(positions1.get(i), positions2.get(i))) return false;
        }

        return true;
    }

    private static boolean isFontInfoSame(TextPosition tp1, TextPosition tp2) {
        if (tp1 == null || tp2 == null || tp1.getFont() == null || tp2.getFont() == null) return false;

        String font1 = tp1.getFont().getName();
        String font2 = tp2.getFont().getName();

        return Objects.equals(getCleanFontName(font1), getCleanFontName(font2))
                && Math.round(tp1.getFontSize()) == Math.round(tp2.getFontSize())
                && Objects.equals(getFontStyle(font1), getFontStyle(font2));
    }

    /**
     * Extracts a clean base font name from a raw PDFBox font name.
     */
    public static String getCleanFontName(String fontRawName) {
        if (fontRawName == null) return null;

        fontRawName = removeSubsetPrefix(fontRawName);

        int dashIndex = fontRawName.lastIndexOf('-');
        int commaIndex = fontRawName.lastIndexOf(',');
        int trimIndex = Math.max(dashIndex, commaIndex);
        if (trimIndex != -1) {
            fontRawName = fontRawName.substring(0, trimIndex);
        }

        fontRawName = fontRawName
                .replaceAll("(?i)boldmtbold", "") // rare edge case
                .replaceAll("(?i)mt", "")          // remove MT/mt
                .replaceAll("[^a-zA-Z0-9]", "")    // cleanup non-alphanumeric
                .trim();

        return fontRawName.isEmpty() ? null : fontRawName;
    }

    /**
     * Extracts the style part of a font name like Bold, Italic, etc.
     * Returns "regular" if no style detected.
     */
    public static String getFontStyle(String fontRawName) {
        if (fontRawName == null) return "regular";

        String lower = removeSubsetPrefix(fontRawName.toLowerCase());
        lower = lower.replace("mt", "");

        int dashIndex = lower.lastIndexOf('-');
        int commaIndex = lower.lastIndexOf(',');
        int styleIndex = Math.max(dashIndex, commaIndex);

        if (styleIndex != -1) {
            String style = lower.substring(styleIndex + 1).trim();
            if (!style.isEmpty()) return style;
        }

        return "regular";
    }

    /**
     * Removes PDF font subset prefix (e.g., 'ABCDE+FontName' → 'FontName')
     */
    private static String removeSubsetPrefix(String fontName) {
        int plusIndex = fontName.indexOf('+');
        return plusIndex != -1 ? fontName.substring(plusIndex + 1) : fontName;
    }
}
