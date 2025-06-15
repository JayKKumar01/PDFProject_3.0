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
import pdfproject.utils.WordToPdfConverter;
import pdfproject.validators.AlignmentValidator;
import pdfproject.validators.ContentValidator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Core engine that processes all row-wise PDF/Word document comparisons
 * as specified by {@link InputData}. It handles validation, alignment,
 * and diff generation for each row in the input list.
 */
public class PDFProcessor {

    /**
     * Processes all input rows and generates output for each,
     * including validation and image-based comparison data.
     *
     * @param dataList List of input data rows to process.
     * @return List of result map models per row.
     */
    public List<MapModel> processAll(List<InputData> dataList) {
        // Create unique output directory for this run
        Config.outputImagePath = AppPaths.OUTPUT_IMAGES_BASE + "\\Result - " + System.currentTimeMillis();
        List<MapModel> resultList = new ArrayList<>();

        // Process each row (InputData) independently
        for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
            InputData data = dataList.get(rowIndex);
            MapModel resultMap = new MapModel(Config.outputImagePath);
            resultMap.setKey(data.getKey());

            try {
                processRow(data, rowIndex, resultMap);
            } catch (Exception e) {
                System.err.printf("Error in item %d: %s%n", rowIndex + 1, e.getMessage());
                e.printStackTrace();
            }

            resultList.add(resultMap); // Always add to keep index consistency
        }

        // Write out data.js map for frontend viewer
        DataMapGenerator.generateDataMapJs(resultList, Config.outputImagePath);
        return resultList;
    }

    /**
     * Processes a single row of input, loading the documents,
     * extracting ranges, rendering pages, and performing validations.
     */
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
            int maxSize = Math.max(size1, size2);

            if (size1 != size2) {
                int diff = Math.abs(size1 - size2);
                System.out.printf("⚠️  [Item %d]: Document %d has %d extra page(s).%n",
                        itemIndex + 1,
                        size1 > size2 ? 1 : 2,
                        diff
                );
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

            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < maxSize; i++) {
                final int pageIndex = i;
                final int p1 = pageIndex < size1 ? range1.get(pageIndex) : -1;
                final int p2 = pageIndex < size2 ? range2.get(pageIndex) : -1;

                futures.add(executor.submit(() -> {
                    String pageLabel = "Item " + (itemIndex + 1) + " | Page " + (pageIndex + 1);
                    int attempt = 0;
                    while (true) {
                        try {
                            System.out.printf("📄 %s: Doc1 Page %s, Doc2 Page %s%n",
                                    pageLabel,
                                    p1 >= 0 ? String.valueOf(p1) : "N/A",
                                    p2 >= 0 ? String.valueOf(p2) : "N/A"
                            );

                            List<BufferedImage> images = alignmentValidator.validateAlignment(p1, p2, pageIndex + 1);
                            contentValidator.validateContent(p1, p2, pageIndex + 1, images);

                            System.out.printf("✅ %s: Validation complete.%n", pageLabel);
                            break;
                        } catch (Throwable e) {
                            attempt++;
                            if (isTransientError(e)) {
                                System.err.printf("🔁 %s: Transient error on attempt %d - retrying: %s%n", pageLabel, attempt, e.getMessage());
                                Thread.sleep(500); // small delay before retry
                            } else {
                                System.err.printf("❌ %s: Validation failed permanently - %s%n", pageLabel, e.getMessage());
                                e.printStackTrace();
                                break; // Don't retry non-transient errors
                            }
                        }
                    }
                    return null;
                }));
            }

            // Wait for all page validations to finish
            for (Future<Void> future : futures) {
                future.get(); // Blocks until done or error (already handled inside)
            }

            System.out.printf("✔️  Item %d: Validation for all pages completed.%n", itemIndex + 1);
        }
    }

    private boolean isTransientError(Throwable e) {
        // Add more as needed — depending on behavior you observed
        Throwable root = e;
        while (root.getCause() != null) root = root.getCause();

        return root instanceof OutOfMemoryError
                || root instanceof java.awt.image.RasterFormatException
                || root.getMessage() != null && root.getMessage().toLowerCase().contains("heap space");
    }



    /**
     * Ensures the given file path is a PDF. If it's a Word document,
     * it is converted to PDF using {@link WordToPdfConverter}.
     *
     * @param path Path to the input document (PDF or Word).
     * @return File object pointing to the PDF file.
     * @throws Exception if file conversion or access fails.
     */
    private File ensurePdf(String path) throws Exception {
        return path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION)
                ? new File(path)
                : WordToPdfConverter.convertToPdf(path);
    }
}
