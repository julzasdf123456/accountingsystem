package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXDialog;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;



public class ORLayoutController implements Initializable {
    @FXML
    private Pane printArea;
    private Node node;

    @FXML
    private Label cusName1, cusName2, address1, address2, cashier1, cashier2, date1, date2, inWord1, inWord2, orNum1, orNum2, ref1, ref2, desc1, desc2, amount1, amount2, totalAmount1, totalAmount2, totalAmount3, totalAmount4;

    private ORContent orContent = null;

    private ObjectTransaction parentController;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parentController = Utility.getParentController();

        JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    printSaveOR();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };

        task.setOnRunning(wse -> {
            dialog.show();
        });

        task.setOnSucceeded(wse -> {
            dialog.close();
            if(Utility.ERROR_MSG == null){
                AlertDialogBuilder.messgeDialog("System Message", "Transaction Complete.",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                Utility.OR_NUMBER = Integer.parseInt(orContent.getOrNumber());
                parentController.receive(true);
            }else {
                AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: "+Utility.ERROR_MSG,
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }

        });

        task.setOnFailed(wse -> {
            dialog.close();
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: "+Utility.ERROR_MSG,
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        });

        new Thread(task).start();
    }

    private void printSaveOR() throws Exception {
        //Anchor pane Size for OR
        //Width 581
        //height 380
        try {
            String pattern = "M/d/yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            orContent = Utility.getOrContent();
            //HashMap<String, List<ItemSummary>> breakdown = tellerInfo.getDCRBreakDown();
            //List<ItemSummary> result = breakdown.get("Breakdown");

            //List<ItemSummary> misc = breakdown.get("Misc");
            //double collectionFromTeller = Double.parseDouble(String.format("%.2f",misc.get(1).getTotal()));


            String description = "", amount = "";
            if(orContent.getTellerCollection()!=null){
                for (ORItemSummary a : orContent.getTellerCollection()) {
                    if (a.getAmount() > 0) {
                        description += a.getDescription() + "\n";
                        amount += Utility.formatDecimal(a.getAmount()) + "\n";
                    }
                }
            }else if(orContent.getCustomerCollection() != null) {
                for (CRMDetails a : orContent.getCustomerCollection()) {
                    if (a.getTotal() > 0) {
                        description += a.getParticulars() + "\n";
                        amount += Utility.formatDecimal(a.getTotal()) + "\n";
                    }
                }
            }
            date1.setText(orContent.getDate().format(dateFormatter));
            date2.setText(orContent.getDate().format(dateFormatter));
            cusName1.setText(orContent.getIssuedTo());
            cusName2.setText(orContent.getIssuedTo());
            address1.setText(orContent.getAddress());
            address2.setText(orContent.getAddress());
            orNum1.setText(orContent.getOrNumber());
            orNum2.setText(orContent.getOrNumber());
            inWord1.setText(Utility.doubleAmountToWords(orContent.getTotal()).replaceAll("£",""));
            inWord2.setText(Utility.doubleAmountToWords(orContent.getTotal()).replaceAll("£",""));
            totalAmount1.setText(Utility.formatDecimal(orContent.getTotal()));
            totalAmount2.setText(Utility.formatDecimal(orContent.getTotal()));
            totalAmount3.setText(Utility.formatDecimal(orContent.getTotal()));
            totalAmount4.setText(Utility.formatDecimal(orContent.getTotal()));
            ref1.setText("");
            ref2.setText("");
            desc1.setText(description);
            desc2.setText(description);
            amount1.setText(amount);
            amount2.setText(amount);


            EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
            //cashier1.setText(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
            //cashier2.setText(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
            cashier1.setText(orContent.getIssuedBy());
            cashier2.setText(orContent.getIssuedBy());

            TransactionHeaderDetailDAO.save(orContent.getCrmQueue(), orContent.getTransactionHeader(), orContent.getTds());
            if(Utility.ERROR_MSG == null){//error message from TransactionHeaderDAO
                Node node = printArea;
                Paper paper = PrintHelper.createPaper("OR", 18, 6, Units.INCH);
                Printer printer = Printer.getDefaultPrinter();
                PageLayout pageLayout = printer.createPageLayout(paper,
                        PageOrientation.PORTRAIT, 10, 10, 0, 0);
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    boolean success = job.printPage(pageLayout, node);
                    if (success) {
                        System.out.println("PRINTING FINISHED");
                        job.endJob();
                    }
                }
            }
        } catch (Exception e) {
            Utility.ERROR_MSG = "OR Layout Class "+e.getMessage();
            e.printStackTrace();
        }
        ModalBuilder.MODAL_CLOSE();
    }

}
