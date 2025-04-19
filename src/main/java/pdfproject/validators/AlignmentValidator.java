package pdfproject.validators;

import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.constants.FileTypes;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlignmentValidator {
    private final InputData data;
    private final String outputImagePath;
    private final int rowIndex;
    private final PDFRenderer renderer1;
    private final PDFRenderer renderer2;
    private final MapModel resultMap;

    public AlignmentValidator(InputData data, String outputImagePath, int rowIndex, PDFRenderer renderer1, PDFRenderer renderer2, MapModel resultMap) {
        this.data = data;
        this.outputImagePath = outputImagePath;
        this.rowIndex = rowIndex;
        this.renderer1 = renderer1;
        this.renderer2 = renderer2;
        this.resultMap = resultMap;
    }

    public List<BufferedImage> validateAlignment(int p1, int p2, int imagePage) throws Exception {
        BufferedImage img1 = null, img2 = null, diff = null;

        if (p1 >= 0) img1 = renderer1.renderImageWithDPI(p1-1, Config.RENDER_DPI);
        if (p2 >= 0) img2 = renderer2.renderImageWithDPI(p2-1, Config.RENDER_DPI);

        if (img1 != null && img2 != null) {
            diff = ImageUtils.generateDiffImage(img1, img2);
        }
        resultMap.addAlignmentRow(saveImages(imagePage, img1, img2, diff));

        return Arrays.asList(img1, img2, diff);
    }



    private List<String> saveImages(int pageNumber, BufferedImage img1, BufferedImage img2, BufferedImage diff) throws Exception {
        String dirPath = String.format("%s/item_%d/alignment/page_%d", outputImagePath, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        String path1 = null, path2 = null, pathDiff = null;

        if (img1 != null) {
            File img1File = new File(dir, "img1" + FileTypes.IMAGE_EXTENSION);
            ImageIO.write(img1, FileTypes.IMAGE_TYPE, img1File);
            path1 = img1File.getPath();
        }

        if (img2 != null) {
            File img2File = new File(dir, "img2" + FileTypes.IMAGE_EXTENSION);
            ImageIO.write(img2, FileTypes.IMAGE_TYPE, img2File);
            path2 = img2File.getPath();
        }

        if (diff != null) {
            File diffFile = new File(dir, "diff" + FileTypes.IMAGE_EXTENSION);
            ImageIO.write(diff, FileTypes.IMAGE_TYPE, diffFile);
            pathDiff = diffFile.getPath();
        } else {
            // Fallback: Use a valid path (img1 or img2) if diff is null
            if (path1 != null) {
                return Arrays.asList(path1,path1,path1);
            } else if (path2 != null) {
                return Arrays.asList(path2,path2,path2);
            }
        }

        return Arrays.asList(path1, path2, pathDiff);
    }


}
