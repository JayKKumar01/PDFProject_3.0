package pdfproject.models;

import java.util.ArrayList;
import java.util.List;

public class MapModel {
    private final List<List<String>> alignmentImages;
    private final List<List<String>> contentImages;

    public MapModel() {
        this.alignmentImages = new ArrayList<>();
        this.contentImages = new ArrayList<>();
    }

    public void addAlignmentRow(List<String> row) {
        alignmentImages.add(row);
    }

    public void addContentRow(List<String> row) {
        contentImages.add(row);
    }

    public List<List<String>> getAlignmentImages() {
        return alignmentImages;
    }

    public List<List<String>> getContentImages() {
        return contentImages;
    }
}
