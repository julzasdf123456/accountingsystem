package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;
import javafx.concurrent.Task;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PrintOEBR extends Task {

    private PaidBill bill;
    private String printData;

    public PrintOEBR(PaidBill bill){
        this.bill = bill;
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
        this.prepareOEBR();

        try{
            InputStream is = new ByteArrayInputStream(this.printData.getBytes());
            DocFlavor flavor =  DocFlavor.INPUT_STREAM.AUTOSENSE;

            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
            printServiceAttributeSet.add(new PrinterName(service.getName(), null));
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 11, 4, MediaPrintableArea.INCH));
            printRequestAttributeSet.add(OrientationRequested.LANDSCAPE);

            //Create the print job
            DocPrintJob job = service.createPrintJob();
            Doc doc= new SimpleDoc(is, flavor, null);

            //Monitor print job events; for the implementation of PrintJobWatcher,
            PrintJobWatcher pjDone = new PrintJobWatcher(job);

            //Print it
            job.print(doc, printRequestAttributeSet);

            //Wait for the print job to be done
            pjDone.waitForDone();

            //Close the input stream
            is.close();
        } catch (PrintException | IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Generate print data based on PaidBill values
     * @return void
     */
    private void prepareOEBR(){
        //Placeholder for elements per line
        String data = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n".replace("x", " ");

        //Print data
        String id = bill.getConsumer().getAccountID();
        String meter_no = id.substring(0,2)+"-"+id.substring(2,6)+"-"+id.substring(6);
        String type = "Type: "+bill.getConsumerType();
        String consumer = bill.getConsumer().getConsumerName();

        if (consumer.length() > 24)
            consumer = consumer.substring(0, 25);

        String billno = bill.getBillNo();
        String address = bill.getConsumer().getConsumerAddress();

        if (address.length() >= 32)
            address = address.substring(0, 32)+"..";

        String[] billing = bill.getBillMonth().split(" ");
        String billmonth = billing[0].substring(0, 3).toUpperCase(Locale.ROOT)+". "+billing[1];
        String kwhUsed = bill.getPowerKWH()+"";
        double surVat = bill.getVat() + bill.getSurChargeTax();
        String vat = Utility.formatDecimal(surVat);
        String due = bill.getDueDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String dueDate = "Due Date: "+due;
        String surcharge = Utility.formatDecimal(bill.getSurCharge());
        if (surcharge.equals("50"))
            surcharge = "50.00";
        String amountDue = Utility.formatDecimal(bill.getTotalAmount());
        //Bill amount is total amount less the sum of vat, surcharge and surchargeVat
        String amount = Utility.formatDecimal(bill.getTotalAmount() - bill.getSurCharge() - surVat);
        String teller = bill.getTeller();
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy hh:mmaa");
        LocalDate serverDate = LocalDate.now();
        try {
            serverDate = Utility.serverDate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String receivedDate = dateFormat2.format(java.sql.Date.valueOf(serverDate));

        //Print elements per line
        String[][] cols = {
                {" "},
                {" "},
                {" "},
                {meter_no, type, meter_no, type},
                {consumer, billno, consumer, billno},
                {address, address},
                {" "},
                {" "},
                {billmonth, kwhUsed, amount, billmonth, kwhUsed, amount},
                {vat, vat},
                {" "},
                {"Surcharge", surcharge, "Surcharge", surcharge},
                {dueDate, amountDue, dueDate, amountDue},
                {" "},
                {teller, receivedDate, teller, receivedDate}
        };

        //Position of elements per line
        int adj = 2; //printing adjusted to two characters to the right
        int[][] layout = {
                {0},
                {0},
                {0},
                {1, 24+adj, 40, 65+adj},
                {1, 24+adj, 40, 65+adj},
                {1, 40},
                {0},
                {0},
                {1, 13+adj, 24+adj+ (amountDue.length() - amount.length()), 40, 55+adj, 66+adj+ (amountDue.length() - amount.length())},
                {24+adj + (amountDue.length() - vat.length()), 66+adj + (amountDue.length() - vat.length())},
                {0},
                {12, 24+adj + (amountDue.length() - surcharge.length()), 54, 66+adj + (amountDue.length() - surcharge.length())},
                {1, 24+adj, 40, 66+adj},
                {0},
                {5+adj+2, 18+adj+2, 44+adj+2, 58+adj+2}
        };

        //Generate print data
        printData = "";
        int index = 0;
        for (String vals[] : cols) {
            String c = data;
            for (int no=0; no < vals.length; no++) {
                int start = layout[index][no];
                int end = start+vals[no].length();
                c = new StringBuffer(c).replace(start, end, vals[no]).toString();
            }
            printData += c;
            index++;
        }
        printData += "\n\n\n";
    }
}
