package pdfproject.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pdfproject.Config;
import pdfproject.models.InputData;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class responsible for reading input data from an Excel sheet
 * and converting each valid row into an {@link InputData} model.
 */
public class InputDataProvider {

    private static final Logger LOGGER = Logger.getLogger(InputDataProvider.class.getName());

    /**
     * Loads input data from the Excel file defined in {@link Config#INPUT_PATH}.
     *
     * @return a list of {@link InputData} entries (empty list if none or on error)
     */
    public static List<InputData> load() {
        if (Config.INPUT_PATH == null || Config.INPUT_PATH.isEmpty()) {
            LOGGER.warning("Config.INPUT_PATH is null or empty.");
            return new ArrayList<>();
        }

        List<InputData> inputList = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(Config.INPUT_PATH);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Iterator<Row> rowIterator = workbook.getSheetAt(0).rowIterator();

            // skip header row if present
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Read required columns
                String path1 = getCellValue(formatter, row.getCell(1));
                String path2 = getCellValue(formatter, row.getCell(2));

                // skip rows missing required paths
                if (path1 == null || path2 == null) {
                    LOGGER.finer(() -> "Skipping row " + row.getRowNum() + " due to missing path(s).");
                    continue;
                }

                // Optional: validate paths exist on disk (uncomment if desired)
                // if (!Files.exists(Path.of(path1)) || !Files.exists(Path.of(path2))) {
                //     LOGGER.warning("One or both files referenced in row " + row.getRowNum() + " do not exist.");
                //     // decide: continue or still add — here we continue
                //     continue;
                // }

                InputData input = new InputData(
                        path1,
                        path2,
                        getCellValue(formatter, row.getCell(3)), // range1
                        getCellValue(formatter, row.getCell(4))  // range2
                );

                input.setKey(getCellValue(formatter, row.getCell(0)));

                String layoutFlag = getCellValue(formatter, row.getCell(5));
                if (layoutFlag != null && !layoutFlag.isEmpty()) {
                    input.setSingleColumn(!layoutFlag.equalsIgnoreCase("yes"));
                }

                inputList.add(input);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read Excel input file: " + Config.INPUT_PATH, e);
            // return empty list on failure; alternatively rethrow as runtime or custom exception
        }

        return inputList;
    }

    private static String getCellValue(DataFormatter formatter, Cell cell) {
        if (cell == null) return null;
        String value = formatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }
}
