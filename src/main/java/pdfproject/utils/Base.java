package pdfproject.utils;

import org.apache.poi.ss.usermodel.IndexedColors;
import pdfproject.constants.Operation;
import pdfproject.constants.OperationColor;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Base {

    public static Color getOperationColor(Set<Operation> operations) {
        if (operations == null || operations.isEmpty()) {
            return null;
        }
        if (operations.size() > 1) {
            return OperationColor.MULTIPLE;
        }

        Operation op = operations.iterator().next();
        return switch (op) {
            case DELETED -> OperationColor.DELETED;
            case ADDED -> OperationColor.ADDED;
            case FONT -> OperationColor.FONT_NAME;
            case SIZE -> OperationColor.FONT_SIZE;
            case STYLE -> OperationColor.FONT_STYLE;
            case EQUAL -> null;
        };
    }

    /**
     * Checks if font information of two WordInfo objects is the same.
     *
     * @param wordInfo1 First WordInfo object.
     * @param wordInfo2 Second WordInfo object.
     * @return True if font information is the same, false otherwise.
     */
    public static boolean isFontInfoSame(WordInfo wordInfo1, WordInfo wordInfo2) {
        if (wordInfo1.getFontName() == null || wordInfo2.getFontName() == null) {
            return false;
        }
        return wordInfo1.getFontName().equals(wordInfo2.getFontName()) &&
                wordInfo1.getFontSize() == wordInfo2.getFontSize() &&
                wordInfo1.getFontStyle().equals(wordInfo2.getFontStyle());
    }

    public static String getInfo(Operation operation, WordInfo wordInfo) {
        return "["+operation.name()+": (Font: "+wordInfo.getFontName()+", Size: "+wordInfo.getFontSize()+", Style: "+wordInfo.getFontStyle()+")]";
    }

    /**
     * Updates font information of the second WordInfo object based on the differences with the first one.
     *
     * @param wordInfo1 First WordInfo object.
     * @param wordInfo2 Second WordInfo object.
     */
    public static void updateFontInfo(WordInfo wordInfo1, WordInfo wordInfo2) {

        StringBuilder builder = new StringBuilder();
        String divider = " : ";
        builder.append("[");
        boolean isComma = false;

        String font1 = wordInfo1.getFontName();
        String font2 = wordInfo2.getFontName();

        if (!font1.equals(font2)){
            wordInfo2.addOperation(Operation.FONT);
            isComma = true;
            builder.append(font1).append(divider).append(font2);
        }
        int size1 = wordInfo1.getFontSize();
        int size2 = wordInfo2.getFontSize();
        if (size1 != size2){
            wordInfo2.addOperation(Operation.SIZE);
            if (isComma){
                builder.append(", ");
            }
            builder.append("Size(").append(size1).append(divider).append(size2).append(")");
            isComma = true;
        }
        String style1 = wordInfo1.getFontStyle();
        String style2 = wordInfo2.getFontStyle();
        if (!style1.equals(style2)){
            wordInfo2.addOperation(Operation.STYLE);
            if (isComma){
                builder.append(", ");
            }
            builder.append(style1).append(divider).append(style2);
        }
        builder.append("]");
        wordInfo2.setInfo(builder.toString());
    }


}