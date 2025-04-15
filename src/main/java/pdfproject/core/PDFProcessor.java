package pdfproject.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.parsers.RangeParser;
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
        outputImagePath = AppPaths.OUTPUT_IMAGES_BASE + "/Result - "+System.currentTimeMillis();
        List<MapModel> resultList = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
            InputData data = dataList.get(rowIndex);
            MapModel resultMap = new MapModel();

            try {
                processRow(data, rowIndex, resultMap);
                resultList.add(resultMap);
            } catch (Exception e) {
                System.err.println("Error in row " + rowIndex + ": " + e.getMessage());
                resultList.add(resultMap); // Optional: still add empty MapModel to keep indexing
            }
        }

        return resultList;
    }

    private void processRow(InputData data, int rowIndex, MapModel resultMap) throws Exception {
        File pdf1 = ensurePdf(data.path1);
        File pdf2 = ensurePdf(data.path2);

        try (PDDocument doc1 = PDDocument.load(pdf1);
             PDDocument doc2 = PDDocument.load(pdf2)) {

            int total1 = doc1.getNumberOfPages();
            int total2 = doc2.getNumberOfPages();

            List<Integer> range1 = RangeParser.parse(data.range1, total1);
            List<Integer> range2 = RangeParser.parse(data.range2, total2);

            if (range1.size() != range2.size())
                throw new IllegalArgumentException("Mismatch in page range counts.");

            // Perform alignment validation
            AlignmentValidator alignmentValidator = new AlignmentValidator(outputImagePath);
            alignmentValidator.validateAlignment(doc1, doc2, range1, range2, rowIndex,resultMap);

            // Perform content validation
            ContentValidator contentValidator = new ContentValidator(outputImagePath);
            contentValidator.validateContent(doc1, doc2, range1, range2,rowIndex,resultMap);



        }
    }

    private File ensurePdf(String path) throws Exception {
        return path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION) ? new File(path) : WordToPdfConverter.convertToPdf(path);
    }
}
