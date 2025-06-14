package pdfproject.models;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.constants.Operation;

import java.awt.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class WordInfo {
    private final String word;
    private final List<TextPosition> textPositions;
    private List<TextPosition> otherTextPositions;
    private int line = -1;
    // Store multiple operations
    private final Set<Operation> operations = EnumSet.noneOf(Operation.class);
    private Rectangle boundingBox;
    private boolean belongsToFirst = false;
    private List<FontInfoPart> fontInfoParts;

    public void setFontInfoParts(List<FontInfoPart> parts) {
        this.fontInfoParts = parts;
    }

    public List<FontInfoPart> getFontInfoParts() {
        return fontInfoParts;
    }


    public WordInfo(String word, List<TextPosition> textPositions) {
        this.word = word;
        this.textPositions = textPositions;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getWord() {
        return word;
    }

    public List<TextPosition> getTextPositions() {
        return textPositions;
    }

    public float getPosition() {
        return textPositions.get(0).getY();
    }

    public List<TextPosition> getOtherTextPositions() {
        return otherTextPositions;
    }

    public void setOtherTextPositions(List<TextPosition> otherTextPositions) {
        this.otherTextPositions = otherTextPositions;
    }

    // ========== Operations Support ==========

    public Set<Operation> getOperations() {
        return operations;
    }

    public void addOperation(Operation op) {
        operations.add(op);
    }

    public void addOperations(Collection<Operation> ops) {
        operations.addAll(ops);
    }

    // ========== Bounding Box Support ==========

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public boolean isBelongsToFirst() {
        return belongsToFirst;
    }

    public void setBelongsToFirst(boolean belongsToFirst) {
        this.belongsToFirst = belongsToFirst;
    }


    @Override
    public String toString() {
        return "WordInfo{" +
                "word='" + word + '\'' +
                ", line=" + line +
//                ", font='" + getFontName() + '\'' +
//                ", style='" + getFontStyle() + '\'' +
//                ", size=" + getFontSize() +
                ", operations=" + operations +
                '}';
    }
}
