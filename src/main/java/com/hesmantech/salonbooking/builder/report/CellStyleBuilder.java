package com.hesmantech.salonbooking.builder.report;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import static com.hesmantech.salonbooking.constants.ReportConstants.TIMES_NEW_ROMAN_FONT_NAME;

public class CellStyleBuilder {
    private final Workbook workbook;
    private BorderStyle borderStyle;
    private IndexedColors borderColor;
    private boolean useFontBold;
    private short fontHeightInPoints;
    private Color fillForegroundColor;
    private FillPatternType fillPatternType;
    private String fontName;

    protected CellStyleBuilder(Workbook workbook) {
        this.workbook = workbook;
        this.borderStyle = BorderStyle.THIN;
        this.borderColor = IndexedColors.BLACK;
        this.useFontBold = false;
        this.fontHeightInPoints = (short) 13;
        this.fillForegroundColor = new XSSFColor(java.awt.Color.WHITE, null);
        this.fillPatternType = FillPatternType.SOLID_FOREGROUND;
        this.fontName = TIMES_NEW_ROMAN_FONT_NAME;
    }

    public static CellStyleBuilder of(Workbook workbook) {
        return new CellStyleBuilder(workbook);
    }

    public static CellStyle ofDefault(Workbook workbook) {
        return new CellStyleBuilder(workbook).build();
    }

    public CellStyleBuilder withBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
        return this;
    }

    public CellStyleBuilder withBorderColor(IndexedColors borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public CellStyleBuilder withUseFontBold(boolean useFontBold) {
        this.useFontBold = useFontBold;
        return this;
    }

    public CellStyleBuilder withFontHeightInPoints(short fontHeightInPoints) {
        this.fontHeightInPoints = fontHeightInPoints;
        return this;
    }

    public CellStyleBuilder withFillForegroundColor(java.awt.Color fillForegroundColor) {
        this.fillForegroundColor = new XSSFColor(fillForegroundColor, null);
        return this;
    }

    public CellStyleBuilder withFillForegroundColor(String hex) {
        this.fillForegroundColor = new XSSFColor(java.awt.Color.decode(hex), null);
        return this;
    }

    public CellStyleBuilder withFillPatternType(FillPatternType fillPatternType) {
        this.fillPatternType = fillPatternType;
        return this;
    }

    public CellStyleBuilder withFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public CellStyle build() {
        var style = workbook.createCellStyle();

        // Set border style
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);

        // Set border color
        var borderColorIndex = this.borderColor.getIndex();
        style.setTopBorderColor(borderColorIndex);
        style.setBottomBorderColor(borderColorIndex);
        style.setLeftBorderColor(borderColorIndex);
        style.setRightBorderColor(borderColorIndex);

        // Set font to bold
        var font = workbook.createFont();
        font.setBold(useFontBold);
        font.setFontHeightInPoints(fontHeightInPoints);
        font.setFontName(fontName);
        style.setFont(font);

        if (useFontBold) {
            // Set background color to dark cyan
            style.setFillForegroundColor(fillForegroundColor);
            style.setFillPattern(fillPatternType);
        }

        return style;
    }
}
