package pdfproject.constants;

public final class AppPaths {
    private AppPaths() {}

    public static final String DOWNLOAD_DIR = System.getProperty("user.home") + "\\Downloads";
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static final String OUTPUT_IMAGES_BASE = DOWNLOAD_DIR + "\\PDFProject\\Images";
    public static final String TEMP_WORD_PDF = TEMP_DIR + "\\PDFProject\\wordpdfs";
}
