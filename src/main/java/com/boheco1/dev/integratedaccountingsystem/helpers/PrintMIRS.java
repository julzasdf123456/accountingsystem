package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.dao.MIRSSignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class PrintMIRS {

    float[] column = {2f,2f,2f,2f,2f,2f};
    PdfPTable table = new PdfPTable(column);
    PdfPCell cell;
    Document document;

    private MIRS mirs;
    private List<MIRSItem> mirsItemList;
    private List<MIRSSignatory> mirsSignatoryList;

    public PrintMIRS(MIRS mirs) throws Exception {
        this.mirs = mirs;
        mirsItemList = MirsDAO.getItems(mirs);
        mirsSignatoryList = MIRSSignatoryDAO.get(mirs);
        createDirectory();
        header();
        tableHeader();
        content();
        Signatory();

        document = new Document(PageSize.LETTER,30f,30f,30f,30f);
        Paragraph preface = new Paragraph();
        PdfWriter.getInstance(document, new FileOutputStream("c:/prints/MIRS.pdf"));
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

            if ((new File("c:\\prints\\MIRS.pdf")).exists()) {
                Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler c:\\prints\\MIRS.pdf");
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
        createCell("MATERIAL ISSUANCE REQUISITION SLIP", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell("MIRS#: ", 5,11, Font.NORMAL, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);
        createCell(mirs.getId(), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);

        createCell("MIRS Date Filed: ", 5,11, Font.NORMAL, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);
        createCell(""+mirs.getDateFiled(), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);

        createCell("Please furnish the following for:", 3,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell("Other Details: ", 3,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell(mirs.getPurpose(), 3,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
        createCell(mirs.getDetails(), 3,11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
    }

    private void tableHeader(){
        createCell(2,column.length);
        createCell("CODE", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("PARTICULARS", 2,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("UNIT", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("QUANTITY", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("REMARKS", 1,11, Font.BOLD, Element.ALIGN_CENTER);
    }

    private  void content() throws Exception {
        for(int x=0;x<mirsItemList.size();x++){
            createCell(mirsItemList.get(x).getStockID().substring(0, 10)+"...", 1,11, Font.NORMAL, Element.ALIGN_LEFT);
            createCell(StockDAO.get(mirsItemList.get(x).getStockID()).getStockName(), 2,11, Font.NORMAL, Element.ALIGN_LEFT);
            createCell(StockDAO.get(mirsItemList.get(x).getStockID()).getUnit(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(""+mirsItemList.get(x).getQuantity(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            createCell(mirsItemList.get(x).getRemarks(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
        }

        createCell(" ", 1,11, Font.NORMAL, Element.ALIGN_CENTER);
        createCell("xxxxx NOTHING FOLLOWS xxxxx", 2,9, Font.NORMAL, Element.ALIGN_CENTER);
        createCell(" ", 1,11, Font.NORMAL, Element.ALIGN_CENTER);
        createCell(" ", 1,11, Font.NORMAL, Element.ALIGN_CENTER);
        createCell(" ", 1,11, Font.NORMAL, Element.ALIGN_CENTER);

    }

    private void Signatory() throws Exception {
        createCell("I hereby certify that the materials/supplies requested above are necessary and wth purpose stated above. ", column.length,10, Font.BOLD, Element.ALIGN_LEFT);

        createCell(2, column.length);

        createCell("Prepared by:", column.length,11, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell(2, column.length);
        createCell(mirs.getRequisitioner().getFullName().toUpperCase(Locale.ROOT), column.length,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell("Requisitioner", column.length,9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2, column.length);

        createCell("Recommended by:", 3,10, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Approved", 3,10, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell(2, column.length);
        createCell(UserDAO.get(mirsSignatoryList.get(0).getUserID()).getFullName().toUpperCase(Locale.ROOT), 3,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell(UserDAO.get(mirsSignatoryList.get(1).getUserID()).getFullName().toUpperCase(Locale.ROOT), 3,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell("Department Manager", 3,9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("General Manager", 3,9, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

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


