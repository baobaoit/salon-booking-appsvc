package com.hesmantech.salonbooking.utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class ExcelUtils {
    private ExcelUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Applies a given CellStyle to all cells in a row.
     * This method iterates through each cell in the provided Row object
     * and applies the specified CellStyle. If a cell in the row is missing,
     * it creates a new cell with a blank value and then applies the style.
     *
     * @param row   the Row object to which the style will be applied
     * @param style the CellStyle to apply to each cell in the row
     * @author OpenAI
     */
    public static void applyStyleToRow(Row row, CellStyle style) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            var cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellStyle(style);
        }
    }

    /**
     * Adjusts the width of a single column in a sheet based on the content.
     * This method calculates the maximum width required by the cells in a column
     * and sets the column width accordingly. It takes into account the font size
     * used in the cells and applies an adjustment factor to ensure the content fits well.
     *
     * @param sheet       the Sheet object whose column width is to be adjusted
     * @param columnIndex the index of the column whose width needs to be adjusted
     * @author OpenAI
     */
    public static void adjustColumnWidth(Sheet sheet, int columnIndex) {
        var workbook = sheet.getWorkbook();
        var font = workbook.getFontAt(sheet.getColumnStyle(columnIndex).getFontIndex());

        // Assuming the default font is Calibri and using the maximum digit width of 11 point font size which is 7 pixels
        final int maxDigitWidth = 7; // This is an approximation and might need adjustment

        double maxWidth = 0;
        for (Row row : sheet) {
            var cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                var cellValue = cell.toString();
                // Approximate width calculation based on the font size
                double cellWidth = ((double) (cellValue.length() * font.getFontHeightInPoints()) / maxDigitWidth) * 256;
                maxWidth = Math.max(maxWidth, cellWidth);
            }
        }

        if (maxWidth > 0) {
            // Set the width with a little extra padding
            sheet.setColumnWidth(columnIndex, (int) (maxWidth * 1.14388)); // The magic number 1.14388 is an adjustment factor
        }
    }
}
