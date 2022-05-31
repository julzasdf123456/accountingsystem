package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MIRSSignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSApprovalFormController implements Initializable {
    @FXML
    private HBox btnHolder;

    @FXML
    private TextArea details;

    @FXML
    private Label purpose, requisitioner, itemCounter, mirsNumber, date, address, applicant, signatories;

    @FXML
    private TableView<MIRSItem> mirsItemTable;

    @FXML
    private JFXButton acceptBtn, rejectBtn;

    private MIRS mirs;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSItem> mirsItemList = MirsDAO.getItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=UserDAO.get(sig.getUserID()).getFullName();
                signatures+="\n";
            }
            signatories.setText(signatures);

            initializeItemTable(mirsItemList);

            itemCounter.setText(mirsItemList.size()+" items found");
            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            address.setText(mirs.getAddress());
            applicant.setText(mirs.getApplicant());
            requisitioner.setText(mirs.getRequisitioner().getFullName());


            /*ObservableList<MIRSItem> observableList = FXCollections.observableArrayList(mirsItemList);

            codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));

            particularsCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

            quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
            tableView.getItems().setAll(observableList);*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeItemTable(List<MIRSItem> mirsItemList) throws Exception {

        ObservableList<MIRSItem> observableList = FXCollections.observableArrayList(mirsItemList);
        TableColumn<MIRSItem, String> neaCodeCol = new TableColumn<>("NEA Code");
        neaCodeCol.setStyle("-fx-alignment: center;");
        neaCodeCol.setPrefWidth(150);
        neaCodeCol.setMaxWidth(150);
        neaCodeCol.setMinWidth(150);
        neaCodeCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getNeaCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setStyle("-fx-alignment: center;");
        quantityCol.setPrefWidth(100);
        quantityCol.setMaxWidth(100);
        quantityCol.setMinWidth(100);
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        mirsItemTable.getColumns().add(neaCodeCol);
        mirsItemTable.getColumns().add(descriptionCol);
        mirsItemTable.getColumns().add(quantityCol);
        mirsItemTable.setPlaceholder(new Label("No item Added"));

        mirsItemTable.getItems().setAll(observableList);
    }

    @FXML
    private void acceptBtn(ActionEvent event) throws Exception {
        if(MIRSSignatoryDAO.getSignatoryCount(mirs.getId()) > 0){
            AlertDialogBuilder.messgeDialog("System Message", "Sorry but approval from assigned signatories are required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }
        confirmationDialog(acceptBtn, "Confirm MIRS approval", "You are about to approved this MIRS.");
    }

    @FXML
    private void rejectBtn(ActionEvent event) {
        confirmationDialog(rejectBtn, "Confirm MIRS rejection", "You are about to reject this MIRS.");
    }

    private void confirmationDialog(JFXButton btn, String title, String message){
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + ColorPalette.INFO + ";");
        dialogContent.setPrefHeight(200);

        Label head = new Label(title);
        head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
        head.setTextFill(Paint.valueOf(ColorPalette.INFO));
        dialogContent.setHeading(head);

        FlowPane flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setRowValignment(VPos.CENTER);
        flowPane.setColumnHalignment(HPos.CENTER);
        flowPane.setVgap(6);

        Label context = new Label(message);
        context.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
        context.setWrapText(true);
        context.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

        flowPane.getChildren().add(context);
        dialogContent.setBody(flowPane);

        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogContent, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);

        JFXButton accept = new JFXButton("Accept");
        accept.setDefaultButton(true);
        accept.getStyleClass().add("JFXButton");

        JFXButton cancel = new JFXButton("Cancel");
        cancel.setDefaultButton(true);
        cancel.getStyleClass().add("JFXButton");
        dialogContent.setActions(cancel,accept);

        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                if(btn == acceptBtn){
                    try {
                        mirs.setStatus(Utility.RELEASING);
                        mirs.setDetails(details.getText());
                        MirsDAO.update(mirs);
                        AlertDialogBuilder.messgeDialog("System Message", "MIRS application was approved and ready for releasing.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                        btnHolder.setDisable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (btn == rejectBtn){
                    try {
                        mirs.setStatus(Utility.REJECTED);
                        mirs.setDetails(details.getText());
                        MirsDAO.update(mirs);
                        AlertDialogBuilder.messgeDialog("System Message", "MIRS application rejected.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                        btnHolder.setDisable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dialog.close();
            }
        });

        cancel.setTextFill(Paint.valueOf(ColorPalette.GREY));
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                dialog.close();
            }
        });
        dialog.show();
    }
}
