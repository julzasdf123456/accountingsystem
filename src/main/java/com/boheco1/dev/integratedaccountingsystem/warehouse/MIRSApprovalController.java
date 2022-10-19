package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSApprovalController implements Initializable {
    @FXML
    private HBox btnHolder;

    @FXML
    private Label purpose, requisitioner, itemCounter, mirsNumber, date, address, applicant, signatories, details;

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
                signatures+= sig.getStatus().toUpperCase()+" : "+EmployeeDAO.getOne(sig.getUserID(), DB.getConnection()).getFullName();
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
        /*TableColumn<MIRSItem, String> neaCodeCol = new TableColumn<>("NEA Code");
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
        });*/

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
        //quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> qtycellFactory
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                    // System.out.println(mirsItem.getQuantity());
                                    setGraphic(null);
                                    double req = mirsItem.getQuantity();
                                    if(req%1 == 0)
                                        setText(""+(int) req);
                                    else
                                        setText(""+req);
                                }
                            }
                        };
                        return cell;
                    }
                };

        quantityCol.setCellFactory(qtycellFactory);

        //mirsItemTable.getColumns().add(neaCodeCol);
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
        JFXButton accept = new JFXButton("Accept");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Confirm MIRS approval","You are about to approved this MIRS.?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try {
                    mirs.setStatus(Utility.RELEASING);
                    mirs.setDetails(details.getText());
                    MirsDAO.update(mirs);
                    String notif_details = "MIRS ("+mirs.getId()+") for releasing.";
                    Notifications torequisitioner = new Notifications(notif_details, Utility.NOTIF_INFORMATION, ActiveUser.getUser().getEmployeeID(), mirs.getRequisitionerID(), mirs.getId());
                    NotificationsDAO.create(torequisitioner);
                    AlertDialogBuilder.messgeDialog("System Message", "MIRS application was approved and ready for releasing.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                    btnHolder.setDisable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.close();
            }
        });
    }

    @FXML
    private void rejectBtn(ActionEvent event) {
        JFXButton accept = new JFXButton("Accept");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Confirm MIRS rejection","You are about to reject this MIRS.?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try {
                    JFXButton accept = new JFXButton("Accept");
                    JFXTextField remarks = new JFXTextField();
                    remarks.setStyle("-fx-alignment: center; -fx-text-fill: "+ ColorPalette.BLACK + ";");
                    remarks.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 18));
                    remarks.setLabelFloat(false);
                    JFXDialog dialogMCTNumber = DialogBuilder.showInputDialog("System Message","Please provide reason for the rejection of this MIRS.", Orientation.HORIZONTAL, remarks, accept, DialogBuilder.INFO_DIALOG, Utility.getStackPane());
                    accept.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent __) {
                            if(remarks.getText().isEmpty()) {
                                AlertDialogBuilder.messgeDialog("System Message", "Please provide reason for this action.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                            }else{
                                try {
                                    mirs.setStatus(Utility.REJECTED);
                                    mirs.setDetails(details.getText());
                                    MirsDAO.update(mirs);
                                    String notif_details = "MIRS ("+mirs.getId()+") was rejected.";
                                    Notifications torequisitioner = new Notifications(notif_details, Utility.NOTIF_INFORMATION, ActiveUser.getUser().getEmployeeID(), mirs.getRequisitionerID(), mirs.getId());
                                    NotificationsDAO.create(torequisitioner);
                                    AlertDialogBuilder.messgeDialog("System Message", "MIRS application rejected.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                                    btnHolder.setDisable(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            dialogMCTNumber.close();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.close();
            }
        });
    }

}
