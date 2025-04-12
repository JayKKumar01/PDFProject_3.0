package pdfproject.models;

import java.util.ArrayList;
import java.util.List;

public class MapModel {
    private final List<List<String>> alignmentImages;
    private final List<List<String>> validationImages;

    public MapModel() {
        this.alignmentImages = new ArrayList<>();
        this.validationImages = new ArrayList<>();
    }

    public void addAlignmentRow(List<String> row) {
        alignmentImages.add(row);
    }

    public void addValidationRow(List<String> row) {
        validationImages.add(row);
    }

    public List<List<String>> getAlignmentImages() {
        return alignmentImages;
    }

    public List<List<String>> getValidationImages() {
        return validationImages;
    }
}
