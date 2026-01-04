package pdfproject.utils.converter;

import java.io.File;

public interface WordPdfConverter {
    File convert(File wordFile) throws Exception;
}
