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

public class AddedFontInfoGuideGenerator {

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addHeader(doc, "User Guide: ADDED Font Differences");

        addAddedExample(doc,
                "Simple addition with single font/size/style",
                Arrays.asList(
                        colored("[ADDED] ", OperationColor.get(Operation.ADDED)),
                        black("[World]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("11", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE))
                )
        );

        addAddedExample(doc,
                "Mixed fonts",
                Arrays.asList(
                        colored("[ADDED] ", OperationColor.get(Operation.ADDED)),
                        black("[Wo]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("11", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[rld]: "),
                        colored("Times New Roman", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("11", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE))
                )
        );

        addAddedExample(doc,
                "Mixed sizes",
                Arrays.asList(
                        colored("[ADDED] ", OperationColor.get(Operation.ADDED)),
                        black("[Wor]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("10", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[ld]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE))
                )
        );

        addAddedExample(doc,
                "Mixed styles",
                Arrays.asList(
                        colored("[ADDED] ", OperationColor.get(Operation.ADDED)),
                        black("[Wo]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("11", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Italic", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[rld]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("11", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE))
                )
        );

        addAddedExample(doc,
                "Mixed font, size and style",
                Arrays.asList(
                        colored("[ADDED] ", OperationColor.get(Operation.ADDED)),
                        black("[W]: "),
                        colored("Verdana", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("10", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Italic", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[or]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[ld]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE))
                )
        );

        try (FileOutputStream out = new FileOutputStream("AddedFontInfoGuide.docx")) {
            doc.write(out);
            System.out.println("User guide generated: AddedFontInfoGuide.docx");
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

    private static void addAddedExample(XWPFDocument doc, String title, List<FontInfoPart> parts) {
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
