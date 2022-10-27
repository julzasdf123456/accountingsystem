package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.concurrent.Task;
import javafx.print.*;
import javafx.scene.Node;

public class CustomPrintHelper extends Task {

    private Node node;
    private Paper paper;

    public CustomPrintHelper(Node node, String page, double width, double height){
        this.node = node;
        this.paper = PrintHelper.createPaper(page, width, height, Units.INCH);
    }

    @Override
    protected Object call() throws Exception {
        print();
        return null;
    }

    public void print() throws Exception{
        Printer printer = Printer.getDefaultPrinter();

        PrinterJob job = PrinterJob.createPrinterJob();

        PageLayout layout = printer.createPageLayout(paper, PageOrientation.PORTRAIT, Printer.MarginType.EQUAL);

        JobSettings jobSettings = job.getJobSettings();
        jobSettings.setPageLayout(layout);

        if (job != null) {
            boolean printed = job.printPage(node);
            if (printed) {
                job.endJob();
            } else {
                throw new Exception("Printer error! Printing failed!");
            }
        } else {
            throw new Exception("Printer error! Could not create a printer job!");
        }
    }

}
