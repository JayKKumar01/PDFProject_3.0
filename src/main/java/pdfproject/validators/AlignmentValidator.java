package pdfproject.validators;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlignmentValidator {

    public List<String> validateAlignment(PDDocument doc1, PDDocument doc2, List<Integer> range1, List<Integer> range2, int rowIndex) throws Exception {
        PDFRenderer renderer1 = new PDFRenderer(doc1);
        PDFRenderer renderer2 = new PDFRenderer(doc2);

        List<String> alignmentRow = new ArrayList<>();

        for (int i = 0; i < range1.size(); i++) {
            int p1 = range1.get(i) - 1;
            int p2 = range2.get(i) - 1;

            BufferedImage img1 = renderer1.renderImageWithDPI(p1, Config.RENDER_DPI);
            BufferedImage img2 = renderer2.renderImageWithDPI(p2, Config.RENDER_DPI);
            BufferedImage diff = ImageUtils.generateDiffImage(img1, img2);

            String[] paths = saveImages(rowIndex, i + 1, img1, img2, diff);
            alignmentRow.add(paths[0]); // img1 path
            alignmentRow.add(paths[1]); // img2 path
            alignmentRow.add(paths[2]); // diff path
        }

        return alignmentRow;
    }

    private String[] saveImages(int rowIndex, int pageNumber, BufferedImage img1, BufferedImage img2, BufferedImage diff) throws Exception {
        String dirPath = String.format("%s/item_%d/alignment/page_%d", AppPaths.OUTPUT_IMAGES_BASE, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        File img1File = new File(dir, "img1" + FileTypes.PNG_EXTENSION);
        File img2File = new File(dir, "img2" + FileTypes.PNG_EXTENSION);
        File diffFile = new File(dir, "diff" + FileTypes.PNG_EXTENSION);

        ImageIO.write(img1, FileTypes.PNG_EXTENSION, img1File);
        ImageIO.write(img2, FileTypes.PNG_EXTENSION, img2File);
        ImageIO.write(diff, FileTypes.PNG_EXTENSION, diffFile);

        return new String[]{img1File.getPath(), img2File.getPath(), diffFile.getPath()};
    }
}
