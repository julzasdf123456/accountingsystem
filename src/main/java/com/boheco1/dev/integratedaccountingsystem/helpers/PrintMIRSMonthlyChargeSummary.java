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
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class PrintMIRSMonthlyChargeSummary {

    float[] column = {3f,1f,1f,1.5f,1.5f,1.5f,1.5f};
    PdfPTable table = new PdfPTable(column);
    PdfPCell cell;
    Document document;

    private List<MIRS> mirsList;

    public PrintMIRSMonthlyChargeSummary(List<MIRS> mirsList) throws Exception {
        this.mirsList = mirsList;
        //List<MIRSItem> mirsItemList = MirsDAO.getItems(mirs);
        //mirsSignatoryList = MIRSSignatoryDAO.get(mirs);
        createDirectory();
        header();
        tableHeader();
        content();

        document = new Document(PageSize.LETTER.rotate(),30f,30f,30f,30f);

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

        MIRS temp = mirsList.get(0);
        LocalDate ld = temp.getDateFiled();
        createCell("BOHOL 1 ELECTRIC COOPERATIVE, INC.", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Cabulijan, Tubigon, Bohol", column.length,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

        createCell(2,column.length);
        createCell("MIRS: Released Items", column.length,12, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
        createCell("Monthly Charges Summary for the month of " + ld.getMonth() +" "+ ld.getYear(), column.length,11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
    }

    private void tableHeader(){
        createCell(2,column.length);
        createCell("Particulars", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("Qty", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("Price", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("Charges", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("NEA Code", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("Acct Code", 1,11, Font.BOLD, Element.ALIGN_CENTER);
        createCell("Local Code", 1,11, Font.BOLD, Element.ALIGN_CENTER);
    }

    private  void content() throws Exception {
        double total = 0;
        for (MIRS m : mirsList) {
            System.out.println(m.getId());
            List<Releasing> mirsItemList = ReleasingDAO.get(m, Utility.RELEASED);
            for (Releasing rel : mirsItemList) {
                System.out.println(rel.getStockID());
                createCell(StockDAO.get(rel.getStockID()).getDescription(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
                createCell(""+rel.getQuantity(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
                createCell(""+String.format("%,.2f",rel.getPrice()), 1,11, Font.NORMAL, Element.ALIGN_RIGHT);
                double temp = rel.getPrice() * +rel.getQuantity();
                total+=temp;
                createCell(""+String.format("%,.2f",temp), 1,11, Font.NORMAL, Element.ALIGN_RIGHT);
                createCell(StockDAO.get(rel.getStockID()).getNeaCode(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
                createCell(StockDAO.get(rel.getStockID()).getAcctgCode(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
                createCell(StockDAO.get(rel.getStockID()).getLocalCode(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
            }
            System.out.println();
            /*for(int x=0;x<mirsItemList.size();x++){
                createCell(StockDAO.get(mirsItemList.get(x).getStockID()).getDescription(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
                createCell(""+mirsItemList.get(x).getQuantity(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
                createCell(""+StockDAO.get(mirsItemList.get(x).getStockID()).getPrice(), 1,11, Font.NORMAL, Element.ALIGN_LEFT);
                createCell(""+mirsItemList.get(x).getPrice(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
                createCell(StockDAO.get(mirsItemList.get(x).getStockID()).getNeaCode(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
                createCell(StockDAO.get(mirsItemList.get(x).getStockID()).getAcctgCode(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
                createCell(StockDAO.get(mirsItemList.get(x).getStockID()).getLocalCode(), 1,11, Font.NORMAL, Element.ALIGN_CENTER);
            }*/
        }
        createCell("Total: " + String.format("%,.2f",total), 4,14, Font.BOLD, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);
        createCell(1,3);
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


