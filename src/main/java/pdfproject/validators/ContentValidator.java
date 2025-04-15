package pdfproject.validators;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.core.StringDiff;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.models.WordInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentValidator {
    private final InputData data;
    private final String outputImagePath;

    public ContentValidator(InputData data, String outputImagePath) {
        this.data = data;
        this.outputImagePath = outputImagePath;
    }

    public void validateContent(PDDocument doc1, PDDocument doc2, List<Integer> range1, List<Integer> range2, int rowIndex, MapModel resultMap) throws Exception {

        // Loop through the ranges and compare content
        for (int i = 0; i < range1.size(); i++) {

            List<WordInfo> words1 = extractWords(doc1, range1.get(i));
            List<WordInfo> words2 = extractWords(doc2, range2.get(i));

            // Use StringDiff class to compare word lists
            List<WordInfo> diff = StringDiff.compare(words1, words2);

            // i will do something later
        }
    }

    // Extract WordInfo list from a PDF page
    private List<WordInfo> extractWords(PDDocument document, int pageNum) throws IOException {
        List<WordInfo> wordInfoList = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String string, List<TextPosition> textPositions){

                WordInfo prevWordInfo = null;
                int line = 1;
                String[] words = string.split(getWordSeparator());
                int i = 0;
                for (String word : words) {
                    if (!word.isEmpty() && textPositions.get(i).getFontSize() > 1) {

                        List<TextPosition> positions = new ArrayList<>();
                        int len = i + word.length();
                        for (int j = i; j < len; j++) {
                            positions.add(textPositions.get(j));
                        }
                        WordInfo wordInfo = new WordInfo(word, positions);

                        if (prevWordInfo != null) {
                            if (prevWordInfo.getPosition() < wordInfo.getPosition()) {
                                line++;
                            }
                        }
                        wordInfo.setLine(line);

                        wordInfoList.add(wordInfo);

                        prevWordInfo = wordInfo;
                    }
                    i += word.length() + 1;
                }
            }
        };

        stripper.setStartPage(pageNum);
        stripper.setEndPage(pageNum);
        stripper.setSortByPosition(data.isSingleColumn());
        stripper.getText(document);  // Trigger extraction

        return wordInfoList;
    }
}
