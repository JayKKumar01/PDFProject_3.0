package pdfproject.validators;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.Config;
import pdfproject.constants.FileTypes;
import pdfproject.constants.Operation;
import pdfproject.core.StringDiff;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.models.WordInfo;
import pdfproject.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentValidator {
    private final InputData data;
    private final String outputImagePath;
    private final int rowIndex;
    private final PDDocument doc1;
    private final PDDocument doc2;
    private final MapModel resultMap;

    public ContentValidator(InputData data, String outputImagePath, int rowIndex, PDDocument doc1, PDDocument doc2, MapModel resultMap) {
        this.data = data;
        this.outputImagePath = outputImagePath;
        this.rowIndex = rowIndex;
        this.doc1 = doc1;
        this.doc2 = doc2;
        this.resultMap = resultMap;
    }

    public void validateContent(int p1, int p2, int imagePage, List<BufferedImage> images) throws Exception {

        List<WordInfo> words1 = extractWords(doc1, p1);
        List<WordInfo> words2 = extractWords(doc2, p2);

        // Use StringDiff class to compare word lists
        List<WordInfo> diff = StringDiff.compare(words1, words2);

        List<WordInfo> forImage1 = diff.stream()
                .filter(word -> word.getOperations().contains(Operation.DELETED))
                .toList();

        List<WordInfo> forImage2 = diff.stream()
                .filter(word -> !word.getOperations().contains(Operation.DELETED))
                .toList();




        // Create bounding boxes on each image
        BufferedImage boxedImg1 = ImageUtils.drawBoundingBoxes(images.get(0), forImage1);
        BufferedImage boxedImg2 = ImageUtils.drawBoundingBoxes(images.get(1), forImage2);

        // Combine images side by side
        BufferedImage combinedImage = combineImagesSideBySide(boxedImg1, boxedImg2);


        BufferedImage diffImage = generateDiffImage(diff, images.get(0), images.get(1));
        String diffPath = saveDiffImage(imagePage, diffImage);

        // Save the combined image
        String[] paths = saveImage(imagePage, combinedImage);
        List<String> finalList = new ArrayList<>(List.of(paths));
        finalList.add(diffPath);
        resultMap.addContentRow(finalList);

    }

    private BufferedImage generateDiffImage(List<WordInfo> diff, BufferedImage img1, BufferedImage img2) {
        final int padding = 5;
        final int width = Math.max(img1.getWidth(), img2.getWidth());

        int estimatedHeight = 0;
        float lastPos = -1;
        String lastInfo = null;
        int currentLineHeight = 0;

        // Estimate required height
        for (WordInfo word : diff) {
            Rectangle box = word.getBoundingBox();
            if (box.height == 0) continue;

            String info = word.getInfo();
            float pos = word.getPosition();

            if (pos != lastPos || !Objects.equals(info, lastInfo)) {
                estimatedHeight += currentLineHeight + padding;
                currentLineHeight = box.height;
            } else {
                currentLineHeight = Math.max(currentLineHeight, box.height);
            }

            lastPos = pos;
            lastInfo = info;
        }
        estimatedHeight += currentLineHeight + 2 * padding;

        int finalHeight = Math.max(estimatedHeight, Math.max(img1.getHeight(), img2.getHeight()));
        BufferedImage diffImg = new BufferedImage(width, finalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = diffImg.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, finalHeight);

        // Draw words
        int x = padding, y = padding;
        lastPos = -1;
        lastInfo = null;

        for (WordInfo word : diff) {
            Rectangle box = word.getBoundingBox();
            if (box.width == 0 || box.height == 0) continue;

            String info = word.getInfo();
            float pos = word.getPosition();

            if (pos != lastPos || !Objects.equals(info, lastInfo)) {
                x = padding;
                y += box.height + padding;
            }

            BufferedImage srcImg = word.isBelongsToFirst() ? img1 : img2;
            BufferedImage wordImg = srcImg.getSubimage(
                    Math.max(0, box.x),
                    Math.max(0, box.y),
                    Math.min(box.width, srcImg.getWidth() - box.x),
                    Math.min(box.height, srcImg.getHeight() - box.y)
            );

            g.drawImage(wordImg, x, y, null);
            x += box.width + padding;

            lastPos = pos;
            lastInfo = info;
        }

        g.dispose();
        return diffImg;
    }


    private String saveDiffImage(int pageNumber, BufferedImage diffImage) throws IOException {
        String dirPath = String.format("%s/item_%d/content/page_%d", outputImagePath, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        File diffFile = new File(dir, "diff" + FileTypes.IMAGE_EXTENSION);
        ImageIO.write(diffImage, FileTypes.IMAGE_TYPE, diffFile);

        return diffFile.getPath();
    }


    // Combine images side by side (both images should have the same height)
    private BufferedImage combineImagesSideBySide(BufferedImage img1, BufferedImage img2) {
        int totalWidth = img1.getWidth() + img2.getWidth();
        int height = Math.max(img1.getHeight(), img2.getHeight());

        BufferedImage combined = new BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combined.createGraphics();
        g2d.drawImage(img1, 0, 0, null);  // Draw the first image at (0, 0)
        g2d.drawImage(img2, img1.getWidth(), 0, null);  // Draw the second image next to it
        g2d.dispose();

        return combined;
    }

    // Save the combined image
    private String[] saveImage(int pageNumber, BufferedImage combinedImage) throws Exception {
        String dirPath = String.format("%s/item_%d/content/page_%d", outputImagePath, rowIndex + 1, pageNumber);
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        File combinedFile = new File(dir, "combined" + FileTypes.IMAGE_EXTENSION);
        ImageIO.write(combinedImage, FileTypes.IMAGE_TYPE, combinedFile);

        return new String[]{combinedFile.getPath()};
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
