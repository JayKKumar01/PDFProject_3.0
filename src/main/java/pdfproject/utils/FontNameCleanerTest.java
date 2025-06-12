package pdfproject.utils;

public class FontNameCleanerTest {

    public static void main(String[] args) {
        testFont("ABCDE+TimesNewRomanMT-Bold");
        testFont("XYZ+ArialMT,Italic");
        testFont("HelveticaMT");
        testFont("Custom+Calibri-BoldItalic");
        testFont("CourierNewMT-Regular");
        testFont("SomeFont+Verdana-Bold");
        testFont("ABC+Tahoma");
        testFont(null);
        testFont("PQR+Georgia,Italic");
        testFont("DEF+Roboto-BoldMT");
    }

    private static void testFont(String rawFontName) {
        String cleaned = getCleanFontName(rawFontName);
        System.out.printf("Original: %-35s => Cleaned: %-15s%n", rawFontName, cleaned);
    }

    private static String getCleanFontName(String font) {
        if (font == null) return null;

        int plusIndex = font.indexOf('+');
        if (plusIndex != -1) font = font.substring(plusIndex + 1);

        int dashIndex = font.lastIndexOf('-');
        int commaIndex = font.lastIndexOf(',');

        int trimIndex = Math.max(dashIndex, commaIndex);
        if (trimIndex != -1) font = font.substring(0, trimIndex);

        return font.replace("mt", "").replace("MT", "").trim();
    }
}
