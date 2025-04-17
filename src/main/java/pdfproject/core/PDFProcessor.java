package pdfproject.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.parsers.RangeParser;
import pdfproject.utils.DataMapGenerator;
import pdfproject.utils.ImageUtils;
import pdfproject.utils.WordToPdfConverter;
import pdfproject.validators.AlignmentValidator;
import pdfproject.validators.ContentValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFProcessor {
    private String outputImagePath;

    // 🔁 Updated: return a list of MapModel (one per row)
    public List<MapModel> processAll(List<InputData> dataList) {
        outputImagePath = AppPaths.OUTPUT_IMAGES_BASE + "\\Result - "+System.currentTimeMillis();
        List<MapModel> resultList = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
            InputData data = dataList.get(rowIndex);
            MapModel resultMap = new MapModel(outputImagePath);

            try {
                processRow(data, rowIndex, resultMap);
                resultList.add(resultMap);
            } catch (Exception e) {
                System.err.println("Error in item " + (rowIndex + 1) + ": " + e.getMessage());
                resultList.add(resultMap); // Optional: still add empty MapModel to keep indexing
            }
        }

        DataMapGenerator.generateDataMapJs(resultList,outputImagePath);

        return resultList;
    }

    private void processRow(InputData data, int itemIndex, MapModel resultMap) throws Exception {
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

            if (size1 != size2) {
                int minSize = Math.min(size1, size2);

                if (size1 > minSize) {
                    System.out.printf("⚠️  [Item %d]: Skipping %d extra page(s) in Document 1.%n", itemIndex + 1, size1 - minSize);
                    range1 = range1.subList(0, minSize);
                }

                if (size2 > minSize) {
                    System.out.printf("⚠️  [Item %d]: Skipping %d extra page(s) in Document 2.%n", itemIndex + 1, size2 - minSize);
                    range2 = range2.subList(0, minSize);
                }
            }

            PDFRenderer renderer1 = new PDFRenderer(doc1);
            PDFRenderer renderer2 = new PDFRenderer(doc2);

            AlignmentValidator alignmentValidator = new AlignmentValidator(data, outputImagePath, itemIndex, renderer1, renderer2, resultMap);
            ContentValidator contentValidator = new ContentValidator(data, outputImagePath, itemIndex, doc1, doc2, resultMap);

            System.out.printf("📝 Item %d: Starting validation for %d page(s).%n", itemIndex + 1, range1.size());

            for (int i = 0; i < range1.size(); i++) {
                int p1 = range1.get(i);
                int p2 = range2.get(i);

                System.out.printf("📄 Item %d | Page %d: Doc1 Page %d, Doc2 Page %d%n", itemIndex + 1, i + 1, p1 + 1, p2 + 1);

                // Alignment validation
                System.out.printf("🔍 Item %d | Page %d: Validating alignment...%n", itemIndex + 1, i + 1);
                List<BufferedImage> images = alignmentValidator.validateAlignment(p1, p2, i + 1);
                System.out.printf("✅ Item %d | Page %d: Alignment validation complete.%n", itemIndex + 1, i + 1);

                // Content validation
                System.out.printf("🧠 Item %d | Page %d: Validating content...%n", itemIndex + 1, i + 1);
                contentValidator.validateContent(p1, p2, i + 1, images);
                System.out.printf("✅ Item %d | Page %d: Content validation complete.%n", itemIndex + 1, i + 1);
            }

            System.out.printf("✔️  Item %d: Validation for all pages completed.%n", itemIndex + 1);
        }
    }


    private File ensurePdf(String path) throws Exception {
        return path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION) ? new File(path) : WordToPdfConverter.convertToPdf(path);
    }
}
