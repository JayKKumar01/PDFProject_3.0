package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
import pdfproject.models.WordInfo;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for handling WordInfo and font-related comparisons and cleaning operations.
 */
public class WordUtil {

    // List of font modifiers to remove when cleaning font names
    private static final String[] FONT_MODIFIERS_TO_REMOVE = {
            "black", "narrow", "bold", "italic", "oblique", "light", "condensed", "compressed",
            "poster", "demi", "mt", "ps", "fb", "ui", "display", "text", "sc", "tc", "unicode",
            "collection", "historic", "emoji", "specialty", "symbol", "script", "sans", "serif"
    };

    /**
     * Compares two WordInfo objects to see if they have identical font attributes for all characters.
     *
     * @param wordInfo1 First WordInfo to compare
     * @param wordInfo2 Second WordInfo to compare
     * @return true if font info matches character-by-character; false otherwise
     */
    public static boolean isWordInfoSame(WordInfo wordInfo1, WordInfo wordInfo2) {
        List<TextPosition> positions1 = wordInfo1.getTextPositions();
        List<TextPosition> positions2 = wordInfo2.getTextPositions();

        if (positions1.size() != positions2.size()) return false;

        for (int i = 0; i < positions1.size(); i++) {
            if (!isFontInfoSame(positions1.get(i), positions2.get(i))) return false;
        }

        return true;
    }

    /**
     * Compares the font name, size (rounded), and style of two TextPositions.
     */
    private static boolean isFontInfoSame(TextPosition tp1, TextPosition tp2) {
        if (tp1 == null || tp2 == null || tp1.getFont() == null || tp2.getFont() == null) return false;

        return Objects.equals(getCleanFontName(tp1.getFont().getName()), getCleanFontName(tp2.getFont().getName()))
                && Math.round(tp1.getFontSize()) == Math.round(tp2.getFontSize())
                && Objects.equals(getFontStyle(tp1), getFontStyle(tp2));
    }

    /**
     * Extracts a clean base font name from a raw PDFBox font name.
     * Removes PDF subset prefixes and known modifier terms.
     *
     * @param fontRawName Raw font name (e.g., "ABCDEE+Arial-BoldItalic")
     * @return Cleaned font name (e.g., "Arial")
     */
    public static String getCleanFontName(String fontRawName) {
        if (fontRawName == null) return null;

        fontRawName = removeSubsetPrefix(fontRawName);

        // Trim any postfixes like "-Bold" or ",Italic"
        int dashIndex = fontRawName.lastIndexOf('-');
        int commaIndex = fontRawName.lastIndexOf(',');
        int trimIndex = Math.max(dashIndex, commaIndex);
        if (trimIndex != -1) {
            fontRawName = fontRawName.substring(0, trimIndex);
        }

        // Remove known modifier terms
        for (String mod : FONT_MODIFIERS_TO_REMOVE) {
            fontRawName = fontRawName.replaceAll("(?i)" + mod, "");
        }

        // Keep only alphanumeric characters
        fontRawName = fontRawName.replaceAll("[^a-zA-Z0-9]", "").trim();

        return fontRawName.isEmpty() ? null : fontRawName;
    }

    /**
     * Attempts to determine the font style (bold/italic) from the font name and descriptor.
     *
     * @param tp TextPosition with font info
     * @return Style string: "bold", "italic", "bold|italic", or "regular"
     */
    public static String getFontStyle(TextPosition tp) {
        if (tp == null || tp.getFont() == null) return "regular";

        String fontName = tp.getFont().getName().toLowerCase();
        String cleaned = removeSubsetPrefix(fontName);

        boolean nameBold = cleaned.contains("bold");
        boolean nameItalic = cleaned.contains("italic") || cleaned.contains("oblique");

        boolean descBold = false;
        boolean angleItalic = false;

        // Use descriptor if available
        try {
            var descriptor = tp.getFont().getFontDescriptor();
            if (descriptor != null) {
                descBold = descriptor.getFontWeight() >= 700;
                angleItalic = descriptor.getItalicAngle() <= -10;
            }
        } catch (Exception ignored) {}

        boolean isBold = nameBold || descBold;
        boolean isItalic = nameItalic || angleItalic;

        if (isBold && isItalic) return "bold|italic";
        if (isBold) return "bold";
        if (isItalic) return "italic";
        return "regular";
    }

    /**
     * Removes PDF font subset prefix (e.g., "ABCDE+Arial" becomes "Arial").
     */
    private static String removeSubsetPrefix(String fontName) {
        int plusIndex = fontName.indexOf('+');
        return plusIndex != -1 ? fontName.substring(plusIndex + 1) : fontName;
    }
}
