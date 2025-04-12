package pdfproject.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

import java.io.*;
import java.util.concurrent.*;

public class WordToPdfConverter {

    private static final int TIMEOUT_SECONDS = 60;

    public static File convertToPdf(String wordPath) throws Exception {
        File inputFile = new File(wordPath);
        if (!inputFile.exists()) throw new FileNotFoundException("Word file not found: " + wordPath);

        File outputFile = File.createTempFile("converted_", ".pdf");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            try (InputStream in = new FileInputStream(inputFile);
                 OutputStream out = new FileOutputStream(outputFile)) {

                IConverter converter = LocalConverter.builder().build();
                converter.convert(in).as(DocumentType.MS_WORD)
                        .to(out).as(DocumentType.PDF).execute();
                converter.shutDown();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
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

        return outputFile;
    }
}
