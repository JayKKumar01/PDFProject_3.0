package pdfproject.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.constants.FileTypes;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.parsers.RangeParser;
import pdfproject.utils.WordToPdfConverter;
import pdfproject.validators.AlignmentValidator;
import pdfproject.validators.ContentValidator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class PDFProcessor {

    public static void processRow(InputData data, int itemIndex, MapModel resultMap) throws Exception {
        System.out.printf("▶️  Processing Item %d...%n", itemIndex + 1);

        File pdf1 = ensurePdf(data.getPath1());
        File pdf2 = ensurePdf(data.getPath2());

        try (PDDocument doc1 = PDDocument.load(pdf1);
             PDDocument doc2 = PDDocument.load(pdf2)) {

            int total1 = doc1.getNumberOfPages();
            int total2 = doc2.getNumberOfPages();

            List<Integer> range1 = RangeParser.parse(data.getRange1(), total1);
            List<Integer> range2 = RangeParser.parse(data.getRange2(), total2);

            int size1 = range1.size();
            int size2 = range2.size();
            int maxSize = Math.max(size1, size2);

            if (size1 != size2) {
                int diff = Math.abs(size1 - size2);
                System.out.printf("⚠️  [Item %d]: Document %d has %d extra page(s).%n",
                        itemIndex + 1, size1 > size2 ? 1 : 2, diff);
            }

            System.out.printf("📝 Item %d: Starting validation for %d page(s).%n", itemIndex + 1, maxSize);

            PDFRenderer renderer1 = new PDFRenderer(doc1);
            PDFRenderer renderer2 = new PDFRenderer(doc2);

            AlignmentValidator alignmentValidator = new AlignmentValidator(
                    Config.outputImagePath, itemIndex, renderer1, renderer2, resultMap
            );
            ContentValidator contentValidator = new ContentValidator(
                    data, Config.outputImagePath, itemIndex, doc1, doc2, resultMap
            );

            for (int i = 0; i < maxSize; i++) {
                int p1 = i < size1 ? range1.get(i) : -1;
                int p2 = i < size2 ? range2.get(i) : -1;

                System.out.printf("📄 Item %d | Page %d: Doc1 Page %s, Doc2 Page %s%n",
                        itemIndex + 1, i + 1,
                        p1 >= 0 ? String.valueOf(p1) : "N/A",
                        p2 >= 0 ? String.valueOf(p2) : "N/A"
                );

                try {
                    List<BufferedImage> images = alignmentValidator.validateAlignment(p1, p2, i + 1);
                    contentValidator.validateContent(p1, p2, i + 1, images);
                    System.out.printf("✅ Item %d | Page %d: Validation complete.%n", itemIndex + 1, i + 1);
                } catch (Exception e) {
                    System.err.printf("❌ Item %d | Page %d: Validation failed - %s%n",
                            itemIndex + 1, i + 1, e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.printf("✔️  Item %d: Validation for all pages completed.%n", itemIndex + 1);
        }
    }

    private static File ensurePdf(String path) throws Exception {
        return path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION)
                ? new File(path)
                : WordToPdfConverter.convertToPdf(path);
    }
}
