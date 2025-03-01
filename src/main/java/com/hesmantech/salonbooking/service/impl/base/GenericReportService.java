package com.hesmantech.salonbooking.service.impl.base;

import com.hesmantech.salonbooking.api.dto.ReportData;
import com.hesmantech.salonbooking.builder.report.CellStyleBuilder;
import com.hesmantech.salonbooking.exception.report.GenerateReportFailedException;
import com.hesmantech.salonbooking.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;

import static com.hesmantech.salonbooking.constants.ReportConstants.ARIAL_FONT_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.EXCEL_EXTENSION;

public abstract class GenericReportService {
    /**
     * Generates a report in Excel format with the specified data and styles.
     * This method creates an Excel workbook with a single sheet named as per the 'sheetName' parameter.
     * It applies a bold and bordered style to the column headers and a default bordered style to the data rows.
     * The method also auto-sizes the columns based on the content and writes the workbook to a ByteArrayOutputStream.
     *
     * @param <T>           the type of the data in the 'data' collection
     * @param fileName      the base name of the file to be generated, without the extension
     * @param sheetName     the name of the sheet to be created in the workbook
     * @param columnHeaders an array of strings representing the column headers
     * @param data          a collection of data items of type T to be written to the report
     * @param fillDataRows  a BiConsumer that takes a Row and a data item of type T and fills the row with the data item's details
     * @return a ReportData object containing the name of the generated file and the byte array of the written content
     * @throws GenerateReportFailedException if an I/O error occurs while writing the workbook to the output stream
     * @author Bao Le
     * @author OpenAI
     */
    protected <T> ReportData generateReport(String fileName, String sheetName, String[] columnHeaders,
                                            Collection<T> data,
                                            BiConsumer<Row, T> fillDataRows) {
        var outputStream = new ByteArrayOutputStream();
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet(sheetName);

            // Create a style for the borders
            var borderedBoldStyle = CellStyleBuilder.of(workbook)
                    .withUseFontBold(true)
                    .withFontHeightInPoints((short) 15)
                    .withFillForegroundColor(Color.CYAN)
                    .withFontName(ARIAL_FONT_NAME)
                    .build();

            var borderedStyle = CellStyleBuilder.ofDefault(workbook);

            // Create header row
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < columnHeaders.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
                cell.setCellStyle(borderedBoldStyle);
            }

            // Fill data rows
            var currentDataIndex = 0;
            for (T entry : data) {
                var dataRow = sheet.createRow(currentDataIndex + 1);
                fillDataRows.accept(dataRow, entry);
                ExcelUtils.applyStyleToRow(dataRow, borderedStyle);
                currentDataIndex++;
            }

            // Auto-size columns
            for (int i = 0; i < columnHeaders.length; i++) {
                ExcelUtils.adjustColumnWidth(sheet, i);
            }

            // Write the output to a byte array
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new GenerateReportFailedException(e);
        }

        return new ReportData(fileName + EXCEL_EXTENSION, outputStream.toByteArray());
    }
}
