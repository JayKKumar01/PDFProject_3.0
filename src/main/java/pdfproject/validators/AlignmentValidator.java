package pdfproject.validators;

import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.constants.FileTypes;
import pdfproject.models.MapModel;
import pdfproject.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Validator responsible for aligning two PDF pages by rendering their images,
 * generating a visual difference (diff), and saving the results.
 */
public class AlignmentValidator {
    private final String outputImagePath;
    private final int rowIndex;
    private final PDFRenderer renderer1;
    private final PDFRenderer renderer2;
    private final MapModel resultMap;

    /**
     * Constructs an alignment validator for a specific data row.
     *
     * @param outputImagePath Base directory where image outputs will be saved.
     * @param rowIndex        Index of the current row (zero-based).
     * @param renderer1       PDF renderer for document 1.
     * @param renderer2       PDF renderer for document 2.
     * @param resultMap       Model to store output image paths.
     */
    public AlignmentValidator(String outputImagePath, int rowIndex,
                              PDFRenderer renderer1, PDFRenderer renderer2, MapModel resultMap) {
        this.outputImagePath = outputImagePath;
        this.rowIndex = rowIndex;
        this.renderer1 = renderer1;
        this.renderer2 = renderer2;
        this.resultMap = resultMap;
    }

    /**
     * Validates alignment by rendering pages from both documents,
     * generating a diff image, and saving all three.
     *
     * @param p1        Page index in document 1 (1-based).
     * @param p2        Page index in document 2 (1-based).
     * @param imagePage The displayed page number for output naming.
     * @return List of BufferedImages: [img1, img2, diff].
     * @throws Exception on image rendering or saving failure.
     */
    public List<BufferedImage> validateAlignment(int p1, int p2, int imagePage) throws Exception {
        BufferedImage img1 = null, img2 = null, diff = null;

        if (p1 >= 0) img1 = renderer1.renderImageWithDPI(p1 - 1, Config.RENDER_DPI);
        if (p2 >= 0) img2 = renderer2.renderImageWithDPI(p2 - 1, Config.RENDER_DPI);

        if (img1 != null && img2 != null) {
            diff = ImageUtils.generateDiffImage(img1, img2);
        }

        // Save images to disk and record paths in result map
        List<String> paths = saveImages(imagePage, img1, img2, diff);
        resultMap.addAlignmentRow(paths,imagePage-1);

        return Arrays.asList(img1, img2, diff);
    }

    /**
     * Saves rendered images (img1, img2, diff) to disk under a consistent structure.
     * If diff is null, fallback to saving a placeholder image from img1 or img2.
     *
     * @param pageNumber Page number used in output folder path.
     * @param img1       Rendered image from document 1.
     * @param img2       Rendered image from document 2.
     * @param diff       Difference image (may be null).
     * @return List of saved image paths: [img1Path, img2Path, diffPath]
     * @throws Exception if any image cannot be written to disk.
     */
    private List<String> saveImages(int pageNumber, BufferedImage img1, BufferedImage img2, BufferedImage diff) throws Exception {
        String dirPath = String.format("%s/item_%d/alignment/page_%d", outputImagePath, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new Exception("Failed to create output directory: " + dirPath);
        }

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
            // Fallback: reuse available image as placeholder for all three
            if (path1 != null) {
                return Arrays.asList(path1, path1, path1);
            } else if (path2 != null) {
                return Arrays.asList(path2, path2, path2);
            } else {
                throw new Exception("Both images are null. Cannot generate fallback diff.");
            }
        }

        return Arrays.asList(path1, path2, pathDiff);
    }
}
