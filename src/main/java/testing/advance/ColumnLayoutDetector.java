package testing.advance;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ColumnLayoutDetector extends PDFTextStripper {

    private final Map<Integer, List<TextPosition>> pageTextMap = new HashMap<>();

    public ColumnLayoutDetector() throws IOException {
        super.setSortByPosition(false); // Do NOT sort by position
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        pageTextMap.computeIfAbsent(getCurrentPageNo(), k -> new ArrayList<>()).add(text);
    }

    public void analyze(String pdfPath, String outPath) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(pdfPath))) {
            setStartPage(1);
            setEndPage(doc.getNumberOfPages());
            getText(doc); // Fills pageTextMap

            for (int pageNum = 1; pageNum <= doc.getNumberOfPages(); pageNum++) {
                System.out.println("\n--- Page " + pageNum + " ---");

                List<TextPosition> textPositions = pageTextMap.getOrDefault(pageNum, Collections.emptyList());
                List<Line> lines = groupLines(textPositions);
                List<ColumnBlock> blocks = analyzeAdaptiveColumns(lines);

                drawBlocks(doc, doc.getPage(pageNum - 1), blocks);
                printDebug(blocks);
            }

            doc.save(outPath);
        }
    }

    private List<Line> groupLines(List<TextPosition> textPositions) {
        List<Line> lines = new ArrayList<>();
        textPositions.sort(Comparator.comparing(TextPosition::getYDirAdj).reversed());
        float threshold = 2.0f;

        for (TextPosition tp : textPositions) {
            boolean added = false;
            for (Line line : lines) {
                if (Math.abs(line.y - tp.getYDirAdj()) < threshold) {
                    line.positions.add(tp);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Line line = new Line();
                line.y = tp.getYDirAdj();
                line.positions.add(tp);
                lines.add(line);
            }
        }

        return lines;
    }

    private List<ColumnBlock> analyzeAdaptiveColumns(List<Line> lines) {
        List<ColumnBlock> allBlocks = new ArrayList<>();
        int groupSize = 15;

        for (int i = 0; i < lines.size(); i += groupSize) {
            List<Line> segment = lines.subList(i, Math.min(i + groupSize, lines.size()));

            float avgGap = estimateAverageGap(segment);
            float dynamicGapThreshold = avgGap > 50 ? 80f : 20f;

            allBlocks.addAll(analyzeColumns(segment, dynamicGapThreshold));
        }

        return mergeBlocks(allBlocks);
    }

    private float estimateAverageGap(List<Line> lines) {
        List<Float> gaps = new ArrayList<>();

        for (Line line : lines) {
            line.positions.sort(Comparator.comparing(TextPosition::getXDirAdj));
            for (int i = 1; i < line.positions.size(); i++) {
                float prevRight = line.positions.get(i - 1).getXDirAdj() + line.positions.get(i - 1).getWidthDirAdj();
                float currLeft = line.positions.get(i).getXDirAdj();
                gaps.add(currLeft - prevRight);
            }
        }

        return (float) gaps.stream().filter(g -> g > 0).mapToDouble(f -> f).average().orElse(0);
    }

    private List<ColumnBlock> analyzeColumns(List<Line> lines, float gapThreshold) {
        List<ColumnBlock> blocks = new ArrayList<>();

        for (Line line : lines) {
            line.positions.sort(Comparator.comparing(TextPosition::getXDirAdj));
            List<List<TextPosition>> clusters = new ArrayList<>();
            List<TextPosition> currentCluster = new ArrayList<>();
            float lastX = -1;

            for (TextPosition tp : line.positions) {
                float x = tp.getXDirAdj();
                if (currentCluster.isEmpty() || x - lastX < gapThreshold) {
                    currentCluster.add(tp);
                } else {
                    clusters.add(new ArrayList<>(currentCluster));
                    currentCluster.clear();
                    currentCluster.add(tp);
                }
                lastX = tp.getXDirAdj() + tp.getWidthDirAdj();
            }
            if (!currentCluster.isEmpty()) {
                clusters.add(currentCluster);
            }

            for (List<TextPosition> cluster : clusters) {
                float minX = cluster.stream().map(TextPosition::getXDirAdj).min(Float::compare).orElse(0f);
                float maxX = cluster.stream().map(tp -> tp.getXDirAdj() + tp.getWidthDirAdj()).max(Float::compare).orElse(0f);
                float y = line.y;
                blocks.add(new ColumnBlock(minX, y, maxX - minX, "col"));
            }
        }

        return blocks;
    }

    private List<ColumnBlock> mergeBlocks(List<ColumnBlock> blocks) {
        blocks.sort(Comparator.comparing(cb -> -cb.yTop));
        List<ColumnBlock> merged = new ArrayList<>();
        float mergeYThreshold = 2.0f;
        float mergeXThreshold = 5.0f;

        for (ColumnBlock block : blocks) {
            boolean added = false;
            for (ColumnBlock prev : merged) {
                if (Math.abs(prev.x - block.x) < mergeXThreshold &&
                        Math.abs(prev.width - block.width) < mergeXThreshold &&
                        Math.abs(prev.yBottom - block.yTop) < mergeYThreshold) {
                    prev.yBottom = block.yBottom;
                    added = true;
                    break;
                }
            }
            if (!added) {
                merged.add(block.copy());
            }
        }

        return merged;
    }

    private void drawBlocks(PDDocument doc, PDPage page, List<ColumnBlock> blocks) throws IOException {
        float pageHeight = page.getMediaBox().getHeight();

        try (PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
            for (ColumnBlock block : blocks) {
                cs.setStrokingColor(0, 0, 255);
                cs.setLineWidth(1.0f);

                float flippedYBottom = pageHeight - block.yTop;
                float flippedHeight = block.yTop - block.yBottom;

                cs.addRect(block.x, flippedYBottom, block.width, flippedHeight);
                cs.stroke();
            }
        }
    }

    private void printDebug(List<ColumnBlock> blocks) {
        for (ColumnBlock block : blocks) {
            System.out.printf("Rect: [x=%.1f, yTop=%.1f, yBottom=%.1f, width=%.1f]\n",
                    block.x, block.yTop, block.yBottom, block.width);
        }
    }

    static class Line {
        float y;
        List<TextPosition> positions = new ArrayList<>();
    }

    static class ColumnBlock {
        float x, yTop, yBottom, width;
        String columnType;

        ColumnBlock(float x, float y, float width, String type) {
            this.x = x;
            this.width = width;
            this.yTop = y;
            this.yBottom = y - 1;
            this.columnType = type;
        }

        ColumnBlock copy() {
            ColumnBlock b = new ColumnBlock(x, yTop, width, columnType);
            b.yBottom = yBottom;
            return b;
        }
    }

    public static void main(String[] args) throws IOException {
        String basePath = "C:\\Users\\jayte\\Downloads\\samples\\";
        new ColumnLayoutDetector().analyze(basePath + "2col2tables.pdf", basePath + "2col2tables_updated.pdf");
    }
}