package pdfproject.utils;

import org.apache.pdfbox.text.TextPosition;
import pdfproject.constants.Operation;
import pdfproject.models.WordInfo;

import java.util.*;

public class FontInfoUtil {

    public static void setFontInfo(Operation operation, WordInfo wordInfo) {
        List<TextPosition> positions = wordInfo.getTextPositions();
        if (positions == null || positions.isEmpty()) {
            wordInfo.setInfo("[" + operation.name() + ": empty]");
            return;
        }

        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("[").append(operation.name()).append(": ");

        StringBuilder charGroup = new StringBuilder();

        String lastFont = null;
        int lastSize = -1;
        String lastStyle = null;
        boolean isFirstGroup = true;

        for (int i = 0; i < positions.size(); i++) {
            TextPosition tp = positions.get(i);
            char ch = wordInfo.getWord().charAt(i);

            String font = WordUtil.getCleanFontName(tp.getFont().getName());
            int size = Math.round(tp.getFontSize());
            String style = WordUtil.getFontStyle(tp.getFont().getName());

            if (lastFont == null) {
                charGroup.append(ch);
                lastFont = font;
                lastSize = size;
                lastStyle = style;
            } else if (font.equals(lastFont) && size == lastSize && style.equals(lastStyle)) {
                charGroup.append(ch);
            } else {
                if (!isFirstGroup) infoBuilder.append(", ");
                infoBuilder.append(charGroup)
                        .append("(Font: ").append(lastFont)
                        .append(", Size: ").append(lastSize)
                        .append(", Style: ").append(lastStyle)
                        .append(")");
                isFirstGroup = false;

                // Reset group
                charGroup.setLength(0);
                charGroup.append(ch);
                lastFont = font;
                lastSize = size;
                lastStyle = style;
            }
        }

        // Final group
        if (!charGroup.isEmpty()) {
            if (!isFirstGroup) infoBuilder.append(", ");
            infoBuilder.append(charGroup)
                    .append("(Font: ").append(lastFont)
                    .append(", Size: ").append(lastSize)
                    .append(", Style: ").append(lastStyle)
                    .append(")");
        }

        infoBuilder.append("]");
        wordInfo.setInfo(infoBuilder.toString());
    }

    public static void setFontDiffInfo(WordInfo wordInfo) {
        List<TextPosition> tps1 = wordInfo.getOtherTextPositions();
        List<TextPosition> tps2 = wordInfo.getTextPositions();

        if (tps1 == null || tps2 == null || tps1.size() != tps2.size()) {
            wordInfo.setInfo("[Invalid or mismatched positions]");
            return;
        }

        StringBuilder result = new StringBuilder();
        StringBuilder chunk = new StringBuilder();
        String lastDiff = null;
        boolean needChunk = false;

        for (int i = 0; i < tps1.size(); i++) {
            TextPosition tp1 = tps1.get(i);
            TextPosition tp2 = tps2.get(i);
            char ch = wordInfo.getWord().charAt(i);

            String font1 = WordUtil.getCleanFontName(tp1.getFont().getName());
            String font2 = WordUtil.getCleanFontName(tp2.getFont().getName());
            String style1 = WordUtil.getFontStyle(tp1.getFont().getName());
            String style2 = WordUtil.getFontStyle(tp2.getFont().getName());
            int size1 = Math.round(tp1.getFontSize());
            int size2 = Math.round(tp2.getFontSize());

            List<String> diffs = new ArrayList<>();

            if (!Objects.equals(font1, font2)) {
                diffs.add(font1 + " : " + font2);
                wordInfo.addOperation(Operation.FONT);
            }

            if (size1 != size2) {
                diffs.add(size1 + " : " + size2);
                wordInfo.addOperation(Operation.SIZE);
            }

            if (!Objects.equals(style1, style2)) {
                diffs.add(style1 + " : " + style2);
                wordInfo.addOperation(Operation.STYLE);
            }

            String currentDiff = diffs.isEmpty() ? "[same]" : "[" + String.join(", ", diffs) + "]";

            if (lastDiff == null) {
                chunk.append(ch);
                lastDiff = currentDiff;
            } else if (lastDiff.equals(currentDiff)) {
                chunk.append(ch);
            } else {
                needChunk = true;
                result.append(chunk).append(": ").append(lastDiff).append(", ");
                chunk.setLength(0);
                chunk.append(ch);
                lastDiff = currentDiff;
            }
        }

        // Append the final chunk
        if (!chunk.isEmpty() && lastDiff != null) {
            if (needChunk){
                result.append(chunk).append(": ");
            }
            result.append(lastDiff);
        }

        wordInfo.setInfo(result.toString());
    }
}
