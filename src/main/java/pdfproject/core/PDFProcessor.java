package pdfproject.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.parsers.RangeParser;
import pdfproject.utils.ImageUtils;
import pdfproject.utils.WordToPdfConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFProcessor {

    public MapModel processAll(List<InputData> dataList) {
        MapModel resultMap = new MapModel();
        int rowIndex = 0;

        for (InputData data : dataList) {
            try {
                processRow(data, rowIndex, resultMap);
            } catch (Exception e) {
                System.err.println("Error in row " + rowIndex + ": " + e.getMessage());
            }
            rowIndex++;
        }

        return resultMap;
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

            PDFRenderer renderer1 = new PDFRenderer(doc1);
            PDFRenderer renderer2 = new PDFRenderer(doc2);

            List<String> alignmentRow = new ArrayList<>();

            for (int i = 0; i < range1.size(); i++) {
                int p1 = range1.get(i) - 1;
                int p2 = range2.get(i) - 1;

                BufferedImage img1 = renderer1.renderImageWithDPI(p1, 200);
                BufferedImage img2 = renderer2.renderImageWithDPI(p2, 200);
                BufferedImage diff = ImageUtils.generateDiffImage(img1, img2);

                String[] paths = saveImages(rowIndex, i + 1, img1, img2, diff);
                alignmentRow.add(paths[0]); // img1 path
                alignmentRow.add(paths[1]); // img2 path
                alignmentRow.add(paths[2]); // diff path
            }

            resultMap.addAlignmentRow(alignmentRow);
        }
    }

    private File ensurePdf(String path) throws Exception {
        return path.toLowerCase().endsWith(".pdf") ? new File(path) : WordToPdfConverter.convertToPdf(path);
    }

    private String[] saveImages(int rowIndex, int pageNumber, BufferedImage img1, BufferedImage img2, BufferedImage diff) throws Exception {
        String dirPath = String.format("%s/item_%d/alignment/page_%d", Config.OUTPUT_IMAGES_PATH, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        File img1File = new File(dir, "img1.png");
        File img2File = new File(dir, "img2.png");
        File diffFile = new File(dir, "diff.png");

        ImageIO.write(img1, "png", img1File);
        ImageIO.write(img2, "png", img2File);
        ImageIO.write(diff, "png", diffFile);

        return new String[]{img1File.getPath(), img2File.getPath(), diffFile.getPath()};
    }
}
