package pdfproject.window.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class InputExcelGenerator {

    public static void main(String[] args) {
        // ✅ Easily change these paths
        String path1 = "C:\\Users\\jayte\\Downloads\\PDFProject\\ForestStory_V1.pdf";
        String path2 = "C:\\Users\\jayte\\Downloads\\PDFProject\\ForestStory_V2.pdf";

        // Optional: Set range and layout
        String range1 = "1-2";
        String range2 = "1-2";
        String isMultiColumn = "no";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("InputData");

        // Header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("path1");
        header.createCell(1).setCellValue("path2");
        header.createCell(2).setCellValue("range1");
        header.createCell(3).setCellValue("range2");
        header.createCell(4).setCellValue("isMultiColumn");

        // Data row
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(path1);
        row.createCell(1).setCellValue(path2);
        row.createCell(2).setCellValue(range1);
        row.createCell(3).setCellValue(range2);
        row.createCell(4).setCellValue(isMultiColumn);

        // Autosize columns
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save to file
        try (FileOutputStream fileOut = new FileOutputStream("input-data.xlsx")) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("Excel file created: input-data.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
