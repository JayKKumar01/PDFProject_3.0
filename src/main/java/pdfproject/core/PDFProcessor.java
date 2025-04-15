package pdfproject.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.parsers.RangeParser;
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
        File pdf1 = ensurePdf(data.getPath1());
        File pdf2 = ensurePdf(data.getPath2());

        try (PDDocument doc1 = PDDocument.load(pdf1);
             PDDocument doc2 = PDDocument.load(pdf2)) {

            int total1 = doc1.getNumberOfPages();
            int total2 = doc2.getNumberOfPages();

            List<Integer> range1 = RangeParser.parse(data.getRange1(), total1);
            List<Integer> range2 = RangeParser.parse(data.getRange2(), total2);

            if (range1.size() != range2.size())
                throw new IllegalArgumentException("Mismatch in page range counts.");

            PDFRenderer renderer1 = new PDFRenderer(doc1);
            PDFRenderer renderer2 = new PDFRenderer(doc2);

            AlignmentValidator alignmentValidator = new AlignmentValidator(data,outputImagePath,rowIndex,renderer1,renderer2,resultMap);
            ContentValidator contentValidator = new ContentValidator(data,outputImagePath,rowIndex,doc1,doc2,resultMap);



            for (int i = 0; i < range1.size(); i++) {
                int p1 = range1.get(i);
                int p2 = range2.get(i);
                List<BufferedImage> images = alignmentValidator.validateAlignment(p1, p2,i+1);
                contentValidator.validateContent(p1, p2,i+1,images);
            }



        }
    }

    private File ensurePdf(String path) throws Exception {
        return path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION) ? new File(path) : WordToPdfConverter.convertToPdf(path);
    }
}
