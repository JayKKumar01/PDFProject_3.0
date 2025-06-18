package testing;

import org.apache.poi.xwpf.usermodel.*;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FontInfoUserGuideGenerator {

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addTitle(doc, "Font Info Report: User Guide");

        addSection(doc, "1. DELETED Example",
                new String[]{"[DELETED] This word was removed"},
                new Color[]{Color.RED});

        addSection(doc, "2. ADDED Example",
                new String[]{"[ADDED] This new word was added"},
                new Color[]{new Color(255, 215, 0)}); // Gold

        addSection(doc, "3. FONT Info Example",
                new String[]{
                        "[FONT] ",
                        "[Test]: ",
                        "Arial", "/", "12", "/", "Plain"
                },
                new Color[]{
                        Color.MAGENTA,
                        Color.BLACK,
                        Color.MAGENTA, Color.BLACK, Color.BLUE, Color.BLACK, Color.CYAN
                });

        addSection(doc, "4. SIZE Info Example",
                new String[]{
                        "[SIZE] ",
                        "[Zoom]: ",
                        "TimesNewRoman", "/", "11", "/", "Bold"
                },
                new Color[]{
                        Color.BLUE,
                        Color.BLACK,
                        Color.MAGENTA, Color.BLACK, Color.BLUE, Color.BLACK, Color.CYAN
                });

        addSection(doc, "5. STYLE Info Example",
                new String[]{
                        "[STYLE] ",
                        "[Focus]: ",
                        "Verdana", "/", "10", "/", "Italic"
                },
                new Color[]{
                        Color.CYAN,
                        Color.BLACK,
                        Color.MAGENTA, Color.BLACK, Color.BLUE, Color.BLACK, Color.CYAN
                });

        addSection(doc, "6. Mixed Difference Example",
                new String[]{
                        "[FONT, SIZE, STYLE] ",
                        "[Doc]: ",
                        "Arial", "→", "TimesNewRoman", "/", "12", "→", "14", "/", "Plain", "→", "BoldItalic"
                },
                new Color[]{
                        Color.BLACK,
                        Color.BLACK,
                        Color.MAGENTA, Color.BLACK, Color.MAGENTA, Color.BLACK,
                        Color.BLUE, Color.BLACK, Color.BLUE, Color.BLACK,
                        Color.CYAN, Color.BLACK, Color.CYAN
                });

        try (FileOutputStream out = new FileOutputStream("FontInfoUserGuide.docx")) {
            doc.write(out);
        }

        System.out.println("FontInfoUserGuide.docx generated successfully.");
    }

    private static void addTitle(XWPFDocument doc, String title) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setText(title);
        r.setFontSize(16);
        r.setBold(true);
    }

    private static void addSection(XWPFDocument doc, String heading, String[] texts, Color[] colors) {
        XWPFParagraph headingPara = doc.createParagraph();
        headingPara.setSpacingBefore(300);
        XWPFRun headingRun = headingPara.createRun();
        headingRun.setText(heading);
        headingRun.setFontSize(13);
        headingRun.setBold(true);

        XWPFParagraph bodyPara = doc.createParagraph();
        XWPFRun run = bodyPara.createRun();
        run.setFontSize(11);

        for (int i = 0; i < texts.length; i++) {
            XWPFRun part = bodyPara.createRun();
            part.setFontSize(11);
            part.setText(texts[i]);
            part.setColor(toHex(colors[i]));
        }
    }

    private static String toHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
