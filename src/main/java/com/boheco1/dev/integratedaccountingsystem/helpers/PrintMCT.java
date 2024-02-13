package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintMCT {
    private float[] headerColumn = {.7f, 1f, 2f, .7f, 1f};
    private float[] bodyColumn = {1.3f,.9f,2f,1.2f,1.5f,.5f,1.5f};
    private float[] footerColumn = {1f, 1f};
    private PdfPTable docHeader;
    private PdfPTable docBody;
    private PdfPTable docFooter;
    private File pdf;
    private MCT mct;
    private List<Releasing> fromReleasing;

    private String[] signatories;
    private PdfPCell cell;
    private Document document;


    public PrintMCT(File pdf, MCT mct, List<Releasing> fromReleasing, String[] signatories) throws Exception {
        this.pdf = pdf;
        this.docHeader = new PdfPTable(headerColumn);
        this.docBody = new PdfPTable(bodyColumn);
        this.docFooter = new PdfPTable(footerColumn);
        this.mct = mct;
        this.fromReleasing = fromReleasing;
        this.signatories = signatories;

        header();
        body();
        footer();
    }
    public void generate() throws Exception{
        document = new Document(PageSize.LETTER,10f,10f,80f,25f);
        Paragraph preface = new Paragraph();
        preface.setAlignment(1);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
        writer.setPageEvent(new PDFPageEvent(mct.getMctNo()));
        document.open();
        //docHeader.getDefaultCell().setFixedHeight(80);
        docHeader.setWidthPercentage(100);
        docHeader.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

        //docBody.getDefaultCell().setFixedHeight(80);
        docBody.setWidthPercentage(100);
        docBody.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        docBody.setHeaderRows(1);

        //docFooter.getDefaultCell().setFixedHeight(80);
        docFooter.setWidthPercentage(100);
        docFooter.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

        preface.add(docHeader);
        preface.add(docBody);
        preface.add(docFooter);

        document.add(preface);
        document.setMarginMirroring(true);
        document.close();

        displayOutput(pdf);
    }

    private void header(){
        createCell(docHeader, "BOHOL 1 ELECTRIC COOPERATIVE, INC.", headerColumn.length,12, Font.BOLD, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, "Cabulijan, Tubigon, Bohol", headerColumn.length,11, Font.BOLD, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCellSpacer(docHeader,1,headerColumn.length);
        createCell(docHeader, "Material Charge Ticket".toUpperCase(), headerColumn.length,12, Font.BOLD, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCellSpacer(docHeader,1,headerColumn.length);
        createCellSpacer(docHeader,1,3);
        createCell(docHeader, "MIRS No.: ", 1,11, Font.NORMAL, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, mct.getMirsNo().replaceAll("/","\n").replaceAll("main-","").replaceAll("sub-",""), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCell(docHeader, "Particulars: ", 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, mct.getParticulars(), 2,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCell(docHeader, "Date: ", 1,11, Font.NORMAL, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCell(docHeader, "Address: ", 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, mct.getAddress(), 2,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCell(docHeader, "MCT No.: ", 1,11, Font.NORMAL, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, mct.getMctNo(), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCellSpacer(docHeader,1,3);
        createCell(docHeader, "W.O#: ", 1,11, Font.NORMAL, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docHeader, mct.getWorkOrderNo(), 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCellSpacer(docHeader,2,headerColumn.length);
    }

    private void body() throws Exception {
        MIRS mirs = MirsDAO.getMIRS(mct.getMirsNo());
        String[] headers = {"Acct. Code", "Item Code", "Description", "Unit Cost", "Amount", "Unit", "Qty"};
        for (String header : headers){
            createCell(docBody, header, 1,11, Font.BOLD, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.UNDEFINED);
        }

        double total=0;
        HashMap<String, Double> acctCodeSummary = new HashMap<String, Double>();
        for (Releasing items : fromReleasing) {
            List<ItemizedMirsItem> details = MirsDAO.getItemizedMirsItemDetails(items.getStockID(), items.getMirsID());
            String additionalDescription = "";
            for(ItemizedMirsItem i : details){
                Stock stock = StockDAO.get(items.getStockID());
                additionalDescription += "\n("+stock.controlled()+i.getBrand()+", "+i.getSerial()+", "+i.getRemarks()+")";
            }

            Stock stock = StockDAO.get(items.getStockID());
            String itemCode="-";
            if(stock.getNeaCode() != null || !stock.getNeaCode().isEmpty())
                itemCode = stock.getNeaCode();
            else
                itemCode = stock.getLocalCode();
            createCell(docBody, stock.getAcctgCode(), 1,11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_TOP, Rectangle.NO_BORDER);
            createCell(docBody, itemCode, 1,11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_TOP, Rectangle.NO_BORDER);
            createCell(docBody, stock.getDescription()+additionalDescription, 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_TOP, Rectangle.NO_BORDER);
            createCell(docBody, String.format("%,.2f", stock.getPrice()), 1,11, Font.NORMAL, Element.ALIGN_RIGHT, Element.ALIGN_TOP, Rectangle.NO_BORDER);
            createCell(docBody, String.format("%,.2f", (stock.getPrice() * items.getQuantity())), 1,11, Font.NORMAL, Element.ALIGN_RIGHT, Element.ALIGN_TOP, Rectangle.NO_BORDER);
            createCell(docBody, stock.getUnit(),  1,11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_TOP, Rectangle.NO_BORDER);
            createCell(docBody, Utility.formatQty(items.getQuantity()), 1,11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_TOP, Rectangle.NO_BORDER);

            total += stock.getPrice() * items.getQuantity();

            if(acctCodeSummary.get(stock.getAcctgCode()) == null){
                acctCodeSummary.put(stock.getAcctgCode(),stock.getPrice() * items.getQuantity());
            }else{
                acctCodeSummary.replace(stock.getAcctgCode(),acctCodeSummary.get(stock.getAcctgCode()) + (stock.getPrice() * items.getQuantity()) );
            }
        }

        createCellSpacer(docBody,2,bodyColumn.length);
        createCell(docBody, "TOTAL: ", 4,11, Font.BOLD, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docBody, String.format("%,.2f",total), 1,11, Font.BOLD, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCellSpacer(docBody,1,2);
        createCellSpacer(docBody,2,bodyColumn.length);

        //Create account code summary
        for(Map.Entry<String, Double> acctCode : acctCodeSummary.entrySet()){
            createCell(docBody, acctCode.getKey(), 1,11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
            createCell(docBody, String.format("%,.2f", acctCode.getValue()), 6,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        }
        createCellSpacer(docBody,2,bodyColumn.length);
    }

    private void footer(){
        createCell(docFooter, "Issued By:", 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docFooter, "Received By: ", 1,11, Font.NORMAL, Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);

        createCell(docFooter, signatories[0].toUpperCase(),1, 11, Font.BOLD, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docFooter, signatories[1].toUpperCase(),1, 11, Font.BOLD, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docFooter, signatories[2].toUpperCase(),1, 11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
        createCell(docFooter, signatories[3].toUpperCase(),1, 11, Font.NORMAL, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, Rectangle.NO_BORDER);
    }



    private void displayOutput(File file)throws Exception {
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

    private void createCell(PdfPTable table, String text, int span, int size, int font, int Halignment, int Valignment, int border){
        cell=new PdfPCell(new Paragraph(text,new Font(Font.FontFamily.HELVETICA,size,font)));
        cell.setColspan(span);
        cell.setHorizontalAlignment(Halignment);
        cell.setVerticalAlignment(Valignment);
        if(border != -1)
            cell.setBorder(border);
        table.addCell(cell);
    }

    private void createCellSpacer(PdfPTable table, int loop, int colSpan){
        for(int x=1;x<=loop;x++){
            cell=new PdfPCell(new Paragraph(" ",new Font(Font.FontFamily.HELVETICA,8,Font.BOLD)));
            cell.setColspan(colSpan);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
    }

}
