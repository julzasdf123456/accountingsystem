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

    float[] column = {1f,3f,1f,1f};
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
        footer();

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
        createCell("MIRS - RELEASED ITEM LIST", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell("Date Released: "+mirs.getDateFiled(), column.length,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell("ID: "+mirs.getId(), column.length,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell("Work Order #: "+releasedIitems.get(0).getWorkOrderNo(), column.length,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);

    }

    private void tableHeader(){
        createCell(1,column.length);
        createCell("CODE", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("PARTICULARS", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("UNIT", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("QTY", 1,11, Font.BOLD, Element.ALIGN_CENTER);

    }

    private  void content() throws Exception {
        for(int x=0;x<releasedIitems.size();x++){
            createCell(""+releasedIitems.get(x).getStockID(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(StockDAO.get(releasedIitems.get(x).getStockID()).getDescription(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
            createCell(StockDAO.get(releasedIitems.get(x).getStockID()).getUnit(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(""+releasedIitems.get(x).getQuantity(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
        }

    }

    private  void footer() throws Exception {
        createCell("Please furnish the following for: \n"+mirs.getPurpose(), 2,11, Font.NORMAL, Element.ALIGN_LEFT);
        createCell("Other Details: \n"+mirs.getDetails(), 2,11, Font.NORMAL, Element.ALIGN_LEFT);

        createCell("Requested By: "+mirs.getRequisitioner().getFullName(), column.length,8, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell("Released By: "+UserDAO.get(releasedIitems.get(0).getUserID()).getFullName(), column.length,8, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
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


