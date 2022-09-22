package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.PrintPDF;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class SummaryChargesReportController implements Initializable {

    @FXML
    private TableView<SummaryCharges> chargesTable;

    @FXML
    private JFXTextField search_tf;

    @FXML
    private JFXComboBox<String> month_cb;

    @FXML
    private JFXTextField year_tf;

    private List<SummaryCharges> summaryCharges;
    private ObservableList<SummaryCharges> observableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        month_cb.getItems().addAll(month);
        year_tf.setText(""+Calendar.getInstance().get(Calendar.YEAR));
        prepareTable();
    }

    @FXML
    private void printSummary(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(summaryCharges == null)
            return;

        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        String month = month_cb.getSelectionModel().getSelectedItem();
        String year = year_tf.getText();
        fileChooser.setInitialFileName("Summary_of_charges_"+month+ "_" +year+".pdf");
        File selectedFile = fileChooser.showSaveDialog(stage);
        JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, generating report.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try{
                    dialog.show();

                    if (selectedFile != null) {
                        float[] columns = {1f,1f,2f,1f,.5f,1f,1f,1f,1f,1f};
                        PrintPDF pdf = new PrintPDF(selectedFile, columns, "", "Prepared by: "+ActiveUser.getUser().getFullName());
                        pdf.header(null, "Summary of Charges (as of "+month+", "+year+")" .toUpperCase(), "".toUpperCase());

                        pdf.createCell(1,columns.length);
                        pdf.createCell("Account\nCode", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Item\nCode", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Description", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Unit Cost\n"+month, 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Unit", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Stock\nBalance", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Issued", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Returned", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Received", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell("Stock\nBalance", 1, 11, Font.NORMAL, Element.ALIGN_CENTER);

                        for(SummaryCharges charges : summaryCharges){
                            Stock temp = StockDAO.get(charges.getId());
                            pdf.createCell(charges.getAcctgCode(), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(charges.getId(), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(charges.getDescription(), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(String.format("%,.2f",charges.getPrice()), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(temp.getUnit(), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(isZero(temp.getQuantity()), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(isZero(charges.getIssued()), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(isZero(charges.getReturned()), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(isZero(charges.getReceived()), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                            pdf.createCell(isZero(charges.getBalance()), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        }

                        pdf.generateLandscape();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("Printing Error", e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                return null;
            }
        };
        task.setOnSucceeded(wse -> {
            dialog.close();
        });

        task.setOnFailed(workerStateEvent -> {
            dialog.close();
            AlertDialogBuilder.messgeDialog("System Warning", "A problem was encountered, please try again",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        });
        new Thread(task).start();
    }

    private String isZero(int n){
        return n == 0 ? "-" : ""+n;
    }

    private String isZero(double n){
        return n == 0 ? "-" : ""+n;
    }


    @FXML
    private void selectByMonth(ActionEvent event) throws SQLException, ClassNotFoundException {
        try{
            int index = month_cb.getSelectionModel().getSelectedIndex()+1;
            int monthEnd = 30;
            int year = Integer.parseInt(year_tf.getText());
            if(index == 1 || index == 3 || index == 5 || index == 7 || index == 8 || index == 10 || index == 12)
                monthEnd = 31;

            if(index == 2){
                if((year%4==0 && year%100!=0) || year%400==0) {
                    monthEnd = 29;
                }else {
                    monthEnd = 28;
                }
            }

            summaryCharges = StockDAO.getSummaryCharges(year_tf.getText()+"-"+index+"-1", year_tf.getText()+"-"+index+"-"+monthEnd);
            System.out.println(summaryCharges.size());
            observableList = FXCollections.observableArrayList(summaryCharges);
            chargesTable.getItems().clear();
            chargesTable.getItems().setAll(observableList);
            chargesTable.refresh();
        }catch (Exception e){
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }

    }

    private void prepareTable(){
        TableColumn<SummaryCharges, String> desCol = new TableColumn<>("Description");
        desCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<SummaryCharges, Double> priCol = new TableColumn<>("Price");
        priCol.setStyle("-fx-alignment: center;");
        priCol.setPrefWidth(100);
        priCol.setMaxWidth(100);
        priCol.setMinWidth(100);
        priCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SummaryCharges, String> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setStyle("-fx-alignment: center;");
        qtyCol.setPrefWidth(100);
        qtyCol.setMaxWidth(100);
        qtyCol.setMinWidth(100);
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SummaryCharges, String> balCol = new TableColumn<>("Balance");
        balCol.setStyle("-fx-alignment: center;");
        balCol.setPrefWidth(100);
        balCol.setMaxWidth(100);
        balCol.setMinWidth(100);
        balCol.setCellValueFactory(new PropertyValueFactory<>("balance"));

        chargesTable.getColumns().add(desCol);
        chargesTable.getColumns().add(priCol);
        chargesTable.getColumns().add(qtyCol);
        chargesTable.getColumns().add(balCol);

        chargesTable.setPlaceholder(new Label("No item Added"));
    }

}
