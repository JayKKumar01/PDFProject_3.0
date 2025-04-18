package pdfproject.models;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.Config;
import pdfproject.constants.Operation;

import java.awt.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class WordInfo {
    private final String word;
    private final List<TextPosition> positions;
    private int line = -1;
    // Store multiple operations
    private final Set<Operation> operations = EnumSet.noneOf(Operation.class);
    private String info;
    private Rectangle boundingBox;
    private boolean belongsToFirst = false;

    public WordInfo(String word, List<TextPosition> positions) {
        this.word = word;
        this.positions = positions;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public List<TextPosition> getPositions() {
        return positions;
    }

    public float getPosition() {
        return positions.get(0).getY();
    }

    public PDFont getPDFont() {
        return positions.get(0).getFont();
    }

    public String getJustFont() {
        return getPDFont().getName();
    }

    public String getFontName() {
        String font = getJustFont();
        if (font == null) {
            return null;
        }
        if (font.contains("+")){
            font = font.substring(font.indexOf("+")+1);
        }
        if (font.contains("-")){
            font = font.replace(font.substring(font.lastIndexOf("-")),"");
        }else if (font.contains(",")){
            font = font.replace(font.substring(font.lastIndexOf(",")),"");
        }
        return font.replace("mt","").replace("MT","");
    }

    public String getFontStyle() {
        String font = getJustFont();
        if (font == null){
            return "unknown";
        }
        font = font.toLowerCase().replace("mt","");
        if (font.contains("-")){
            return (font.substring(font.lastIndexOf("-")+1));
        }else if (font.contains(",")){
            return (font.substring(font.lastIndexOf(",")+1));
        }

        return "regular";
    }

    public int getFontSize() {
        return Math.round(positions.get(0).getFontSize());
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
                ", font='" + getFontName() + '\'' +
                ", style='" + getFontStyle() + '\'' +
                ", size=" + getFontSize() +
                ", operations=" + operations +
                '}';
    }
}
