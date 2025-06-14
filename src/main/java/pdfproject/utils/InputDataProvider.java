package pdfproject.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pdfproject.Config;
import pdfproject.constants.ConsoleMessages;
import pdfproject.constants.StringConstants;
import pdfproject.models.InputData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

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
    private static final Pattern RANGE_PATTERN = Pattern.compile("\\d+-\\d+"); // e.g., "1-3"
    private static final Pattern SINGLE_PAGE_PATTERN = Pattern.compile("\\d+"); // e.g., "5"

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
                input.setSingleColumn(!layoutFlag.equalsIgnoreCase(StringConstants.YES));
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
     * Returns true if the given cell does not contain a valid range string.
     * Accepts "n" or "n-m" where n <= m.
     */
    private static boolean isInvalidRange(Cell rangeCell) {
        String val = getCellValue(rangeCell);
        if (val == null) return true;

        if (RANGE_PATTERN.matcher(val).matches()) {
            String[] parts = val.split("-");
            return Integer.parseInt(parts[0]) > Integer.parseInt(parts[1]);
        }

        return !SINGLE_PAGE_PATTERN.matcher(val).matches();
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
            System.err.println(ConsoleMessages.EXCEL_READ_ERROR + e.getMessage());
            return null;
        }
    }
}
