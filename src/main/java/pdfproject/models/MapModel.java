package pdfproject.models;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapModel {
    private final List<List<String>> alignmentImages;
    private final List<List<String>> contentImages;
    private final List<List<Pair<String,String>>> sourceTexts;
    private final List<List<Pair<String,String>>> targetTexts;
    private final String outputImagePath;
    private String key;

    public MapModel(String outputImagePath) {
        this.outputImagePath = outputImagePath.replace("\\", "/");
        this.alignmentImages = Collections.synchronizedList(new ArrayList<>());
        this.contentImages = Collections.synchronizedList(new ArrayList<>());
        this.sourceTexts = Collections.synchronizedList(new ArrayList<>());
        this.targetTexts = Collections.synchronizedList(new ArrayList<>());
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

    public void addListOfPairs(List<Pair<String,String>> pairList, boolean isSource) {
        if (pairList == null) return;

        // Convert Pair<String,String> → List<Pair<String,String>>
        List<Pair<String,String>> copyList = new ArrayList<>(pairList);

        if (isSource) {
            synchronized (sourceTexts) {
                sourceTexts.add(copyList);
            }
        } else {
            synchronized (targetTexts) {
                targetTexts.add(copyList);
            }
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

    public List<List<Pair<String, String>>> getSourceTexts() {
        return sourceTexts;
    }

    public List<List<Pair<String, String>>> getTargetTexts() {
        return targetTexts;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
