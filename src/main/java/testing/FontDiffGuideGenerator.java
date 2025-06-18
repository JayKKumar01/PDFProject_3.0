package testing;

import org.apache.poi.xwpf.usermodel.*;
import pdfproject.constants.Operation;
import pdfproject.constants.OperationColor;
import pdfproject.models.FontInfoPart;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FontDiffGuideGenerator {

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addHeader(doc, "User Guide: FONT, SIZE, and STYLE Differences");

        // Font difference
        addDiffExample(doc, "Font change",
                Arrays.asList(
                        black("["),
                        colored("FONT", OperationColor.get(Operation.FONT)),
                        black("] "),
                        black("[World]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("→"),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black(", ")
                )
        );

        // Size difference
        addDiffExample(doc, "Size change",
                Arrays.asList(
                        black("["),
                        colored("SIZE", OperationColor.get(Operation.SIZE)),
                        black("] "),
                        black("[World]: "),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("→"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black(", ")
                )
        );

        // Style difference
        addDiffExample(doc, "Style change",
                Arrays.asList(
                        black("["),
                        colored("STYLE", OperationColor.get(Operation.STYLE)),
                        black("] "),
                        black("[World]: "),
                        colored("Regular", OperationColor.get(Operation.STYLE)),
                        black("→"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", ")
                )
        );

        // Font + Size
        addDiffExample(doc, "Font + Size change",
                Arrays.asList(
                        black("["),
                        colored("FONT", OperationColor.get(Operation.FONT)),
                        black(", "),
                        colored("SIZE", OperationColor.get(Operation.SIZE)),
                        black("] "),
                        black("[World]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("→"),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("→"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black(", ")
                )
        );

        // Font + Style
        addDiffExample(doc, "Font + Style change",
                Arrays.asList(
                        black("["),
                        colored("FONT", OperationColor.get(Operation.FONT)),
                        black(", "),
                        colored("STYLE", OperationColor.get(Operation.STYLE)),
                        black("] "),
                        black("[World]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("→"),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE)),
                        black("→"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", ")
                )
        );

        // Size + Style
        addDiffExample(doc, "Size + Style change",
                Arrays.asList(
                        black("["),
                        colored("SIZE", OperationColor.get(Operation.SIZE)),
                        black(", "),
                        colored("STYLE", OperationColor.get(Operation.STYLE)),
                        black("] "),
                        black("[World]: "),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("→"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE)),
                        black("→"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", ")
                )
        );

        // Font + Size + Style
        addDiffExample(doc, "Font + Size + Style change",
                Arrays.asList(
                        black("["),
                        colored("FONT", OperationColor.get(Operation.FONT)),
                        black(", "),
                        colored("SIZE", OperationColor.get(Operation.SIZE)),
                        black(", "),
                        colored("STYLE", OperationColor.get(Operation.STYLE)),
                        black("] "),
                        black("[World]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("→"),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("→"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE)),
                        black("→"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", ")
                )
        );

        // Save to file
        try (FileOutputStream out = new FileOutputStream("FontDiffGuide.docx")) {
            doc.write(out);
            System.out.println("User guide generated: FontDiffGuide.docx");
        }
    }

    private static void addHeader(XWPFDocument doc, String title) {
        XWPFParagraph header = doc.createParagraph();
        header.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = header.createRun();
        run.setText(title);
        run.setBold(true);
        run.setFontSize(16);
        run.addBreak();
    }

    private static void addDiffExample(XWPFDocument doc, String title, List<FontInfoPart> parts) {
        XWPFParagraph para = doc.createParagraph();
        XWPFRun titleRun = para.createRun();
        titleRun.setText("• " + title);
        titleRun.setBold(true);
        titleRun.addBreak();

        for (FontInfoPart part : parts) {
            XWPFRun run = para.createRun();
            run.setText(part.text());
            run.setColor(toHex(part.color()));
        }

        para.createRun().addBreak();
    }

    private static FontInfoPart colored(String text, Color color) {
        return new FontInfoPart(text, color);
    }

    private static FontInfoPart black(String text) {
        return new FontInfoPart(text, Color.BLACK);
    }

    private static String toHex(Color color) {
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
