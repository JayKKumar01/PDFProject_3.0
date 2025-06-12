package pdfproject.window.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class FontDiffDocxGenerator {

    public static void main(String[] args) throws IOException {
        generateVersion1(); // Original version
        generateVersion2(); // Modified version with differences
    }

    private static void generateVersion1() throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addRun(doc, "FontDiff: Arial vs Times New Roman", "Arial", 12, false, false);
        addRun(doc, "SizeDiff: 12pt vs 16pt", "Calibri", 12, false, false);
        addRun(doc, "StyleDiff: Regular vs Bold", "Calibri", 12, false, false);
        addRun(doc, "PartialFontDiff: ABC", "Courier New", 12, false, false); // All Courier
        addRun(doc, "PartialSizeDiff: 123", "Calibri", 12, false, false); // All 12pt
        addRun(doc, "PartialStyleDiff: xyz", "Calibri", 12, false, false); // All regular
        addRun(doc, "ToBeDeleted: This line will not be in version 2", "Calibri", 12, false, false);

        try (FileOutputStream out = new FileOutputStream("FontDiff_V1.docx")) {
            doc.write(out);
            System.out.println("Generated FontDiff_V1.docx");
        }
    }

    private static void generateVersion2() throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addRun(doc, "FontDiff: Arial vs Times New Roman", "Times New Roman", 12, false, false);
        addRun(doc, "SizeDiff: 12pt vs 16pt", "Calibri", 16, false, false);
        addRun(doc, "StyleDiff: Regular vs Bold", "Calibri", 12, true, false);
        addRun(doc, "PartialFontDiff: ", "Courier New", 12, false, false, "AB");
        addRun(doc, "PartialFontDiff: ", "Times New Roman", 12, false, false, "C");
        addRun(doc, "PartialSizeDiff: ", "Calibri", 12, false, false, "12");
        addRun(doc, "PartialSizeDiff: ", "Calibri", 16, false, false, "3");
        addRun(doc, "PartialStyleDiff: ", "Calibri", 12, false, false, "xy");
        addRun(doc, "PartialStyleDiff: ", "Calibri", 12, true, false, "z");
        addRun(doc, "NewlyAdded: This line was not in version 1", "Calibri", 12, false, false);

        try (FileOutputStream out = new FileOutputStream("FontDiff_V2.docx")) {
            doc.write(out);
            System.out.println("Generated FontDiff_V2.docx");
        }
    }

    private static void addRun(XWPFDocument doc, String label, String font, int size, boolean bold, boolean italic) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(label);
        run.setFontFamily(font);
        run.setFontSize(size);
        run.setBold(bold);
        run.setItalic(italic);
    }

    private static void addRun(XWPFDocument doc, String label, String font, int size, boolean bold, boolean italic, String customText) {
        XWPFParagraph p = doc.createParagraph();
        if (!label.isEmpty()) {
            XWPFRun labelRun = p.createRun();
            labelRun.setText(label);
            labelRun.setFontFamily("Calibri");
            labelRun.setFontSize(11);
            labelRun.setItalic(true);
        }
        XWPFRun run = p.createRun();
        run.setText(customText);
        run.setFontFamily(font);
        run.setFontSize(size);
        run.setBold(bold);
        run.setItalic(italic);
    }
}
