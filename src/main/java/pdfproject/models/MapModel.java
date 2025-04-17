package pdfproject.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapModel {
    private final List<List<String>> alignmentImages;
    private final List<List<String>> contentImages;
    private final String outputImagePath;

    public MapModel(String outputImagePath) {
        // Normalize path separator to forward slash once
        this.outputImagePath = outputImagePath.replace("\\", "/");
        this.alignmentImages = new ArrayList<>();
        this.contentImages = new ArrayList<>();
    }

    public void addAlignmentRow(List<String> row) {
        alignmentImages.add(trimAndQuotePaths(row));
    }

    public void addContentRow(List<String> row) {
        contentImages.add(trimAndQuotePaths(row));
    }

    private List<String> trimAndQuotePaths(List<String> row) {
        return row.stream()
                .map(path -> "\"" + path.replace("\\", "/")
                        .substring(outputImagePath.length() + 1) + "\"")
                .collect(Collectors.toList());
    }

    public List<List<String>> getAlignmentImages() {
        return alignmentImages;
    }

    public List<List<String>> getContentImages() {
        return contentImages;
    }
}
