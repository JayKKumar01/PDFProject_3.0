package testing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.models.WordInfo;
import pdfproject.utils.FontInfoUtil;
import pdfproject.utils.WordToPdfConverter;
import pdfproject.constants.Operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FontVariationPdfTest {

    public static void main(String[] args) {
        try {
            // Step 1: Convert Word to PDF
            File pdf = WordToPdfConverter.convertToPdf("FontVariationTest2.docx");
            System.out.println("📁 PDF generated at: " + pdf.getAbsolutePath());

            // Step 2: Load PDF
            try (PDDocument document = PDDocument.load(pdf)) {
                int pageCount = document.getNumberOfPages();
                System.out.println("📄 Total Pages: " + pageCount);

                // Step 3: Process each page
                for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                    System.out.println("\n--- Page " + pageNum + " ---");
                    List<WordInfo> words = extractWords(document, pageNum);
                    for (WordInfo word : words) {
                        FontInfoUtil.setFontInfo(Operation.FONT, word);

                        String rawFont = word.getTextPositions().isEmpty()
                                ? "❌"
                                : word.getTextPositions().get(0).getFont().getName();

                        System.out.println("📝 Word: " + word.getWord());
                        System.out.println("   ↪ Raw Font: " + rawFont);
                        System.out.println("   ℹ️ Info: " + FontInfoUtil.getPlainInfo(word));
                        System.out.println(); // line break
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<WordInfo> extractWords(PDDocument document, int pageNum) throws IOException {
        List<WordInfo> wordInfoList = new ArrayList<>();
        if (document == null || pageNum == -1) return wordInfoList;

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String ignored, List<TextPosition> textPositions) {
                StringBuilder currentWord = new StringBuilder();
                List<TextPosition> currentPositions = new ArrayList<>();

                for (TextPosition tp : textPositions) {
                    String unicode = tp.getUnicode();
                    for (char c : unicode.toCharArray()) {
                        if (Character.isWhitespace(c)) {
                            if (!currentWord.isEmpty()) {
                                wordInfoList.add(new WordInfo(currentWord.toString(), new ArrayList<>(currentPositions)));
                                currentWord.setLength(0);
                                currentPositions.clear();
                            }
                        } else {
                            currentWord.append(c);
                            currentPositions.add(tp);
                        }
                    }
                }

                if (!currentWord.isEmpty()) {
                    wordInfoList.add(new WordInfo(currentWord.toString(), currentPositions));
                }
            }
        };

        stripper.setStartPage(pageNum);
        stripper.setEndPage(pageNum);
        stripper.setSortByPosition(true);
        stripper.getText(document);

        return wordInfoList;
    }
}
