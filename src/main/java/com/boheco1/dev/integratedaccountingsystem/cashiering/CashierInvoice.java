package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXDialog;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CashierInvoice  implements Initializable  {
    @FXML
    Pane printAreaInvoice;
    private Node node;

    @FXML
    Label cusName1, cashier1, date1, description1, orNum1, desc1, amount1, totalAmount3, totalAmount4;

    private ORContent orContent = null;

    private ObjectTransaction parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parentController = Utility.getParentController();
        try {
            initField();

            JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                   printSaveOR();
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                dialog.show();
            });

            task.setOnSucceeded(wse -> {
                dialog.close();
                if(Utility.ERROR_MSG == null || Utility.ERROR_MSG.isEmpty()){
                    AlertDialogBuilder.messgeDialog("System Message", "Transaction Complete.",
                            Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                    Utility.OR_NUMBER = Integer.parseInt(orContent.getOrNumber())+1;
                    parentController.receive(true);
                } else {
                    AlertDialogBuilder.messgeDialog("System Error (On Succeeded)", "Error encounter while saving transaction: "+Utility.ERROR_MSG +", please try again.",
                            Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    parentController.receive(false);
                }

            });

          task.setOnFailed(wse -> {
                dialog.close();

                AlertDialogBuilder.messgeDialog("System Error (On Failed)", "Error encounter while saving transaction: "+Utility.ERROR_MSG,
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);

                parentController.receive(false);
                ModalBuilder.MODAL_CLOSE();
            });
            new Thread(task).start();

        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error (O.R Layout)", "Error encounter while initializing fields: "+e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            parentController.receive(false);
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    private void initField() throws Exception {
        //Anchor pane Size for OR
        //Width 581
        //height 380
        try{
            String pattern = "M/d/yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            orContent = Utility.getOrContent();

            String description = "", amount = "";
            if(orContent.getTellerCollection()!=null){
                ObservableList<ORItemSummary> items = orContent.getTellerCollection();
                for (ORItemSummary a : items) {
                    if(!a.getDescription().equals("Grand Total")) {
                        if (a.getDescription().length() > 26) {
                            description += a.getDescription().substring(0, 25) + "...\n";
                        } else {
                            description += a.getDescription() + "\n";
                        }
                        amount += Utility.formatDecimal(a.getAmount()) + "\n";
                    }
                }
            }else if(orContent.getCustomerCollection() != null) {
                ObservableList<CRMDetails> items = orContent.getCustomerCollection();
                for (CRMDetails a : items) {
                    if(!a.getParticulars().equals("Grand Total")) {
                        description += a.getParticulars() + "\n";
                        amount += Utility.formatDecimal(a.getTotal()) + "\n";
                    }
                }
            }else if(orContent.getSupplierItems() != null){
                ObservableList<ORItemSummary> items = orContent.getSupplierItems();

                for (ORItemSummary a : items) {
                    if(a.getDescription().contains("TIN") || a.getDescription().contains("BUSINESS STYLE")){
                        description += a.getDescription() + "\n";
                        amount += a.getTotalView() + "\n";
                    }
                }

                description += "\n";
                amount += "\n";

                for (ORItemSummary a : items) {
                    if(!a.getDescription().equals("Grand Total") && !a.getDescription().contains("TIN") && !a.getDescription().contains("BUSINESS STYLE")) {
                        description += a.getDescription() + "\n";
                        amount += a.getTotalView() + "\n";
                    }
                }
            }
            date1.setText(orContent.getDate().format(dateFormatter));
            cusName1.setText(orContent.getIssuedTo());
            description1.setText(orContent.getAddress());
            orNum1.setText(orContent.getOrNumber());
            //description1.setText(description1.getText()+"\n"+Utility.doubleAmountToWords(orContent.getTotal()).replaceAll("£","")+"         "+Utility.formatDecimal(orContent.getTotal()));
            totalAmount3.setText(Utility.formatDecimal(orContent.getTotal()));
            totalAmount4.setText(Utility.formatDecimal(orContent.getTotal()));
            desc1.setText(description);
            amount1.setText(amount);


            EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
            //cashier1Invoice.setText(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
            //cashier2.setText(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
           cashier1.setText(orContent.getIssuedBy());
        } catch (Exception e) {
            Utility.ERROR_MSG = "OR Layout Class "+e.getMessage();
            e.printStackTrace();
        }
    }

    private void printSaveOR(){
        try{
            if(!orContent.isReprint())
                TransactionHeaderDetailDAO.save(orContent.getCrmQueue(), orContent.getTransactionHeader(), orContent.getTds());


            if(Utility.ERROR_MSG == null || Utility.ERROR_MSG.isEmpty()){//error message from TransactionHeaderDAO


                node  = printAreaInvoice;
                Printer printer = Printer.getDefaultPrinter();

                Paper invoice = printer.getDefaultPageLayout().getPaper();
                 PageLayout pageLayout = printer.createPageLayout(invoice, PageOrientation.PORTRAIT, 10, 10, 0, 0);


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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
