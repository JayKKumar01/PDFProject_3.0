package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
import pdfproject.constants.Operation;
import pdfproject.constants.OperationColor;
import pdfproject.models.DiffItem;
import pdfproject.models.FontInfoPart;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FontInfoUtil {

    public static void setFontInfo(Operation operation, WordInfo wordInfo) {
        List<TextPosition> positions = wordInfo.getTextPositions();
        if (positions == null || positions.isEmpty()) {
            wordInfo.setFontInfoParts(Collections.singletonList(
                    new FontInfoPart("[" + operation.name() + ": empty]", Color.BLACK)));
            return;
        }

        List<FontInfoPart> parts = new ArrayList<>();

        // Correct color for operation name using utility method
        parts.add(new FontInfoPart("[" + operation.name() + "] ", OperationColor.get(operation)));

        StringBuilder charGroup = new StringBuilder();

        String lastFont = null;
        int lastSize = -1;
        String lastStyle = null;
        boolean isFirstGroup = true;
        boolean needChunk = false;

        for (int i = 0; i < positions.size(); i++) {
            TextPosition tp = positions.get(i);
            char ch = wordInfo.getWord().charAt(i);

            String font = WordUtil.getCleanFontName(tp.getFont().getName());
            int size = Math.round(tp.getFontSize());
            String style = WordUtil.getFontStyle(tp);

            if (lastFont == null) {
                charGroup.append(ch);
                lastFont = font;
                lastSize = size;
                lastStyle = style;
            } else if (font.equals(lastFont) && size == lastSize && style.equals(lastStyle)) {
                charGroup.append(ch);
            } else {
                needChunk = true;
                if (!isFirstGroup) parts.add(new FontInfoPart(", ", Color.BLACK));

                parts.add(new FontInfoPart("[" + charGroup + "]: ", Color.BLACK));
                parts.add(new FontInfoPart(lastFont, OperationColor.get(Operation.FONT)));
                parts.add(new FontInfoPart("/", Color.BLACK));
                parts.add(new FontInfoPart(String.valueOf(lastSize), OperationColor.get(Operation.SIZE)));
                parts.add(new FontInfoPart("/", Color.BLACK));
                parts.add(new FontInfoPart(lastStyle, OperationColor.get(Operation.STYLE)));

                isFirstGroup = false;
                charGroup.setLength(0);
                charGroup.append(ch);
                lastFont = font;
                lastSize = size;
                lastStyle = style;
            }
        }

        // Final group
        if (!charGroup.isEmpty()) {
            if (!isFirstGroup) parts.add(new FontInfoPart(", ", Color.BLACK));
            if (needChunk) {
                parts.add(new FontInfoPart("[" + charGroup + "]: ", Color.BLACK));
            }
            parts.add(new FontInfoPart(lastFont, OperationColor.get(Operation.FONT)));
            parts.add(new FontInfoPart("/", Color.BLACK));
            parts.add(new FontInfoPart(String.valueOf(lastSize), OperationColor.get(Operation.SIZE)));
            parts.add(new FontInfoPart("/", Color.BLACK));
            parts.add(new FontInfoPart(lastStyle, OperationColor.get(Operation.STYLE)));
        }

        wordInfo.setFontInfoParts(parts);
    }



    public static void setFontDiffInfo(WordInfo wordInfo) {
        List<TextPosition> tps1 = wordInfo.getOtherTextPositions();
        List<TextPosition> tps2 = wordInfo.getTextPositions();

        if (tps1 == null || tps2 == null || tps1.size() != tps2.size()) {
            wordInfo.setFontInfoParts(Collections.singletonList(
                    new FontInfoPart("[Invalid or mismatched positions]", Color.BLACK)));
            return;
        }

        List<FontInfoPart> parts = new ArrayList<>();
        parts.add(new FontInfoPart("[FONT_DIFF] ", Color.BLACK));

        StringBuilder chunk = new StringBuilder();
        List<DiffItem> lastDiffs = null;
        boolean needChunk = false;

        for (int i = 0; i < tps1.size(); i++) {
            TextPosition tp1 = tps1.get(i);
            TextPosition tp2 = tps2.get(i);
            char ch = wordInfo.getWord().charAt(i);

            List<DiffItem> currentDiffs = new ArrayList<>();

            String font1 = WordUtil.getCleanFontName(tp1.getFont().getName());
            String font2 = WordUtil.getCleanFontName(tp2.getFont().getName());
            if (!Objects.equals(font1, font2)) {
                currentDiffs.add(new DiffItem(Operation.FONT, font1, font2));
                wordInfo.addOperation(Operation.FONT);
            }

            int size1 = Math.round(tp1.getFontSize());
            int size2 = Math.round(tp2.getFontSize());
            if (size1 != size2) {
                currentDiffs.add(new DiffItem(Operation.SIZE, String.valueOf(size1), String.valueOf(size2)));
                wordInfo.addOperation(Operation.SIZE);
            }

            String style1 = WordUtil.getFontStyle(tp1);
            String style2 = WordUtil.getFontStyle(tp2);
            if (!Objects.equals(style1, style2)) {
                currentDiffs.add(new DiffItem(Operation.STYLE, style1, style2));
                wordInfo.addOperation(Operation.STYLE);
            }

            if (lastDiffs == null || currentDiffs.equals(lastDiffs)) {
                chunk.append(ch);
            } else {
                needChunk = true;
                appendChunkDiff(parts, chunk.toString(), lastDiffs);
                chunk.setLength(0);
                chunk.append(ch);
            }

            lastDiffs = currentDiffs;
        }

        // Final chunk
        if (!chunk.isEmpty()) {
            if (needChunk) {
                appendChunkDiff(parts, chunk.toString(), lastDiffs);
            } else {
                appendChunkDiff(parts, null, lastDiffs); // Only append diffs
            }
        }

        wordInfo.setFontInfoParts(parts);
    }


    private static void appendChunkDiff(List<FontInfoPart> parts, String text, List<DiffItem> diffs) {
        if (diffs == null || diffs.isEmpty()) {
            if (text != null) {
                parts.add(new FontInfoPart("[" + text + "]: ", Color.BLACK));
            }
            parts.add(new FontInfoPart("same", Color.BLACK));
            parts.add(new FontInfoPart(", ", Color.BLACK));
            return;
        }

        if (text != null) {
            parts.add(new FontInfoPart("[" + text + "]: ", Color.BLACK));
        }

        for (int i = 0; i < diffs.size(); i++) {
            DiffItem diff = diffs.get(i);
            Color color = OperationColor.get(diff.getOperation());

            parts.add(new FontInfoPart(diff.getFrom(), color));
            parts.add(new FontInfoPart("→", Color.BLACK));
            parts.add(new FontInfoPart(diff.getTo(), color));

            if (i < diffs.size() - 1) {
                parts.add(new FontInfoPart("/", Color.BLACK));
            }
        }

        parts.add(new FontInfoPart(", ", Color.BLACK));
    }

    public static String getPlainInfo(WordInfo word) {
        List<FontInfoPart> parts = word.getFontInfoParts();
        if (parts == null) return "[Invalid or mismatched positions]";
        return parts.stream().map(FontInfoPart::getText).collect(Collectors.joining());
    }



}
