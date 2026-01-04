package pdfproject.utils.converter;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import pdfproject.constants.AppPaths;

import java.io.*;
import java.util.concurrent.*;

public class Documents4jWordPdfConverter implements WordPdfConverter {

    private static final int TIMEOUT_SECONDS = 120;

    @Override
    public File convert(File wordFile) throws Exception {

        // ❌ Fail fast if MS Word is not installed
        if (!isMicrosoftWordInstalled()) {
            throw new RuntimeException(
                    "Microsoft Word is not installed.\n" +
                            "documents4j requires MS Word on Windows to convert Word → PDF."
            );
        }

        File outputFile = File.createTempFile(
                "converted_", ".pdf",
                new File(AppPaths.TEMP_WORD_PDF)
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();
        IConverter converter = LocalConverter.builder().build();

        Future<Boolean> future = executor.submit(() -> {
            try (InputStream in = new FileInputStream(wordFile);
                 OutputStream out = new FileOutputStream(outputFile)) {

                converter.convert(in)
                        .as(DocumentType.MS_WORD)
                        .to(out)
                        .as(DocumentType.PDF)
                        .execute();
                return true;

            } finally {
                converter.shutDown();
            }
        });

        try {
            if (!future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new RuntimeException("Conversion failed");
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Conversion timed out");
        } finally {
            executor.shutdownNow();
        }

        return outputFile;
    }

    /**
     * Checks whether Microsoft Word (winword.exe) is available on Windows.
     */
    private boolean isMicrosoftWordInstalled() {
        try {
            Process process = new ProcessBuilder(
                    "powershell",
                    "-command",
                    "try { " +
                            "  $w = New-Object -ComObject Word.Application; " +
                            "  $w.Quit(); " +
                            "  exit 0 " +
                            "} catch { exit 1 }"
            ).start();

            return process.waitFor() == 0;

        } catch (Exception e) {
            return false;
        }
    }

}
