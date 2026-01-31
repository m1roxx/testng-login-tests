package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public static Object[][] readSheet(String resourcePath, String sheetName) throws IOException {
        try (InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IOException("Sheet not found: " + sheetName);
                }
                return sheetToData(sheet);
            }
        }
    }

    private static Object[][] sheetToData(Sheet sheet) {
        List<Object[]> rows = new ArrayList<>();
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();
        int columnCount = -1;

        for (int r = firstRow; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            if (r == firstRow) {
                columnCount = row.getLastCellNum();
                continue;
            }
            Object[] cellValues = new Object[columnCount > 0 ? columnCount : row.getLastCellNum()];
            for (int c = 0; c < cellValues.length; c++) {
                Cell cell = row.getCell(c);
                cellValues[c] = getCellValueAsString(cell);
            }
            rows.add(cellValues);
        }

        return rows.toArray(new Object[0][]);
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultType();
        }
        return switch (type) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                yield value == (long) value ? String.valueOf((long) value) : String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> "";
            default -> "";
        };
    }
}
