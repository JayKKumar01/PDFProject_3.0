package pdfproject.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pdfproject.Config;
import pdfproject.models.InputData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class responsible for reading input data from an Excel sheet
 * and converting each valid row into an {@link InputData} model.
 * <p>
 * Expected Excel format (starting from row index 1):
 * <ul>
 *     <li>Column 0: Optional key</li>
 *     <li>Column 1: Path to first file</li>
 *     <li>Column 2: Path to second file</li>
 *     <li>Column 3: Page range for first file (e.g., "1-3" or "5")</li>
 *     <li>Column 4: Page range for second file</li>
 *     <li>Column 5: Multi-column flag ("Yes" for multi-column)</li>
 * </ul>
 */
public class InputDataProvider {

    private static final DataFormatter FORMATTER = new DataFormatter();

    /**
     * Loads input data from the Excel file defined in {@link Config#INPUT_PATH}.
     *
     * @return a list of {@link InputData} entries, or null if the file is unreadable
     */
    public static List<InputData> load() {
        Iterator<Row> rowIterator = getRowIterator();
        if (rowIterator == null) return null;

        List<InputData> inputList = new ArrayList<>();

        // Skip header row
        if (rowIterator.hasNext()) rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // Read required columns
            String path1 = getCellValue(row.getCell(1));
            String path2 = getCellValue(row.getCell(2));

            if (path1 == null || path2 == null) continue;

            InputData input = new InputData(
                    path1,
                    path2,
                    getCellValue(row.getCell(3)), // range1
                    getCellValue(row.getCell(4))  // range2
            );

            // Optional: Key column
            input.setKey(getCellValue(row.getCell(0)));

            // Optional: Multi-column layout flag
            String layoutFlag = getCellValue(row.getCell(5));
            if (layoutFlag != null && !layoutFlag.isEmpty()) {
                input.setSingleColumn(!layoutFlag.equalsIgnoreCase("yes"));
            }

            inputList.add(input);
        }

        return inputList;
    }

    /**
     * Returns the trimmed string value of a cell, or null if empty.
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return null;
        String value = FORMATTER.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    /**
     * Opens the Excel file at {@link Config#INPUT_PATH} and returns an iterator
     * over the rows of the first sheet.
     *
     * @return Row iterator, or null if file cannot be read
     */
    private static Iterator<Row> getRowIterator() {
        if (Config.INPUT_PATH == null || Config.INPUT_PATH.isEmpty()) return null;

        try (FileInputStream fis = new FileInputStream(Config.INPUT_PATH);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            return workbook.getSheetAt(0).rowIterator();

        } catch (IOException e) {
            System.err.println("❌ Failed to read Excel input file: " + e.getMessage());
            return null;
        }
    }
}
