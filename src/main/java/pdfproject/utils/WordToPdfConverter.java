package pdfproject.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import org.apache.poi.xwpf.usermodel.XWPFComments;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import pdfproject.constants.AppPaths;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;

public class WordToPdfConverter {

    private static final int TIMEOUT_SECONDS = 120;
    private static final File tempDir = new File(AppPaths.TEMP_WORD_PDF);

    public static File convertToPdf(String wordPath) throws Exception {
        File inputFile = new File(wordPath);
        if (!inputFile.exists()) throw new FileNotFoundException("Word file not found: " + wordPath);

        // Ensure the TEMP_WORD_PDF directory exists
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // First, remove comments from the Word document
        File cleanedWordFile = isDocx(wordPath) ? removeCommentsFromWord(inputFile) : inputFile;

        File outputFile = File.createTempFile("converted_", ".pdf", tempDir);
        long time = System.currentTimeMillis();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        IConverter converter = LocalConverter.builder().build();
        Future<Boolean> future = executor.submit(() -> {
            try (InputStream in = new FileInputStream(cleanedWordFile);
                 OutputStream out = new FileOutputStream(outputFile)) {
                converter.convert(in).as(DocumentType.MS_WORD)
                        .to(out).as(DocumentType.PDF).execute();
                converter.shutDown();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }finally {
                converter.shutDown();
            }
        });

        try {
            boolean success = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!success) throw new RuntimeException("Conversion failed.");
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Conversion timed out after " + TIMEOUT_SECONDS + " seconds.");
        } finally {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - time) / 1000.0;
        System.out.printf("🕒 Total time taken: %.2f seconds%n", elapsedSeconds);

        return outputFile;
    }

    private static boolean isDocx(String wordPath) {
        return wordPath.toLowerCase().endsWith(".docx");
    }

    private static File removeCommentsFromWord(File inputFile) throws Exception {
        // Load the Word document using Apache POI
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(inputFile))) {

            // Remove comments by iterating through all paragraphs, runs, and comments
            removeCommentsFromParagraphs(document);

            // Save the cleaned Word file to the TEMP_WORD_PDF directory
            File cleanedFile = new File(tempDir, "cleaned_" + inputFile.getName());
            try (FileOutputStream fos = new FileOutputStream(cleanedFile)) {
                document.write(fos);
            }

            return cleanedFile;
        }
    }

    private static void removeCommentsFromParagraphs(XWPFDocument document) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // Remove XML-based comment range markers
            removeCommentRangesFromParagraph(paragraph);

            List<XWPFRun> runs = paragraph.getRuns();
            if (runs != null) {
                for (int i = runs.size() - 1; i >= 0; i--) {
                    CTR ctr = runs.get(i).getCTR();
                    if (ctr != null && ctr.getCommentReferenceList() != null && !ctr.getCommentReferenceList().isEmpty()) {
                        paragraph.removeRun(i);
                    }
                }
            }
        }

        // Clear all document-level comments if present
        XWPFComments comments = document.getDocComments();
        if (comments != null) {
            comments.getComments().clear();
        }
    }

    private static void removeCommentRangesFromParagraph(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        if (ctp != null) {
            int startCount = ctp.sizeOfCommentRangeStartArray();
            for (int i = startCount - 1; i >= 0; i--) {
                ctp.removeCommentRangeStart(i);
            }

            int endCount = ctp.sizeOfCommentRangeEndArray();
            for (int i = endCount - 1; i >= 0; i--) {
                ctp.removeCommentRangeEnd(i);
            }
        }
    }

}
