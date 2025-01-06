package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.Bill;

import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;
import javafx.concurrent.Task;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.awt.Desktop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReprintInvoice extends Task  {

    private PaidBill bill;

    public Connection con;
    Object row[][];
    DB connect=new DB();

    PdfPTable mainpdftable;

    PdfPTable innertable;

    PdfPTable innertable2;

    PdfPCell cell=new PdfPCell();
    PdfPCell innercell=new PdfPCell();

    PdfPCell tablecell=new PdfPCell();
    PdfWriter writer;
    PdfReader reader=null;
    Document document;
    String name,data1[];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    SimpleDateFormat format= new SimpleDateFormat("MM/dd/yyyy");

    double surchage=0, vatsur=0;

    public ReprintInvoice(PaidBill bill){
        this.bill = bill;

        try {
            connect.setConnection(Utility.DB_BILLING);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }



    }

    /**
     * Print call method on a thread
     * @return void
     */
    @Override
    protected Object call() throws Exception {

        print();
        return null;
    }

    /**
     * Print method based in inputstream byte data sent to default printer
     * @return void
     */
    public void print() throws Exception {

        this.createPDF();

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.print(new File("C:\\IntegratedAccounting\\Sales Invoice.pdf"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate print data based on PaidBill values
     * @return void
     */
    private void createPDF(){



        Rectangle pagesize;

        pagesize=new Rectangle(468f,648f);
        document = new Document(pagesize,5,5,47,1);
        name="C:/IntegratedAccounting/Sales Invoice.pdf";

        try
        {
            // PdfWriter.getInstance(document, baos);
            writer = PdfWriter.getInstance(document, baos);
            document.open();

        }
        catch(Exception e){javax.swing.JOptionPane.showMessageDialog(null, "The process cannot access the file because it's either in used or already open.\n Closed first the preivious file before opening this file.","ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);}

        setvalue();
        document.close();

        try{
            reader = new PdfReader(baos.toByteArray());

            PdfStamper stamper= new PdfStamper(reader, new FileOutputStream(name));
            stamper.close();

        }
        catch(Exception e){e.printStackTrace();}
    }


    private void setvalue()
    {



            mainpdftable = new PdfPTable(new float[]{6.5f});
            mainpdftable.getDefaultCell().setFixedHeight(100);
            mainpdftable.setWidthPercentage(100);
            mainpdftable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);



            try{
                row=connect.getAllRecord("Select  Bi.AccountNumber, isnull(AM.TINNo,''), substring(AM.ConsumerName,1,35) as ConsumerName,substring(AM.ConsumerAddress,1,50) as ConsumerAddress,ISNULL(Bi.Remarks,'') as SubscriberNo,CONVERT(VARCHAR(10),Bi.ServiceDateFrom,101) as ServiceDateFrom,CONVERT(VARCHAR(10),Bi.ServiceDateTo,101) as ServiceDateTo,Bi.MeterNumber,Routes.RouteCode,Bi.ConsumerType,Bi.Coreloss, CONVERT(VARCHAR(10),Bi.ServicePeriodEnd,101) as BillingMonth," +
                        "    ISNULL(Bi.PowerPresentReading,0),ISNULL(Bi.PowerPreviousReading,0),ISNULL(Meter.Multiplier,1) AS Multiplier,ISNULL(Bi.PowerKWH,0),CONVERT(VARCHAR(10),Bi.DueDate,101) as DueDate,ISNULL(Bi.DemandPresentReading,0),ISNULL(Bi.DemandPreviousReading,0), ISNULL(Bi.DemandKW,0),ISNULL(Bi.NetPresReading,0),ISNULL(Bi.NetPrevReading,0),ISNULL(Bi.NetPowerKWH,0)," +
                        "    ISNULL(URates.GenerationSystemCharge,0),ISNULL(bi.GenerationSystemAmt,0),ISNULL(URatesExt.item3,0) as GenVatRate,ISNULL(BiEx.GenerationVAT,0),ISNULL(URatesExt.item4,0) as TransVatRate,ISNULL(BiEx.TransmissionVAT,0),ISNULL(URates.TransmissionSystemCharge,0),ISNULL(Bi.TransmissionSystemAmt,0),ISNULL(Urates.CrossSubsidyCreditCharge,0),ISNULL(BiEx.SLVAT,0),ISNULL(Urates.TransmissionDemandCharge,0)," +
                        "    ISNULL(Bi.TransmissionDemandAmt,0),ISNULL(BiEx.DistributionVAT,0),ISNULL(URates.DAA_VAT,0),ISNULL(Bi.DAA_VAT,0),ISNULL(URates.SystemLossCharge,0),ISNULL(Bi.SystemLossAmt,0),ISNULL(URates.ACRM_VAT,0),ISNULL(Bi.ACRM_VAT,0),ISNULL(BiEx.OthersVAT,0),ISNULL(URates.DistributionSystemCharge,0),ISNULL(Bi.DistributionSystemAmt,0),ISNULL(Bi.SeniorCitizenDiscount,0)," +
                        "    ISNULL(URates.SeniorCitizenSubsidyCharge,0),ISNULL(Bi.SeniorCitizenSubsidy,0),ISNULL(URates.DistributionDemandCharge,0), ISNULL(Bi.DistributionDemandAmt,0),ISNULL(URates.SupplySystemCharge,0),isnull(Bi.SupplySystemAmt,0),ISNULL(URates.LifelineSubsidyCharge,0), ISNULL(Bi.LifelineSubsidyAmt,0),ISNUll(URates.SupplyRetailCustomerCharge,0),ISNULL(Bi.SupplyRetailCustomerAmt,0),isnull(URates.MCC,0),ISNULL(BiEx.Item10,0)," +
                        "    ISNULL(URates.MeteringSystemCharge,0),ISNULL(Bi.MeteringSystemAmt,0),isnull(URates.PPARefund,0),ISNULL(Bi.Item4,0) as FITall,ISNULL(URates.MeteringRetailCustomerCharge,0),ISNULL(Bi.MeteringRetailCustomerAmt,0),ISNULL(Bi.Others,0),ISNULL(URates.MissionaryElectrificationCharge,0),ISNULL(Bi.MissionaryElectrificationAmt,0),ISNULL(URates.FPCAAdjustmentCharge,0),ISNULL(Bi.FPCAAdjustmentAmt,0),ISNULL(URates.EnvironmentalCharge,0)," +
                        "    ISNULL(Bi.EnvironmentalAmt,0),isnull(URates.ACRM_TAFPPCACharge,0),ISNULL(bI.ACRM_TAFPPCA,0),ISNULL(URates.DAA_GRAMCharge,0),ISNULL(Bi.DAA_GRAM,0),ISNULL(URates.FBHCCharge,0),ISNULL(Bi.FBHCAmt,0),ISNULL(URatesExt.item6,0),ISNULL(BiEx.item16,2),ISNULL(URates.ACRM_TAFxACharge,0),ISNULL(BiEX.Item17,0),ISNULL(Bi.PR,0) as TransRental,BiEx.Item6 as AmountDue, Bi.Item2 as VATAmount,isnull(Paid.Amount2306,0),isnull(Paid.Amount2307,0),isnull(Paid.Surcharge,0),Paid.NetAmount as PaidNetAmount,isnull(Paid.PromptPayment,0),Bi.NetAmount as BillsNetAmount" +
                        "    ,ISNULL(Bi.NetGenerationAmount,0),ISNULL((Select isnull(NetMeteringNetAmount,0) from Bills where AccountNumber=Bi.AccountNumber and ServicePeriodEnd= DATEADD(MONTH, -1, Bi.ServicePeriodEnd) ),0) as ResidualCredit,PB.Teller,CONVERT(VARCHAR(20),GETDATE(),22), PB.DCRNumber" +
                        "   FROM dbo.Bills Bi LEFT OUTER JOIN BillsExtension BiEx ON " +
                        "    Bi.AccountNumber = BiEx.AccountNumber AND Bi.ServicePeriodEnd = BiEx.ServicePeriodEnd LEFT OUTER JOIN AdjustedUnbundledBills AdjUBi ON" +
                        "    Bi.AccountNumber = AdjUBi.AccountNumber AND Bi.ServicePeriodEnd = AdjUBi.ServicePeriodEnd LEFT OUTER JOIN AccountMaster AM ON " +
                        "    Bi.AccountNumber = AM.AccountNumber LEFT OUTER JOIN Meter ON" +
                        "    Bi.MeterNumber = Meter.MeterNumber LEFT  OUTER JOIN Routes ON " +
                        "    AM.Route = Routes.RouteCode LEFT OUTER  JOIN  Towns ON " +
                        "    Routes.TownCode = Towns.TownCode LEFT OUTER JOIN UnbundledRates URates ON" +
                        "    Bi.ServicePeriodEnd = URates.ServicePeriodEnd AND Bi.ConsumerType = URates.ConsumerType LEFT OUTER JOIN UnbundledRatesExtension URatesExt ON" +
                        "    Bi.ServicePeriodEnd = URatesExt.ServicePeriodEnd AND Bi.ConsumerType = URatesExt.ConsumerType LEFT OUTER JOIN PaidBills PB ON" +
                        "    Bi.AccountNumber = PB.AccountNumber AND Bi.ServicePeriodEnd = PB.ServicePeriodEnd  LEFT OUTER JOIN KatasData ON" +
                        "    Bi.AccountNumber = KatasData.AccountNumber LEFT OUTER Join MDRBalances X on Bi.AccountNumber = X.AccountNumber  LEFT OUTER JOIN BillsForDCRRevision BiDCR on Bi.AccountNumber=BiDCR.AccountNumber and Bi.ServicePeriodEnd=BiDCR.ServicePeriodEnd LEFT OUTER JOIN Paidbills Paid ON Bi.AccountNumber=Paid.AccountNumber and Bi.ServicePeriodEnd=Paid.ServicePeriodEnd   " +
                        "WHERE Bi.ServicePeriodEnd= '"+ bill.getServicePeriodEnd()+ "' and Bi.AccountNumber='"+ bill.getConsumer().getAccountID() +"'  ");

            }
            catch(Exception e)
            {e.printStackTrace();}


            PdfPTable itemtable = new PdfPTable(new float[]{.9f,3.4f,.3f,.9f,.3f,.7f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            //=========================== first row ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Account NUmber
            innercell=new PdfPCell(new Paragraph(""+row[0][0].toString().trim(),new Font(Font.FontFamily.TIMES_ROMAN,8,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Tin No
            innercell=new PdfPCell(new Paragraph(""+row[0][1],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Date
            innercell=new PdfPCell(new Paragraph(""+connect.getSpecificData("SELECT CONVERT(VARCHAR(10), GETDATE(), 101) AS [MM/DD/YYYY]"),new Font(Font.FontFamily.TIMES_ROMAN,8,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);


            //=========================== end first row ==================================

            //=========================== SECOND row ==================================

            itemtable = new PdfPTable(new float[]{.9f,2.6f,1.6f,1.4f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Consumer Name
            innercell=new PdfPCell(new Paragraph(""+row[0][2].toString().trim(),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,8,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            //=========================== end SECOND row ==================================

            //=========================== THIRD ROW ==================================

            itemtable = new PdfPTable(new float[]{.9f,2.3f,1.9f,1.4f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Consumer Address
            innercell=new PdfPCell(new Paragraph(""+row[0][3],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end THIRD ROW ==================================


            //=========================== 4TH ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Subscriber No
            innercell=new PdfPCell(new Paragraph(""+row[0][4],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            //=========================== end 4TH ROW ==================================

            //=========================== 5TH ROW ==================================

            itemtable = new PdfPTable(new float[]{1.5f,.7f,.7f,.7f,1f,1.9f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(5);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //period Covered
            innercell=new PdfPCell(new Paragraph(""+row[0][5] +" to " + row[0][6],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end 5TH ROW ==================================

            //=========================== 6tH ROW ==================================

            //METER SERIAL NUMBER
            innercell=new PdfPCell(new Paragraph(""+row[0][7],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //route
            innercell=new PdfPCell(new Paragraph(""+row[0][8],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //COnsumer Type
            innercell=new PdfPCell(new Paragraph(""+row[0][9],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Coreloss
            innercell=new PdfPCell(new Paragraph(""+row[0][10],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Billing Month
            innercell=new PdfPCell(new Paragraph(""+row[0][11],new Font(Font.FontFamily.TIMES_ROMAN,7,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(6);
            innercell.setFixedHeight(5);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            //=========================== end 6TH ROW ==================================

            //=========================== 7tH ROW ==================================

            innertable = new PdfPTable(new float[]{3.7f});
            innertable.getDefaultCell().setFixedHeight(45);
            innertable.setWidthPercentage(100);
            innertable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innertable2 = new PdfPTable(new float[]{2.8f});
            innertable2.getDefaultCell().setFixedHeight(45);
            innertable2.setWidthPercentage(100);
            innertable2.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);


            itemtable = new PdfPTable(new float[]{.9f,.7f,.7f,.6f,.8f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(5);
            innercell.setFixedHeight(2);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //PowerPresentReading
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][12].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //PowerPreviousReading
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][13].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Multiplier
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][14].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Power KWH Used
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][15].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end 7TH ROW ==================================

            //=========================== 8tH ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //DemandPresentReading
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][17].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //DemandPreviousReading
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][18].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Multiplier
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Power KWH Used
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][19].toString().trim()))  ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            //=========================== end 8TH ROW ==================================

            //=========================== 9tH ROW ==================================


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NetPresReading
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][20].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NetPrevReading
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][21].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Mulitplier
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NetPowerKWH
            innercell=new PdfPCell(new Paragraph(""+String.format("%.0f", Double.parseDouble( row[0][22].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            tablecell=new PdfPCell(itemtable);
            tablecell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablecell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(tablecell);

            //=========== FOR DUE DATE==============================
            itemtable = new PdfPTable(new float[]{.7f,2.1f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Duedate
            innercell=new PdfPCell(new Paragraph(""+row[0][16] ,new Font(Font.FontFamily.TIMES_ROMAN,10,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(15);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);
            //==============================END FOR DUEDATE


            //===============FOR AMOUNT PAID ==================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //AMOUNT PAID
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][87].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,12,Font.BOLD)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(25);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            tablecell=new PdfPCell(itemtable);
            tablecell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablecell.setBorder(Rectangle.NO_BORDER);
            innertable2.addCell(tablecell);

            // =====================END FOR AMOUNT PAID ==========


            itemtable = new PdfPTable(new float[]{3.7f,2.8f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innercell=new PdfPCell(innertable);
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(innertable2);
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            //=========================== end 9TH ROW ==================================

            itemtable = new PdfPTable(new float[]{1.7f,.6f,.9f,1.8f,.6f,.9f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(6);
            innercell.setFixedHeight(27);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== Generation ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Generation System Rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][23].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Generation System Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][24].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Generation VAT Rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][25].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Generation VAT Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][26].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end Generation ROW ==================================

            //=========================== Transmission VAT ROW ==================================


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("" ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Transmission VAT Rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][27].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Transmission VAT Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][28].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end Transmission VAT ROW ==================================

            //=========================== Transmission System ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //transmission System rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][29].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //transmission System amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][30].toString().trim()) ),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //System Loss VAT Rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][31].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //System Loss VAT Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][32].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end Transmission System  ROW ==================================

            //=========================== Transmission Demand ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //transmission Demand rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][33].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //transmission Demand amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][34].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Distribution VAT Rate
            innercell=new PdfPCell(new Paragraph("0.1200",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Distribution VAT Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][35].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end Transmission Demand ROW ==================================

            //=========================== DAA VAT ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("" ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //DAA VAT Rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][36].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //DAA VAT Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][37].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end DAA VAT ROW ==================================

            //=========================== System Loss ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //System loss Charge
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][38].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //System Loss Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][39].toString().trim())) ,new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //ACRM VAT Rate
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][40].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //ACRM VAT Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][41].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end System Loss ROW ==================================

            //=========================== Other VAT  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("0.1200",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Others VAT Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][42].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end System Loss ROW ==================================


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(6);
            innercell.setFixedHeight(22);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            //=========================== Distribution System ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Distribution System Rate
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][43].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Distribution Demand Amount
            innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][44].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            if(Double.parseDouble(row[0][45].toString().trim())>0)
            {
                innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setFixedHeight(11);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);

                //Senior Citizen Discount
                innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][45].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setFixedHeight(11);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);
            }
            else {
                innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][46].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setFixedHeight(11);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);

                //Senior Citizen Subsidy
                innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][47].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setFixedHeight(11);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);
            }

            //=========================== end Distribution System ROW ==================================

            //=========================== Distribution Demand  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //distribution Demand Rate
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][48].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //distribution Demand Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][49].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Distribution DemandROW ==================================


            //=========================== Supply System  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Supply System Rate
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][50].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Supply System Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][51].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Lifeline Subsidy Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][52].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Lifeline Subsidy Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][53].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Supply System DemandROW ==================================

            //=========================== Supply Retail  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Supply Retail Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][54].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Supply Retail Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][55].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //RFSC Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][56].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //RFSC Subsidy Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][57].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Supply System DemandROW ==================================

            //=========================== Metering System  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Metering System Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][58].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Metering System Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][59].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //FitAll Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][60].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //FitAllAmount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][61].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Metering System ROW ==================================

            //=========================== Metering Retail  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Metering Retail Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][62].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Metering Retail Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][63].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Other Charges
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][64].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Metering Retail ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(6);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //===============================Surcharge Row =================================================
            surchage=0;
            vatsur=0;

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][86].toString().trim())>0)
            {
                innercell=new PdfPCell(new Paragraph("              SURCHAGE",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}


            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][86].toString().trim())>0)
            {
                surchage=Double.parseDouble(row[0][86].toString().trim());
                vatsur=Double.parseDouble(row[0][86].toString().trim()) * .12;
                innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", surchage ),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));

            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //===============================END Surcharge Row =============================================

            //===============================VAT Surcharge Row =================================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][86].toString().trim())>0)
            {
                innercell=new PdfPCell(new Paragraph("              VAT SURCHAGE",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            }
            else{innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}


            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][86].toString().trim())>0)
            {
                innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", vatsur),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));

            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //===============================END vat Surcharge Row =============================================

            //=========================== Missionary  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Missionary  Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][65].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Missionary Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][66].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Missionary ROW ==================================

            //=========================== NPC Debt  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NPC Stranded  Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][67].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NPC Stranded Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][68].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  NPC Debt ROW ==================================


            //=========================== NPC Con COst  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NPC Stranded Con Cost  Charge
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //NPC Stranded  Con Cost Amount
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  NPC Con COst ROW ==================================

            //=========================== Environmental  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Environmental Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][69].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Environmental Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][70].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Environmental ROW ==================================

            //=========================== ACRM - TAFPPCA  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //ACRM - TAFPPCA   Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][71].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //ACRM - TAFPPCA   Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][72].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  ACRM - TAFPPCA ROW ==================================

            //=========================== DAA  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //DAA  Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][73].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //DAA   Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][74].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  DAA ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(6);
            innercell.setFixedHeight(34);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== Franchise  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Franchise  Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][75].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Franchise   Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][76].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Franchise ROW ==================================

            //=========================== Business Tax  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Business Tax  Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][77].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Business Tax   Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][78].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Business Tax ROW ==================================

            //=========================== Real Property Tax  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Real Property Tax  Charge
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.4f", Double.parseDouble(row[0][79].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Real Property Tax   Amount
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][80].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Transformer Rental
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][81].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Real Property Tax ROW ==================================


            //=========================== Amount  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(5);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //amount due
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][82].toString().trim()) + surchage )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  Amount ROW ==================================

            //=========================== VAT and Pass- Through taxes  ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(5);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //VAT AND PASS-THROUGH TAXES
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][83].toString().trim()) + vatsur )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  VAT and Pass- Through taxes ROW ==================================

            //=========================== BLANK ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(6);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  BLANK ROW ==================================


            //=========================== TOTAL CURRENT AMOUNT ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(2);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][90].toString()) > 0)
            {
                innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][89].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));}


            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  TOTAL CURRENT AMOUNT ROW ==================================

            //=========================== LESS TOTAL CURRENT AMOUNT ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][90].toString()) > 0)
            {
                innercell=new PdfPCell(new Paragraph(""+String.format("%,.4f", Double.parseDouble(row[0][23].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));}

            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][90].toString()) > 0)
            {
                innercell=new PdfPCell(new Paragraph(""+String.format("%,.2f", Double.parseDouble(row[0][90].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));}
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(2);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][90].toString()) > 0)
            {
                innercell=new PdfPCell(new Paragraph("("+String.format("%,.2f", Double.parseDouble(row[0][90].toString().trim())) + ")",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            }
            else {innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));}
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end LESS TOTAL CURRENT AMOUNT ROW ==================================

            //=========================== LESS CREDIT AMOUNT ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][91].toString()) < 0)
            {
                innercell=new PdfPCell(new Paragraph("-"+String.format("%,.2f", Double.parseDouble(row[0][91].toString().trim())) + "",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            }
            else{ innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));}
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(2);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            if(Double.parseDouble(row[0][91].toString()) < 0)
            {
                innercell=new PdfPCell(new Paragraph("-"+String.format("%,.2f", Double.parseDouble(row[0][91].toString().trim())) + "",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            }
            else{ innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));}
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  LESS CREDITAMOUNT ROW ==================================

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            itemtable = new PdfPTable(new float[]{1f,.8f,.4f,.9f,.9f,.9f,.7f,.9f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);


            //=========================== DISCOUNT ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Discount
            if(Double.parseDouble(row[0][88].toString().trim())>0)
            {innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][88].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}
            else{innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}


            //innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][88].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //VATABLE SALES
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][82].toString().trim()) + surchage )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //TOTAL SALES
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][89].toString().trim()) + surchage )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  DISCOUNT ROW ==================================

            data1=connect.getSpecificRow("Select Count(bills.BillNumber),Sum(bills.NetAmount)   FROM    dbo.Bills LEFT OUTER JOIN dbo.PaidBills ON dbo.Bills.AccountNumber = dbo.PaidBills.AccountNumber AND dbo.Bills.ServicePeriodEnd = dbo.PaidBills.ServicePeriodEnd WHERE (dbo.PaidBills.ServicePeriodEnd IS NULL) AND (dbo.Bills.ServicePeriodEnd > '1/1/2013') AND bills.accountNumber='"+ bill.getConsumer().getAccountID() +"' and bills.ServicePeriodEnd < '"+ bill.getServicePeriodEnd()+"'  Group By bills.AccountNumber");


            //=========================== amount 2307 5% ROW ==================================
            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            try{
                if(data1[0]!=null)
                {
                    innercell=new PdfPCell(new Paragraph(""+String.format("%,.0f", Double.parseDouble(data1[0].trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));

                }
                else{
                    innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                }

            }
            catch(Exception e)
            {
                innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                e.printStackTrace();
            }


            //innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Amount 2306
            if(Double.parseDouble(row[0][84].toString().trim())>0)
            {innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][84].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}
            else{innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}

            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //VAT
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][83].toString().trim()) + vatsur )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //(VAT INCLUSIVES)
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][83].toString().trim()) + vatsur )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            //innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][82].toString().trim()) + surchage + Double.parseDouble(row[0][83].toString().trim()) + vatsur )),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  amount 2307 5% ROW ==================================

            //=========================== amount 2307 2% ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(10);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            try
            {
                if(data1[1]!=null)
                {
                    innercell=new PdfPCell(new Paragraph(""+String.format("%,.0f", Double.parseDouble(data1[1].trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));

                }
                else{
                    innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                }

            }
            catch(Exception e)
            {
                innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
                e.printStackTrace();
            }

            //innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //Amount 2307
            if(Double.parseDouble(row[0][85].toString().trim())>0)
            {innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][85].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}
            else{innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));}

            //innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][85].toString().trim())),new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //AMOUNT NET OF VAT
            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f",  Double.parseDouble(row[0][82].toString().trim()) )+ "",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  amount 2307 2% ROW ==================================

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            itemtable = new PdfPTable(new float[]{1.6f,.6f,.8f,.9f,.9f,.7f,.9f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);


            //=========================== AMOUNT NET OF VAT ROW ==================================

                /*innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setColspan(6);
                innercell.setFixedHeight(10);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);


                innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", (Double.parseDouble(row[0][82].toString().trim()) + surchage )),new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setFixedHeight(10);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);

                //=========================== end  AMOUNT NET OF VAT ROW ==================================

                //=========================== LESS:DISCOUNT ROW ==================================

                innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setColspan(6);
                innercell.setFixedHeight(10);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);


                innercell=new PdfPCell(new Paragraph("( "+ String.format("%,.2f", Double.parseDouble(row[0][88].toString().trim())) +" )",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
                innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                innercell.setFixedHeight(10);
                innercell.setBorder(Rectangle.NO_BORDER);
                itemtable.addCell(innercell);

                //=========================== end  LESS:DISCOUNT ROW ==================================

                cell=new PdfPCell(itemtable);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                mainpdftable.addCell(cell);*/

            itemtable = new PdfPTable(new float[]{1.6f,.6f,.8f,.9f,.9f,.7f,.9f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            //=========================== less : Discount ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph(""+ row[0][92].toString().trim(),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("( "+ String.format("%,.2f", (Double.parseDouble(row[0][88].toString().trim()) + Double.parseDouble(row[0][85].toString().trim())) ) + " )",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== less : Discount ROW ==================================

            //=========================== WITHHOLDING TAX ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph(""+ row[0][93].toString().trim(),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph("( "+ String.format("%,.2f", (Double.parseDouble(row[0][84].toString().trim()) + Double.parseDouble(row[0][85].toString().trim())) ) + " )",new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(11);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  WITHHOLDING TAX ROW ==================================

            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            itemtable = new PdfPTable(new float[]{1.6f,.6f,.8f,.9f,.9f,.7f,.9f});
            itemtable.getDefaultCell().setFixedHeight(100);
            itemtable.setWidthPercentage(100);
            itemtable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

            //=========================== TOTAL AMOUNT DUE ROW ==================================

            innercell=new PdfPCell(new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN,6,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(12);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            innercell=new PdfPCell(new Paragraph("Inovice No. "+ row[0][94].toString().trim(),new Font(Font.FontFamily.TIMES_ROMAN,7,Font.NORMAL)));
            innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setColspan(3);
            innercell.setFixedHeight(12);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);


            innercell=new PdfPCell(new Paragraph(""+ String.format("%,.2f", Double.parseDouble(row[0][87].toString().trim())  ) ,new Font(Font.FontFamily.TIMES_ROMAN,8,Font.BOLD)));

            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innercell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            innercell.setFixedHeight(12);
            innercell.setBorder(Rectangle.NO_BORDER);
            itemtable.addCell(innercell);

            //=========================== end  TOTAL AMOUNT DUE  ROW ==================================


            cell=new PdfPCell(itemtable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            mainpdftable.addCell(cell);

            try{
                document.add(mainpdftable);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }



    }

}
