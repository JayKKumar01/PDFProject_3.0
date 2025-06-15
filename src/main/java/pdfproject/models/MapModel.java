package pdfproject.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapModel {
    private final List<List<String>> alignmentImages;
    private final List<List<String>> contentImages;
    private final String outputImagePath;
    private String key;

    public MapModel(String outputImagePath) {
        this.outputImagePath = outputImagePath.replace("\\", "/");
        this.alignmentImages = Collections.synchronizedList(new ArrayList<>());
        this.contentImages = Collections.synchronizedList(new ArrayList<>());
    }

    public void addAlignmentRow(List<String> row, int index) {
        List<String> trimmedRow = trimAndQuotePaths(row);
        synchronized (alignmentImages) {
            ensureCapacity(alignmentImages, index);
            alignmentImages.set(index, trimmedRow);
        }
    }

    public void addContentRow(List<String> row, int index) {
        List<String> trimmedRow = trimAndQuotePaths(row);
        synchronized (contentImages) {
            ensureCapacity(contentImages, index);
            contentImages.set(index, trimmedRow);
        }
    }

    private void ensureCapacity(List<List<String>> list, int index) {
        while (list.size() <= index) {
            list.add(null);
        }
    }

    private List<String> trimAndQuotePaths(List<String> row) {
        return row.stream()
                .map(path -> {
                    if (path == null) return null;
                    return "\"" + path.replace("\\", "/")
                            .substring(outputImagePath.length() + 1) + "\"";
                })
                .collect(Collectors.toList());
    }

    public List<List<String>> getAlignmentImages() {
        return alignmentImages;
    }

    public List<List<String>> getContentImages() {
        return contentImages;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
