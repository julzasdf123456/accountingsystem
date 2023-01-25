package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class ORLayoutController implements Initializable {
    @FXML
    private Pane printArea;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            //Anchor pane Size for OR
            //Width 581
            //height 380
            Node node = printArea;
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4,
                    PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean success = job.printPage(pageLayout, node);
                if (success) {
                    System.out.println("PRINTING FINISHED");
                    job.endJob();
                }
            }
            ModalBuilder. MODAL_CLOSE();
        }catch (Exception e){
            ModalBuilder. MODAL_CLOSE();
        }

    }
}
