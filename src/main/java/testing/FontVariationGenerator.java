package testing;

import org.apache.poi.xwpf.usermodel.*;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FontVariationGenerator {

    private static final List<String> FONT_STYLES = List.of("PLAIN", "BOLD", "ITALIC", "BOLD_ITALIC");
    private static final String NAME = "FontVariationTest2.docx";

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        // All system fonts
        String[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        List<String> fontNames = Arrays.asList(systemFonts);

        // Generate random sizes between 5 and 16
        Random rand = new Random();
        List<Double> fontSizes = new ArrayList<>();
        for (int i = 0; i < fontNames.size(); i++) {
            double size = 5 + rand.nextDouble() * 11;
            fontSizes.add(Math.round(size * 10.0) / 10.0);
        }

        int max = Math.max(Math.max(fontNames.size(), fontSizes.size()), FONT_STYLES.size());

        for (int i = 0; i < max; i++) {
            String font = fontNames.get(i % fontNames.size());
            double size = fontSizes.get(i % fontSizes.size());
            String style = FONT_STYLES.get(i % FONT_STYLES.size());

            addFontTest(doc, "Sample" + (i + 1), font, size, style);
        }

        try (FileOutputStream out = new FileOutputStream(NAME)) {
            doc.write(out);
            System.out.println("Generated: "+NAME);
        }
    }

    private static void addFontTest1(XWPFDocument doc, String word, String font, double size, String style) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(200); // let text flow nicely

        XWPFRun run = p.createRun();
        run.setText(word);
//        run.setText(word + " (Font: " + font + ", Size: " + size + ", Style: " + style + ")");
        run.setFontFamily(font);
        run.setFontSize((int) size);

        if (style.contains("BOLD")) run.setBold(true);
        if (style.contains("ITALIC")) run.setItalic(true);
    }
    private static void addFontTest(XWPFDocument doc, String word, String font, double size, String style) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(200); // let text flow nicely

        XWPFRun run = p.createRun();

        // Append metadata to word using delimiters, no space — stays as one word
        String metadataSuffix = "_f:" + font.replaceAll("\\s+", "") +
                "_s:" + ((int) size) +
                (style.contains("BOLD") ? "_b" : "") +
                (style.contains("ITALIC") ? "_i" : "");

        run.setText(word + metadataSuffix);  // Single word with embedded metadata

        run.setFontFamily(font);
        run.setFontSize((int) size);

        if (style.contains("BOLD")) run.setBold(true);
        if (style.contains("ITALIC")) run.setItalic(true);
    }

}
