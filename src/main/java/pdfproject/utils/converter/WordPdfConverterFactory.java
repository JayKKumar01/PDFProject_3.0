package pdfproject.utils.converter;

public final class WordPdfConverterFactory {

    private WordPdfConverterFactory() {}

    public static WordPdfConverter create() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return new Documents4jWordPdfConverter();
        }

        return new LibreOfficeWordPdfConverter();
    }
}
