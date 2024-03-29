package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.ORContent;
import com.boheco1.dev.integratedaccountingsystem.objects.ORItemSummary;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PrintPDF {

    private float[] column = {1f, 1f, 1f, 1f, 1f, 1f};
    private PdfPTable table;

    private PdfPCell cell;
    private Document document;
    private File pdf;
    String headerText = "";
    String footerText = "";

    public PrintPDF(File pdf){
        this.pdf = pdf;
        this.table = new PdfPTable(column);
    }

    public PrintPDF(File pdf, float[] column){
        this.pdf = pdf;
        this.column = column;
        this.table = new PdfPTable(this.column);
    }

    public PrintPDF(File pdf, float[] column, String headerText, String footerText){
        this.pdf = pdf;
        this.column = column;
        this.table = new PdfPTable(this.column);
        this.headerText = headerText;
        this.footerText = footerText;
    }

    public void generateForOR(List<ORContent> items) throws Exception{

        document = new Document(PageSize.LETTER,10,10,60,10);
       // Paragraph preface = new Paragraph();
        PdfWriter.getInstance(document, new FileOutputStream(pdf));
        document.open();

        //preface.setAlignment(1);
        //preface.add(table);
        for (ORContent orContent : items){
            table = new PdfPTable(this.column);
            table.getDefaultCell().setFixedHeight(100);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
            float[] column = {.5f,2f,1f,.5f,.5f,2f,1f};
            PdfPTable itemTable = new PdfPTable(column);
            itemTable.getDefaultCell().setFixedHeight(100);
            itemTable.setWidthPercentage(100);
            itemTable.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

            //Date and OR
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable,formatter.format(orContent.getDate()),1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//date here
            createCell(itemTable,orContent.getOrNumber(),1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//OR number
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable,formatter.format(orContent.getDate()),1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);//date here
            createCell(itemTable,orContent.getOrNumber(),1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//OR number
            createCell(itemTable,1,column.length,1);

            //Description
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER,50f, Rectangle.NO_BORDER);
            //name, address, and amount in word add \n in each item
            createCell(itemTable,orContent.getIssuedTo()+"\n" +
                    ""+orContent.getAddress()+"\n" +
                    ""+Utility.doubleAmountToWords(orContent.getTotal())+" " +Utility.formatDecimal(orContent.getTotal()),2,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            //name, address, and amount in word add \n in each item
            createCell(itemTable,orContent.getIssuedTo()+"\n" +
                    ""+orContent.getAddress()+"\n" +
                    ""+Utility.doubleAmountToWords(orContent.getTotal())+" " +Utility.formatDecimal(orContent.getTotal()),2,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
            createCell(itemTable,3,column.length,1);

            String description="", amount="";
            for (ORItemSummary particulars : orContent.getBulkPrinting()){
                if(particulars.getAmount()!=0){
                    description+=particulars.getDescription()+"\n";
                    amount+=particulars.getTotalView().concat("\n");
                }
            }

            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER,175f, Rectangle.NO_BORDER);
            createCell(itemTable,description,1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);//items
            createCell(itemTable,amount,1,1,  9, Font.NORMAL, Element.ALIGN_RIGHT, Rectangle.NO_BORDER,20f);//amount
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable,description,1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);//items
            createCell(itemTable,amount,1,1,  9, Font.NORMAL, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);//amount

            //Total amount
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
            createCell(itemTable,Utility.formatDecimal(orContent.getTotal()),1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//total amount
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
            createCell(itemTable,Utility.formatDecimal(orContent.getTotal()),1,0,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//total amount

            //Signatories
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
            createCell(itemTable,orContent.getIssuedBy(),1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//cashier
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(itemTable," ",1,1,  9, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
            createCell(itemTable,orContent.getIssuedBy(),1,0,  9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);//cashier

            cell=new PdfPCell(itemTable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            document.add(table);
            document.newPage();
        }



        document.setMarginMirroring(true);
        document.close();

        displayOutput(pdf);


    }

    public void generateLandscape() throws Exception{
        document = new Document(PageSize.LETTER.rotate(),30f,30f,30f,30f);
        Paragraph preface = new Paragraph();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
        HeaderFooter event = new HeaderFooter(headerText, footerText);
        writer.setPageEvent(event);
        document.open();
        table.getDefaultCell().setFixedHeight(80);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

        preface.setAlignment(1);
        preface.add(table);

        document.add(preface);
        document.setMarginMirroring(true);
        document.close();

        displayOutput(pdf);
    }

    public void generate() throws Exception{
        document = new Document(PageSize.LETTER,30f,30f,30f,30f);
        Paragraph preface = new Paragraph();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
        document.open();
        table.getDefaultCell().setFixedHeight(80);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

        preface.setAlignment(1);
        preface.add(table);

        document.add(preface);
        document.setMarginMirroring(true);
        document.close();

        displayOutput(pdf);
    }

    public void header(LocalDate date, String title){
        createCell("BOHOL 1 ELECTRIC COOPERATIVE, INC.", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Cabulijan, Tubigon, Bohol", column.length,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell(title, column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        if (date != null){
            createCell("As of: "+date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), column.length,11, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(1,column.length);
        }
    }

    public void header(LocalDate date, String title, String subtitle){
        createCell("BOHOL 1 ELECTRIC COOPERATIVE, INC.", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Cabulijan, Tubigon, Bohol", column.length,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell(title, column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell(subtitle, column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        if (date != null){
            createCell("As of: "+date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), column.length,11, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
            createCell(1,column.length);
        }
    }

    public void tableHeader(String[] headings, int[] spans){
        createCell(2,column.length);
        for (int i = 0; i < headings.length; i++){
            createCell(headings[i], spans[i],11, Font.BOLD, Element.ALIGN_CENTER);
        }
    }

    public  void tableContent(ArrayList<String[]> items, int[] spans, int[] positions) {
        for(int x=0;x<items.size();x++){
            String[] data = items.get(x);
            for (int i=0; i < data.length; i++){
                createCell(data[i], spans[i],10, Font.NORMAL, positions[i]);
            }
        }
    }

    public  void tableContent(ArrayList<String[]> items, int[] spans, int[] positions, int border) {
        for(int x=0;x<items.size();x++){
            String[] data = items.get(x);
            for (int i=0; i < data.length; i++){
                createCell(data[i], spans[i],10, Font.NORMAL, positions[i], border);
            }
        }
    }

    public  void tableContent(ArrayList<String[]> items, int[] spans, int[] positions,int Valignment, int border ) {
        for(int x=0;x<items.size();x++){
            String[] data = items.get(x);
            for (int i=0; i < data.length; i++){
                createCell(data[i], spans[i],10, Font.NORMAL, positions[i], Valignment, border);
            }
        }
    }

    public void other_details(String[] details, int[] spans, int[] fonts, int[] aligns, int[] borders, boolean add_space){
        if (add_space)
            createCell(1,column.length);
        for (int i = 0; i < details.length; i++){
            createCell(details[i], spans[i],11, fonts[i], aligns[i], borders[i]);
        }
    }

    public  void footer(String[] prepared, String[] designation, String[] signatures, int[] spans, boolean add_space) {
        if (add_space)
            createCell(2,column.length);

        for (int i = 0; i < prepared.length; i++){
            createCell(prepared[i], spans[i],11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        }
        createCell(2,column.length);
        for (int i = 0; i < signatures.length; i++){
            createCell(signatures[i].toUpperCase(), spans[i],11, Font.BOLD, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        }

        for (int i = 0; i < designation.length; i++){
            createCell(designation[i], spans[i],11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        }
    }

    public  void footer(String[] prepared, String[] designation, String[] signatures, int[] spans, boolean add_space, int des_align, int name_align) {
        if (add_space)
            createCell(2,column.length);

        for (int i = 0; i < prepared.length; i++){
            createCell(prepared[i], spans[i],11, Font.NORMAL, des_align, Rectangle.NO_BORDER);
        }
        createCell(2,column.length);
        for (int i = 0; i < signatures.length; i++){
            createCell(signatures[i].toUpperCase(), spans[i],11, Font.BOLD, name_align, Rectangle.NO_BORDER);
        }

        for (int i = 0; i < designation.length; i++){
            createCell(designation[i], spans[i],11, Font.NORMAL, name_align, Rectangle.NO_BORDER);
        }
    }

    public void createCell(int loop, int colSpan){
        for(int x=1;x<=loop;x++){
            cell=new PdfPCell(new Paragraph(" ",new Font(Font.FontFamily.TIMES_ROMAN,8,Font.BOLD)));
            cell.setColspan(colSpan);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
    }

    public void createCell(PdfPTable table, int loop, int colSpan, int rowSpan){
        for(int x=1;x<=loop;x++){
            cell=new PdfPCell(new Paragraph(" ",new Font(Font.FontFamily.TIMES_ROMAN,8,Font.BOLD)));
            cell.setColspan(colSpan);
            cell.setRowspan(rowSpan);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
    }

    public void createCell(String text, int span, int size, int font, int Halignment, int valignment, int border){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(Halignment);
        cell.setVerticalAlignment(valignment);
        cell.setBorder(border);
        table.addCell(cell);
    }

    public void createCell(String text, int span, int size, int font, int alignment, int border){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);
        table.addCell(cell);
    }

    public void createCell(String text, int span, int size, int font, int alignment){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    public void createCell(PdfPTable table, String text, int colSpan, int rowSpan, int size, int font, int alignment){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    public void createCell(PdfPTable table, String text, int colSpan, int rowSpan, int size, int font, int alignment, float height){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        cell.setMinimumHeight(height);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    public void createCell(PdfPTable table, String text, int colSpan, int rowSpan, int size, int font, int alignment, float height, int border){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        cell.setMinimumHeight(height);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);
        table.addCell(cell);
    }

    public void createCell(PdfPTable table, String text, int colSpan, int rowSpan, int size, int font, int alignment, int border){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);
        table.addCell(cell);
    }

    public void createCell(PdfPTable table, String text, int colSpan, int rowSpan, int size, int font, int alignment, int border, float padding){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.TIMES_ROMAN,size,font)));
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        cell.setPaddingRight(padding);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);
        table.addCell(cell);
    }

    public void displayOutput(File file)throws Exception {
        try {

            if (file.exists()) {
                String path = file.getAbsolutePath();
                Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+path);
                p.waitFor();
            } else {
                System.out.println("File does not exists");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class HeaderFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 9, Font.ITALIC);
        String headerText = "";
        String footerText = "";
        public HeaderFooter(String headerText, String footerText){
            this.headerText = headerText;
            this.footerText = footerText;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase(headerText, ffont);
            Phrase footer = new Phrase(footerText, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }

}
