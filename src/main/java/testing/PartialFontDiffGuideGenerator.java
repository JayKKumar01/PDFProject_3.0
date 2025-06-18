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

public class PartialFontDiffGuideGenerator {

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addHeader(doc, "User Guide: Partial FONT/SIZE/STYLE Differences Within a Word");

        addDiffExample(doc, "Partial font change",
                Arrays.asList(
                        black("["),
                        colored("FONT", OperationColor.get(Operation.FONT)),
                        black("] "),
                        black("[He]: "), colored("same", Color.BLACK), black(", "),
                        black("[ll]: "), colored("Times", OperationColor.get(Operation.FONT)),
                        black("→"), colored("Arial", OperationColor.get(Operation.FONT)), black(", "),
                        black("[o]: "), colored("same", Color.BLACK), black(", ")
                )
        );

        addDiffExample(doc, "Partial size and style change",
                Arrays.asList(
                        black("["),
                        colored("SIZE", OperationColor.get(Operation.SIZE)), black(", "),
                        colored("STYLE", OperationColor.get(Operation.STYLE)),
                        black("] "),
                        black("[T]: "), colored("same", Color.BLACK), black(", "),
                        black("[es]: "),
                        colored("12", OperationColor.get(Operation.SIZE)), black("→"),
                        colored("14", OperationColor.get(Operation.SIZE)), black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE)), black("→"),
                        colored("Bold", OperationColor.get(Operation.STYLE)), black(", "),
                        black("[t]: "), colored("same", Color.BLACK), black(", ")
                )
        );

        try (FileOutputStream out = new FileOutputStream("PartialFontDiffGuide.docx")) {
            doc.write(out);
            System.out.println("User guide generated: PartialFontDiffGuide.docx");
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

        XWPFRun spacer = para.createRun();
        spacer.addBreak();
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
