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

public class FontDiffUserGuideGenerator {

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addHeader(doc, "User Guide: DELETED Font Differences");

        addDeletedExample(doc,
                "Simple deletion with single font/size/style",
                Arrays.asList(
                        colored("[DELETED] ", OperationColor.get(Operation.DELETED)),
                        black("[Hello]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE))
                )
        );

        addDeletedExample(doc,
                "Mixed fonts",
                Arrays.asList(
                        colored("[DELETED] ", OperationColor.get(Operation.DELETED)),
                        black("[He]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[llo]: "),
                        colored("Times New Roman", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE))
                )
        );

        addDeletedExample(doc,
                "Mixed sizes",
                Arrays.asList(
                        colored("[DELETED] ", OperationColor.get(Operation.DELETED)),
                        black("[He]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("10", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[llo]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE))
                )
        );

        addDeletedExample(doc,
                "Mixed styles",
                Arrays.asList(
                        colored("[DELETED] ", OperationColor.get(Operation.DELETED)),
                        black("[He]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Italic", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[llo]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE))
                )
        );

        addDeletedExample(doc,
                "Mixed font, size and style",
                Arrays.asList(
                        colored("[DELETED] ", OperationColor.get(Operation.DELETED)),
                        black("[He]: "),
                        colored("Arial", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("10", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Italic", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[l]: "),
                        colored("Verdana", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("12", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Bold", OperationColor.get(Operation.STYLE)),
                        black(", "),
                        black("[lo]: "),
                        colored("Calibri", OperationColor.get(Operation.FONT)),
                        black("/"),
                        colored("14", OperationColor.get(Operation.SIZE)),
                        black("/"),
                        colored("Regular", OperationColor.get(Operation.STYLE))
                )
        );

        try (FileOutputStream out = new FileOutputStream("DeletedFontInfoGuide.docx")) {
            doc.write(out);
            System.out.println("User guide generated: DeletedFontInfoGuide.docx");
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

    private static void addDeletedExample(XWPFDocument doc, String title, List<FontInfoPart> parts) {
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
