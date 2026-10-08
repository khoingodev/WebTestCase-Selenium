package com.automation.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelReader - Đọc dữ liệu test từ file .xlsx bằng Apache POI.
 * Dòng đầu tiên (sau tiêu đề) được coi là header.
 * Trả về List<Map<String, String>>: mỗi Map là 1 dòng dữ liệu.
 */
public class ExcelReader {

    /**
     * Đọc toàn bộ dữ liệu từ 1 sheet trong file xlsx trên classpath.
     *
     * @param fileName   tên file (vd: "Test_Cases_Dang_Nhap.xlsx") - nằm trong src/test/resources hoặc src/main/resources
     * @param sheetName  tên sheet cần đọc
     * @return danh sách các dòng, mỗi dòng là Map<header, value>
     */
    public static List<Map<String, String>> readTestData(String fileName, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = ExcelReader.class
                .getClassLoader()
                .getResourceAsStream("testdata/" + fileName);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Khong tim thay sheet '" + sheetName + "' trong file " + fileName);
            }

            // Tìm dòng header: dòng đầu tiên có các ô không rỗng chứa "STT"
            int headerRowIdx = findHeaderRow(sheet);
            if (headerRowIdx < 0) {
                throw new RuntimeException("Khong tim thay dong header chua 'STT' trong sheet " + sheetName);
            }

            Row headerRow = sheet.getRow(headerRowIdx);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(formatter.formatCellValue(cell).trim());
            }

            // Đọc các dòng dữ liệu từ headerRow + 1
            for (int i = headerRowIdx + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // Bỏ qua dòng trống
                boolean isEmpty = true;
                for (Cell cell : row) {
                    if (cell != null && !formatter.formatCellValue(cell).trim().isEmpty()) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty) {
                    continue;
                }

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headers.get(j), formatter.formatCellValue(cell).trim());
                }
                data.add(rowData);
            }

        } catch (IOException e) {
            throw new RuntimeException("Loi doc file Excel: " + e.getMessage(), e);
        }
        return data;
    }

    private static int findHeaderRow(Sheet sheet) {
        DataFormatter formatter = new DataFormatter();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell firstCell = row.getCell(0);
            if (firstCell != null && "STT".equalsIgnoreCase(formatter.formatCellValue(firstCell).trim())) {
                return i;
            }
        }
        return -1;
    }
}
