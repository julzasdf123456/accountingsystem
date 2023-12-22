package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.ORContent;
import com.boheco1.dev.integratedaccountingsystem.objects.ORItemSummary;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class PrintORFinance {
    private ORContent orContent;
    float[] column = {1f,2f,1f};
    PdfPTable table = new PdfPTable(column);
    PdfPCell cell;
    Document document;

    public PrintORFinance (ORContent orContent) {
        this.orContent = orContent;
        createDirectory();
        header();
        orInfo();
        orContent();
    }

    public void print() throws Exception {
        document = new Document(PageSize.LETTER,30f,30f,30f,30f);
        Paragraph preface = new Paragraph();
        PdfWriter.getInstance(document, new FileOutputStream("c:/prints/finance_OR.pdf"));
        document.open();
        table.getDefaultCell().setFixedHeight(80);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

        preface.setAlignment(1);
        preface.add(table);

        document.add(preface);
        document.setMarginMirroring(true);
        document.close();

        displayOutput();
    }


    private void createDirectory(){
        File theDir = new File("c:\\prints");
        if (!theDir.exists()){
            boolean result = theDir.mkdir();
            if(result){
                System.out.println("DIR created");
            }
        }else{
            System.out.println("Exist");
        }
    }
    private void header() {
        createCell("BOHOL 1 ELECTRIC COOPERATIVE, INC.", column.length, 12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Cabulijan, Tubigon, Bohol", column.length, 11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("TIN 000-534 418-000-NV", column.length, 11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
    }
    private void orInfo() {

        String amountInWord = Utility.doubleAmountToWords(orContent.getTotal()).replaceAll("£","");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");


        createCell("OR No ", 2,12,Font.NORMAL,Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
        createCell(orContent.getOrNumber(), 1,12,Font.NORMAL,Element.ALIGN_LEFT);

        createCell("Date ", 2,12,Font.NORMAL,Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
        createCell(orContent.getDate().format(dateFormat), 1,12,Font.NORMAL,Element.ALIGN_LEFT);

        createCell("Received From ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
        createCell(orContent.getIssuedBy(), 2,12,Font.NORMAL,Element.ALIGN_LEFT);
        createCell("Address ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
        createCell(orContent.getAddress(), 2,12,Font.NORMAL,Element.ALIGN_LEFT);
        createCell("Amount in Words ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
        createCell(amountInWord, 2,12,Font.NORMAL,Element.ALIGN_LEFT);

        createCell("In payment of ", 1,12,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);
        createCell("", 1,12,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);
        createCell(Utility.formatDecimal(orContent.getTotal()), 1,12,Font.NORMAL,Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
    }
    private void orContent() {
        createCell("REFERENCE", 1,12,Font.NORMAL,Element.ALIGN_CENTER);
        createCell("PARTICULARS", 1,12,Font.NORMAL,Element.ALIGN_CENTER);
        createCell("AMOUNT", 1,12,Font.NORMAL,Element.ALIGN_CENTER);

        ObservableList<ORItemSummary> orItemSummary = orContent.getTellerCollection();

        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);
        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);
        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);
        for(ORItemSummary ois : orItemSummary){
            createCell(ois.getAccountCode(), 1,12,Font.NORMAL,Element.ALIGN_LEFT,0f,0f);
            createCell(ois.getDescription(), 1,12,Font.NORMAL,Element.ALIGN_LEFT,0f,0f);
            createCell(Utility.formatDecimal(ois.getAmount()), 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);
        }
        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);
        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);
        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_RIGHT,0f,0f);


        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_CENTER);
        createCell(" ", 1,12,Font.NORMAL,Element.ALIGN_CENTER);
        createCell(Utility.formatDecimal(orContent.getTotal()), 1,12,Font.NORMAL,Element.ALIGN_RIGHT);

        createCell("Cash", 1,11,Font.NORMAL,Element.ALIGN_CENTER);
        createCell(" ", 2,11,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);

        createCell("P "+Utility.formatDecimal(orContent.getTotal()), 1,11,Font.NORMAL,Element.ALIGN_CENTER);
        createCell(" ", 2,11,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);

        createCell("Check No.", 1,11,Font.NORMAL,Element.ALIGN_CENTER);
        createCell(" ", 2,11,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);

        createCell(orContent.getCheckNo(), 1,11,Font.NORMAL,Element.ALIGN_LEFT);
        createCell(" ", 2,11,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);

        createCell(" ", 1,11,Font.NORMAL,Element.ALIGN_LEFT);
        createCell(" ", 2,11,Font.NORMAL,Element.ALIGN_LEFT,Rectangle.NO_BORDER);

    }

    private void displayOutput()throws Exception {
        try {

            if ((new File("c:\\prints\\finance_OR.pdf")).exists()) {
                Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler c:\\prints\\finance_OR.pdf");
                p.waitFor();
            } else {
                System.out.println("File does not exists");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createCell(int loop, int span){
        for(int x=1;x<=loop;x++){
            cell=new PdfPCell(new Paragraph(" ",new Font(Font.FontFamily.TIMES_ROMAN,8,Font.BOLD)));
            cell.setColspan(span);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
    }

    private void createCell(String text, int span, int size, int font, int alignment, float top, float bottom){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidthTop(top);
        cell.setBorderWidthBottom(bottom);
        table.addCell(cell);
    }

    private void createCell(String text, int span, int size, int font, int alignment, int border){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(border);
        table.addCell(cell);
    }

    private void createCell(String text, int span, int size, int font, int alignment){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }
}
