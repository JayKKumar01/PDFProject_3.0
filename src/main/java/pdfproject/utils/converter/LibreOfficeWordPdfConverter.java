package pdfproject.utils.converter;

import pdfproject.constants.AppPaths;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LibreOfficeWordPdfConverter implements WordPdfConverter {

    @Override
    public File convert(File wordFile) throws Exception {

        File outDir = new File(AppPaths.TEMP_WORD_PDF);
        outDir.mkdirs();

        ProcessBuilder pb = new ProcessBuilder(
                "soffice",
                "--headless",
                "--convert-to", "pdf",
                wordFile.getAbsolutePath(),
                "--outdir", outDir.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        if (!process.waitFor(60, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("LibreOffice conversion timed out");
        }

        String pdfName = wordFile.getName().replaceAll("\\.(docx|doc)$", ".pdf");
        File outputFile = new File(outDir, pdfName);

        if (!outputFile.exists()) {
            throw new RuntimeException("LibreOffice failed to create PDF");
        }

        return outputFile;
    }
}
