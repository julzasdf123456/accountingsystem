package com.boheco1.dev.integratedaccountingsystem.helpers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class ExcelBuilder {

    private String header;
    private String address;
    private String title;

    private Workbook wb;
    private Sheet sheet;

    private int wide;

    public static final int DOC_SHORT = 25;
    public static final int LONG_SHORT = 35;

    public ExcelBuilder(String header, String address, String title, int wide){
        this.wb = new XSSFWorkbook();
        this.sheet = this.wb.createSheet("Sheet");
        this.header = header;
        this.address = address;
        this.title = title;
        this.wide = wide;
    }

    public void setMargin(double top, double right, double bottom, double left){
        sheet.setMargin(Sheet.RightMargin, right);
        sheet.setMargin(Sheet.LeftMargin, left);
        sheet.setMargin(Sheet.TopMargin, top);
        sheet.setMargin(Sheet.BottomMargin, bottom);
    }

    public void createHeader(){
        Row header = sheet.createRow(0);
        Cell header_cell = header.createCell(0);
        header_cell.setCellValue(this.header);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0, this.wide - 1));
        Font header_font = wb.createFont();
        header_font.setFontHeightInPoints((short)12);
        header_font.setFontName("Arial");
        header_font.setBold(true);
        CellStyle header_style = wb.createCellStyle();
        header_style.setFont(header_font);
        header_style.setAlignment(HorizontalAlignment.CENTER);
        header_cell.setCellStyle(header_style);

        Row address = sheet.createRow(1);
        Cell address_cell = address.createCell(0);
        address_cell.setCellValue(this.address);
        sheet.addMergedRegion(new CellRangeAddress(1,1,0, this.wide - 1));
        Font address_font = wb.createFont();
        address_font.setFontHeightInPoints((short)12);
        address_font.setFontName("Arial");
        CellStyle address_style = wb.createCellStyle();
        address_style.setAlignment(HorizontalAlignment.CENTER);
        address_cell.setCellStyle(address_style);
    }

    public void createTitle(int row, String subtitle){
        Row title = sheet.createRow(row);
        Cell title_cell = title.createCell(0);
        title_cell.setCellValue(this.title.toUpperCase(Locale.ROOT));
        sheet.addMergedRegion(new CellRangeAddress(row, row,0, this.wide - 1));
        Font title_font = wb.createFont();
        title_font.setFontHeightInPoints((short)14);
        title_font.setFontName("Arial");
        title_font.setBold(true);
        CellStyle title_style = wb.createCellStyle();
        title_style.setFont(title_font);
        title_style.setAlignment(HorizontalAlignment.CENTER);
        title_cell.setCellStyle(title_style);

        if (subtitle != null){
            Row subrow = sheet.createRow(row+1);
            Cell subtitle_cell = subrow.createCell(0);
            subtitle_cell.setCellValue(subtitle);
            sheet.addMergedRegion(new CellRangeAddress(row+1, row+1,0, this.wide - 1));
            Font subtitle_font = wb.createFont();
            subtitle_font.setFontHeightInPoints((short)12);
            subtitle_font.setFontName("Arial");
            CellStyle subtitle_style = wb.createCellStyle();
            subtitle_style.setFont(subtitle_font);
            subtitle_style.setAlignment(HorizontalAlignment.CENTER);
            subtitle_cell.setCellStyle(subtitle_style);
        }
    }

    public void styleLeftBorder(Cell cell, Font font, HorizontalAlignment alignment){
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(alignment);
        /*style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());*/
        cell.setCellStyle(style);
    }

    public void styleMergedCells(CellRangeAddress address){
        RegionUtil.setBorderTop(BorderStyle.THIN, address, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, address, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, address, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, address, sheet);
    }

    public void styleBorder(Cell cell, int fontSize, HorizontalAlignment alignment, boolean wrap){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)fontSize);
        font.setFontName("Arial");
        style.setFont(font);
        style.setAlignment(alignment);
        style.setWrapText(wrap);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cell.setCellStyle(style);
    }

    public void styleRightBorder(Cell cell, Font font, HorizontalAlignment alignment){
        XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(alignment);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cell.setCellStyle(style);
    }

    public void createSignatorees(int row, String names[], String designations[]){

        int width = (this.wide/2);

        Font font_11 = wb.createFont();
        font_11.setFontHeightInPoints((short)11);
        font_11.setFontName("Arial");

        Font font_12 = wb.createFont();
        font_12.setFontHeightInPoints((short)12);
        font_12.setBold(true);
        font_12.setFontName("Arial");

        //Prepared and Recommended
        Row row1 = sheet.createRow(row);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("Prepared by:");
        sheet.addMergedRegion(new CellRangeAddress(row, row,0, width - 1));
        this.styleLeftBorder(cell1, font_11, HorizontalAlignment.CENTER);

        Cell cell2 = row1.createCell(width);
        cell2.setCellValue("Recommended by:");
        sheet.addMergedRegion(new CellRangeAddress(row, row, width,this.wide - 1));
        this.styleRightBorder(cell2, font_11, HorizontalAlignment.CENTER);

        //Signatorees
        int second_row = row+2;
        Row row2 = sheet.createRow(second_row);
        Cell cell3 = row2.createCell(0);
        cell3.setCellValue(names[0].toUpperCase(Locale.ROOT));
        sheet.addMergedRegion(new CellRangeAddress(second_row, second_row,0, width - 1));
        this.styleRightBorder(cell3, font_12, HorizontalAlignment.CENTER);


        Cell cell4 = row2.createCell(width);
        cell4.setCellValue(names[1].toUpperCase(Locale.ROOT));
        sheet.addMergedRegion(new CellRangeAddress(second_row, second_row, width,this.wide - 1));
        this.styleRightBorder(cell4, font_12, HorizontalAlignment.CENTER);

        //Designation of signatorees
        int third_row = row+3;
        Row row3 = sheet.createRow(third_row);
        Cell cell5 = row3.createCell(0);
        cell5.setCellValue(designations[0]);
        sheet.addMergedRegion(new CellRangeAddress(third_row, third_row,0, width - 1));
        this.styleLeftBorder(cell5, font_11, HorizontalAlignment.CENTER);

        Cell cell6 = row3.createCell(width);
        cell6.setCellValue(designations[1]);
        sheet.addMergedRegion(new CellRangeAddress(third_row, third_row, width,this.wide - 1));
        this.styleLeftBorder(cell6, font_11, HorizontalAlignment.CENTER);

        //Approved
        int fourth_row = row + 6;
        Row row4 = sheet.createRow(fourth_row);
        Cell cell7 = row4.createCell(0);
        cell7.setCellValue("Approved:");
        sheet.addMergedRegion(new CellRangeAddress(fourth_row, fourth_row,0, this.wide - 1));
        this.styleRightBorder(cell7, font_11, HorizontalAlignment.CENTER);


        //Last signatoree name
        int fifth_row = row + 8;
        Row row5 = sheet.createRow(fifth_row);
        Cell cell8 = row5.createCell(0);
        cell8.setCellValue(names[2]);
        sheet.addMergedRegion(new CellRangeAddress(fifth_row, fifth_row,0, this.wide - 1));
        this.styleRightBorder(cell8, font_12, HorizontalAlignment.CENTER);

        int sixth_row = row + 9;
        Row row6 = sheet.createRow(sixth_row);
        Cell cell9 = row6.createCell(0);
        cell9.setCellValue(designations[2]);
        sheet.addMergedRegion(new CellRangeAddress(sixth_row, sixth_row,0, this.wide - 1));
        this.styleRightBorder(cell9, font_11, HorizontalAlignment.CENTER);
    }

    public void save(OutputStream file) throws IOException {
        this.wb.write(file);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Workbook getWb() {
        return wb;
    }

    public void setWb(Workbook wb) {
        this.wb = wb;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public int getWide() {
        return wide;
    }

    public void setWide(int wide) {
        this.wide = wide;
    }
}