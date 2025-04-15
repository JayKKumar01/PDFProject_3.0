package pdfproject.models;

public class InputData {
    private final String path1;
    private final String path2;
    private final String range1;
    private final String range2;
    private boolean isSingleColumn = true;

    public InputData(String path1, String path2, String range1, String range2) {
        this.path1 = path1;
        this.path2 = path2;
        this.range1 = range1;
        this.range2 = range2;
    }

    public String getPath1() {
        return path1;
    }

    public String getPath2() {
        return path2;
    }

    public String getRange1() {
        return range1;
    }

    public String getRange2() {
        return range2;
    }

    public boolean isSingleColumn() {
        return isSingleColumn;
    }

    public void setSingleColumn(boolean singleColumn) {
        this.isSingleColumn = singleColumn;
    }
}
