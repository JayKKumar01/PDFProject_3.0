package pdfproject.utils.converter;

import pdfproject.constants.AppPaths;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LibreOfficeWordPdfConverter implements WordPdfConverter {

    private static final int TIMEOUT_SECONDS = 60;

    @Override
    public File convert(File wordFile) {

        String soffice = resolveSofficeExecutable();

        File outDir = new File(AppPaths.TEMP_WORD_PDF);
        outDir.mkdirs();

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    soffice,
                    "--headless",
                    "--convert-to", "pdf",
                    wordFile.getAbsolutePath(),
                    "--outdir", outDir.getAbsolutePath()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new RuntimeException("LibreOffice conversion timed out");
            }

            String pdfName = wordFile.getName()
                    .replaceAll("\\.(docx|doc)$", ".pdf");

            File outputFile = new File(outDir, pdfName);

            if (!outputFile.exists()) {
                throw new RuntimeException(
                        "LibreOffice did not produce a PDF file"
                );
            }

            return outputFile;

        } catch (Exception e) {
            throw new RuntimeException(
                    "LibreOffice Word → PDF conversion failed", e
            );
        }
    }

    /**
     * Resolves the LibreOffice 'soffice' executable in a cross-platform,
     * PATH-independent way.
     */
    private String resolveSofficeExecutable() {
        String os = System.getProperty("os.name").toLowerCase();

        // 🪟 Windows
        if (os.contains("win")) {
            String[] candidates = {
                    "C:\\Program Files\\LibreOffice\\program\\soffice.exe",
                    "C:\\Program Files (x86)\\LibreOffice\\program\\soffice.exe"
            };
            for (String path : candidates) {
                if (new File(path).exists()) {
                    return path;
                }
            }
        }

        // 🍎 macOS
        if (os.contains("mac")) {
            String macPath =
                    "/Applications/LibreOffice.app/Contents/MacOS/soffice";
            if (new File(macPath).exists()) {
                return macPath;
            }
        }

        // 🐧 Linux / fallback (PATH-based)
        try {
            Process p = new ProcessBuilder("soffice", "--version").start();
            if (p.waitFor(5, TimeUnit.SECONDS) && p.exitValue() == 0) {
                return "soffice";
            }
        } catch (Exception ignored) {}

        throw new RuntimeException(
                "LibreOffice is not installed or the 'soffice' executable could not be found.\n" +
                        "Please install LibreOffice to enable Word → PDF conversion."
        );
    }
}
