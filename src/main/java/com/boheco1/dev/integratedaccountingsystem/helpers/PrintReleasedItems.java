package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.boheco1.dev.integratedaccountingsystem.objects.Releasing;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class PrintReleasedItems {

    float[] column = {3f,2f,2f,2f,2f,2f,2f};
    PdfPTable table = new PdfPTable(column);
    PdfPCell cell;
    Document document;

    private MIRS mirs;
    private List<Releasing> releasedIitems;

    public PrintReleasedItems(MIRS mirs) throws Exception {
        this.mirs = mirs;
        releasedIitems = ReleasingDAO.get(mirs);
        createDirectory();
        header();
        tableHeader();
        content();

        document = new Document(PageSize.LETTER,30f,30f,30f,30f);
        Paragraph preface = new Paragraph();
        PdfWriter.getInstance(document, new FileOutputStream("c:/prints/releasedItems.pdf"));
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

    private void displayOutput()throws Exception {
        try {

            if ((new File("c:\\prints\\releasedItems.pdf")).exists()) {
                Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler c:\\prints\\releasedItems.pdf");
                p.waitFor();
            } else {
                System.out.println("File does not exists");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void header(){
        createCell("BOHOL 1 ELECTRIC COOPERATIVE, INC.", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Cabulijan, Tubigon, Bohol", column.length,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell("RELEASED ITEM LIST", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell("MIRS Date Filed: ", 6,11, Font.NORMAL, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);
        createCell(""+mirs.getDateFiled(), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);

        createCell("Please furnish the following for:", 3,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell("Other Details: ", 4,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell(mirs.getPurpose(), 3,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell(mirs.getDetails(), 4,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);

    }

    private void tableHeader(){
        createCell(2,column.length);
        createCell("PARTICULARS", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("MIRS ID", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("W.O.N", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("QUANTITY", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("PRICE", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("DATE", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("Released By", 1,11, Font.BOLD, Element.ALIGN_CENTER);
    }

    private  void content() throws Exception {
        for(int x=0;x<releasedIitems.size();x++){
            createCell(StockDAO.get(releasedIitems.get(x).getStockID()).getStockName(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
            createCell(releasedIitems.get(x).getMirsID(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(releasedIitems.get(x).getWorkOrderNo(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(""+releasedIitems.get(x).getQuantity(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(""+releasedIitems.get(x).getPrice(), 1,11, Font.NORMAL, Element.ALIGN_RIGHT);
            createCell(""+releasedIitems.get(x).getCreatedAt(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
            createCell(""+releasedIitems.get(x).getUserID(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
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


