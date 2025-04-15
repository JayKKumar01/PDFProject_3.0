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

    public void validateAlignment(int p1, int p2, int imagePage) throws Exception {

        BufferedImage img1 = renderer1.renderImageWithDPI(p1, Config.RENDER_DPI);
        BufferedImage img2 = renderer2.renderImageWithDPI(p2, Config.RENDER_DPI);
        BufferedImage diff = ImageUtils.generateDiffImage(img1, img2);

        String[] paths = saveImages(imagePage, img1, img2, diff);
        List<String> alignmentRow = new ArrayList<>();
        alignmentRow.add(paths[0]); // img1 path
        alignmentRow.add(paths[1]); // img2 path
        alignmentRow.add(paths[2]); // diff path

        resultMap.addAlignmentRow(alignmentRow);
    }

    private String[] saveImages(int pageNumber, BufferedImage img1, BufferedImage img2, BufferedImage diff) throws Exception {
        String dirPath = String.format("%s/item_%d/alignment/page_%d", outputImagePath, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        File img1File = new File(dir, "img1" + FileTypes.IMAGE_EXTENSION);
        File img2File = new File(dir, "img2" + FileTypes.IMAGE_EXTENSION);
        File diffFile = new File(dir, "diff" + FileTypes.IMAGE_EXTENSION);

        ImageIO.write(img1, FileTypes.IMAGE_TYPE, img1File);
        ImageIO.write(img2, FileTypes.IMAGE_TYPE, img2File);
        ImageIO.write(diff, FileTypes.IMAGE_TYPE, diffFile);

        return new String[]{img1File.getPath(), img2File.getPath(), diffFile.getPath()};
    }
}
