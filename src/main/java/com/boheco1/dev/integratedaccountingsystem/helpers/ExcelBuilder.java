package com.boheco1.dev.integratedaccountingsystem.helpers;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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

    public void createHeader(){
        Row header = sheet.createRow(0);
        Cell header_cell = header.createCell(0);
        header_cell.setCellValue(this.header);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0, (this.wide * 2) - 1));
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

    public void createSignatorees(int row, String names[], String designations[]){
        int width = (this.wide/2);
        //Prepared and Recommended
        Row row1 = sheet.createRow(row);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("Prepared by:");
        sheet.addMergedRegion(new CellRangeAddress(row, row,0, width - 1));
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)11);
        font.setFontName("Arial");
        CellStyle style_left = wb.createCellStyle();
        style_left.setFont(font);
        style_left.setAlignment(HorizontalAlignment.CENTER);
        style_left.setBorderLeft(BorderStyle.THIN);
        style_left.setLeftBorderColor(IndexedColors.BLUE.getIndex());
        cell1.setCellStyle(style_left);

        Cell cell2 = row1.createCell(width);
        cell2.setCellValue("Recommended by:");
        sheet.addMergedRegion(new CellRangeAddress(row, row, width,this.wide - 1));
        Font font2 = wb.createFont();
        font2.setFontHeightInPoints((short)11);
        font2.setFontName("Arial");
        CellStyle style_right = wb.createCellStyle();
        style_right.setFont(font);
        style_right.setAlignment(HorizontalAlignment.CENTER);
        style_right.setBorderRight(BorderStyle.THIN);
        style_right.setRightBorderColor(IndexedColors.BLUE.getIndex());
        cell2.setCellStyle(style_right);

        //Signatorees
        int second_row = row+2;
        Row row2 = sheet.createRow(second_row);
        Cell cell3 = row2.createCell(0);
        cell3.setCellValue(names[0].toUpperCase(Locale.ROOT));
        sheet.addMergedRegion(new CellRangeAddress(second_row, second_row,0, width - 1));
        Font font3 = wb.createFont();
        font3.setBold(true);
        font3.setFontHeightInPoints((short)12);
        font3.setFontName("Arial");
        CellStyle style_left1 = wb.createCellStyle();
        style_left1.setFont(font3);
        style_left1.setAlignment(HorizontalAlignment.CENTER);
        style_left1.setBorderLeft(BorderStyle.THIN);
        style_left1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cell3.setCellStyle(style_left1);

        Cell cell4 = row2.createCell(width);
        cell4.setCellValue(names[1].toUpperCase(Locale.ROOT));
        sheet.addMergedRegion(new CellRangeAddress(second_row, second_row, width,this.wide - 1));
        Font font4 = wb.createFont();
        font4.setBold(true);
        font4.setFontHeightInPoints((short)12);
        font4.setFontName("Arial");
        CellStyle style_right1 = wb.createCellStyle();
        style_right1.setFont(font4);
        style_right1.setAlignment(HorizontalAlignment.CENTER);
        style_right1.setBorderRight(BorderStyle.THIN);
        style_right1.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cell4.setCellStyle(style_right1);

        //Designation of signatorees
        int third_row = row+3;
        Row row3 = sheet.createRow(third_row);
        Cell cell5 = row3.createCell(0);
        cell5.setCellValue(designations[0]);
        sheet.addMergedRegion(new CellRangeAddress(third_row, third_row,0, width - 1));
        Font font5 = wb.createFont();
        font5.setFontHeightInPoints((short)11);
        font5.setFontName("Arial");
        CellStyle style_left2 = wb.createCellStyle();
        style_left2.setFont(font5);
        style_left2.setAlignment(HorizontalAlignment.CENTER);
        style_left2.setBorderLeft(BorderStyle.THIN);
        style_left2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cell5.setCellStyle(style_left2);

        Cell cell6 = row3.createCell(width);
        cell6.setCellValue(designations[1]);
        sheet.addMergedRegion(new CellRangeAddress(third_row, third_row, width,this.wide - 1));
        Font font6 = wb.createFont();
        font6.setFontHeightInPoints((short)11);
        font6.setFontName("Arial");
        CellStyle style_right2 = wb.createCellStyle();
        style_right2.setFont(font6);
        style_right2.setAlignment(HorizontalAlignment.CENTER);
        style_right2.setBorderRight(BorderStyle.THIN);
        style_right2.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cell6.setCellStyle(style_right2);

        //Last signatoree
        int fourth_row = row + 6;
        Row row4 = sheet.createRow(fourth_row);
        Cell cell7 = row4.createCell(0);
        cell7.setCellValue(names[2]);
        sheet.addMergedRegion(new CellRangeAddress(fourth_row, fourth_row,0, this.wide - 1));
        Font font7 = wb.createFont();
        font7.setBold(true);
        font7.setFontHeightInPoints((short)12);
        font7.setFontName("Arial");
        CellStyle style_both = wb.createCellStyle();
        style_both.setFont(font7);
        style_both.setAlignment(HorizontalAlignment.CENTER);
        style_both.setBorderRight(BorderStyle.THIN);
        style_both.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style_both.setBorderLeft(BorderStyle.THIN);
        style_both.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cell7.setCellStyle(style_both);

        //Last signatoree
        int fifth_row = row + 7;
        Row row5 = sheet.createRow(fifth_row);
        Cell cell8 = row5.createCell(0);
        cell8.setCellValue(designations[2]);
        sheet.addMergedRegion(new CellRangeAddress(fifth_row, fifth_row,0, this.wide - 1));
        Font font8 = wb.createFont();
        font8.setFontHeightInPoints((short)11);
        font8.setFontName("Arial");
        CellStyle style_both1 = wb.createCellStyle();
        style_both1.setFont(font8);
        style_both1.setAlignment(HorizontalAlignment.CENTER);
        style_both1.setBorderRight(BorderStyle.THIN);
        style_both1.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style_both1.setBorderLeft(BorderStyle.THIN);
        style_both1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cell8.setCellStyle(style_both1);
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
