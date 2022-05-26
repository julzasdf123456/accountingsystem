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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSReleasingFormController implements Initializable {
    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private HBox btnHolder, addItemHolder;

    @FXML
    private TextArea details, purpose;

    @FXML
    private JFXTextField particulars, remarks, quantity;

    @FXML
    private Label available, inStock, pending;

    @FXML
    private Label mirsNum, date, requisitioner, dm, gm;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, unitCol, particularsCol, remarksCol, releaseCol, cancelCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;
    @FXML

    private TableView particularsTable;
    private Stock selectedStock = null;
    private ObservableList<MIRSItem> requestItem = null;

    private MIRSItem selectedMirsItem;
    private MIRS mirs;
    private String work_order_number;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindParticularsAutocomplete(particulars);
        InputValidation.restrictNumbersOnly(quantity);
        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSItem> mirsItemList = MirsDAO.getUnreleasedItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            work_order_number = mirsItemList.get(0).getWorkOrderNo();

            mirsNum.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            requisitioner.setText(mirs.getRequisitioner().getFullName());
            dm.setText(""+ UserDAO.get(mirsSignatoryList.get(0).getUserID()).getFullName());
            gm.setText(""+ UserDAO.get(mirsSignatoryList.get(1).getUserID()).getFullName());

            requestItem = FXCollections.observableArrayList(mirsItemList);

            codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));
            codeCol.setStyle("-fx-alignment: center-left;");

            particularsCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(StockDAO.get(cellData.getValue().getStockID()).getDescription());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            particularsCol.setStyle("-fx-alignment: center-left;");

            unitCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getUnit());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            unitCol.setStyle("-fx-alignment: center;");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
            quantityCol.setStyle("-fx-alignment: center-left;");
            remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));
            remarksCol.setStyle("-fx-alignment: center-left;");

            releaseCol.setStyle("-fx-alignment: center;");
            Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> releaseBtn
                    = //
                    new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                        @Override
                        public TableCell call(final TableColumn<MIRSItem, String> param) {
                            final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                                Button btn = new Button("");
                                FontIcon icon = new FontIcon("mdi2c-check-circle");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                    btn.setStyle("-fx-background-color: "+ColorPalette.SUCCESS);
                                    btn.setGraphic(icon);
                                    btn.setGraphicTextGap(5);
                                    btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        btn.setOnAction(event -> {
                                            selectedMirsItem = getTableView().getItems().get(getIndex());
                                            try {
                                                //code for release individual item
                                                JFXDialogLayout dialogContent = new JFXDialogLayout();
                                                dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + ColorPalette.INFO + ";");
                                                dialogContent.setPrefHeight(200);

                                                Label head = new Label("Confirm Action");
                                                head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
                                                head.setTextFill(Paint.valueOf(ColorPalette.INFO));
                                                dialogContent.setHeading(head);

                                                FlowPane flowPane = new FlowPane();
                                                flowPane.setOrientation(Orientation.VERTICAL);
                                                flowPane.setAlignment(Pos.CENTER);
                                                flowPane.setRowValignment(VPos.CENTER);
                                                flowPane.setColumnHalignment(HPos.CENTER);
                                                flowPane.setVgap(6);

                                                Label context = new Label("Confirm releasing of item: "+StockDAO.get(selectedMirsItem.getStockID()).getDescription());
                                                context.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
                                                context.setWrapText(true);
                                                context.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

                                                flowPane.getChildren().add(context);
                                                dialogContent.setBody(flowPane);

                                                JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);
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
                                                        try{
                                                            Releasing releasing = new Releasing();
                                                            releasing.setStockID(selectedMirsItem.getStockID());
                                                            releasing.setMirsID(selectedMirsItem.getMirsID());
                                                            releasing.setQuantity(selectedMirsItem.getQuantity());
                                                            releasing.setPrice(selectedMirsItem.getPrice());
                                                            releasing.setUserID(ActiveUser.getUser().getId());
                                                            releasing.setStatus(Utility.RELEASED);
                                                            releasing.setWorkOrderNo(selectedMirsItem.getWorkOrderNo());
                                                            ReleasingDAO.add(releasing);
                                                            Stock temp = StockDAO.get(selectedMirsItem.getStockID()); //temp stock object for quantity deduction
                                                            StockDAO.deductStockQuantity(temp, selectedMirsItem.getQuantity());

                                                            requestItem.remove(selectedMirsItem);
                                                            particularsTable.setItems(requestItem);
                                                            Utility.getActiveMIRS().setDetails(details.getText());
                                                            MirsDAO.update(Utility.getActiveMIRS());

                                                            if(requestItem.size() == 0){
                                                                Utility.getActiveMIRS().setStatus(Utility.CLOSED);
                                                                Utility.getActiveMIRS().setDetails(details.getText());
                                                                MirsDAO.update(Utility.getActiveMIRS());
                                                                anchorpane.setDisable(true);
                                                                dialog.close();
                                                            }

                                                            AlertDialogBuilder.messgeDialog("System Message", "MIRS item released.", stackPane, AlertDialogBuilder.INFO_DIALOG);
                                                        }catch (Exception e){
                                                            AlertDialogBuilder.messgeDialog("System Error", "Individual releasing: " + e.getMessage(), stackPane, AlertDialogBuilder.INFO_DIALOG);
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
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            releaseCol.setCellFactory(releaseBtn);

            cancelCol.setStyle("-fx-alignment: center;");
            Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> removeBtn
                    = //
                    new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                        @Override
                        public TableCell call(final TableColumn<MIRSItem, String> param) {
                            final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                                Button btn = new Button("");
                                FontIcon icon = new FontIcon("mdi2c-cancel");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                    btn.setStyle("-fx-background-color: "+ColorPalette.DANGER);
                                    btn.setGraphic(icon);
                                    btn.setGraphicTextGap(5);
                                    btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        btn.setOnAction(event -> {
                                            selectedMirsItem = getTableView().getItems().get(getIndex());
                                            try {
                                                //code for release individual item
                                                JFXDialogLayout dialogContent = new JFXDialogLayout();
                                                dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + ColorPalette.INFO + ";");
                                                dialogContent.setPrefHeight(200);

                                                Label head = new Label("Confirm Action");
                                                head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
                                                head.setTextFill(Paint.valueOf(ColorPalette.INFO));
                                                dialogContent.setHeading(head);

                                                FlowPane flowPane = new FlowPane();
                                                flowPane.setOrientation(Orientation.VERTICAL);
                                                flowPane.setAlignment(Pos.CENTER);
                                                flowPane.setRowValignment(VPos.CENTER);
                                                flowPane.setColumnHalignment(HPos.CENTER);
                                                flowPane.setVgap(6);

                                                Label context = new Label("Confirm cancellation of MIRS item: "+StockDAO.get(selectedMirsItem.getStockID()).getDescription());
                                                context.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
                                                context.setWrapText(true);
                                                context.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

                                                flowPane.getChildren().add(context);
                                                dialogContent.setBody(flowPane);

                                                JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);
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
                                                        try{
                                                            //MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                                            try {
                                                                Releasing releasing = new Releasing();
                                                                releasing.setStockID(selectedMirsItem.getStockID());
                                                                releasing.setMirsID(selectedMirsItem.getMirsID());
                                                                releasing.setQuantity(selectedMirsItem.getQuantity());
                                                                releasing.setPrice(selectedMirsItem.getPrice());
                                                                releasing.setUserID(ActiveUser.getUser().getId());
                                                                releasing.setStatus(Utility.UNAVAILABLE);
                                                                releasing.setWorkOrderNo(selectedMirsItem.getWorkOrderNo());
                                                                ReleasingDAO.add(releasing);
                                                                //Stock temp = StockDAO.get(selectedMirsItem.getStockID()); //temp stock object for quantity deduction
                                                                //StockDAO.deductStockQuantity(temp, selectedMirsItem.getQuantity());

                                                                requestItem.remove(selectedMirsItem);
                                                                particularsTable.setItems(requestItem);
                                                                Utility.getActiveMIRS().setDetails(details.getText());
                                                                MirsDAO.update(Utility.getActiveMIRS());

                                                                /*requestItem.remove(mirsItem);
                                                                particularsTable.setItems(requestItem);
                                                                Utility.getActiveMIRS().setDetails(details.getText());
                                                                MirsDAO.update(Utility.getActiveMIRS());
                                                                MirsDAO.removeItem(mirsItem);*/

                                                                if(requestItem.size() == 0){
                                                                    Utility.getActiveMIRS().setStatus(Utility.CLOSED);
                                                                    Utility.getActiveMIRS().setDetails(details.getText());
                                                                    MirsDAO.update(Utility.getActiveMIRS());
                                                                    anchorpane.setDisable(true);
                                                                }

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            Utility.getActiveMIRS().setDetails(details.getText());
                                                            MirsDAO.update(Utility.getActiveMIRS());

                                                            AlertDialogBuilder.messgeDialog("System Message", "MIRS items canceled.", stackPane, AlertDialogBuilder.INFO_DIALOG);
                                                        }catch (Exception e){
                                                            AlertDialogBuilder.messgeDialog("System Error", "Item removed: " + e.getMessage(), stackPane, AlertDialogBuilder.INFO_DIALOG);
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
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            cancelCol.setCellFactory(removeBtn);
            particularsTable.setFixedCellSize(35);
            particularsTable.setPlaceholder(new Label("No rows to display"));
            particularsTable.getItems().setAll(requestItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acceptBtn(ActionEvent event) {
        try {

            JFXDialogLayout dialogContent = new JFXDialogLayout();
            dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + ColorPalette.INFO + ";");
            dialogContent.setPrefHeight(200);

            Label head = new Label("Confirm Action");
            head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
            head.setTextFill(Paint.valueOf(ColorPalette.INFO));
            dialogContent.setHeading(head);

            FlowPane flowPane = new FlowPane();
            flowPane.setOrientation(Orientation.VERTICAL);
            flowPane.setAlignment(Pos.CENTER);
            flowPane.setRowValignment(VPos.CENTER);
            flowPane.setColumnHalignment(HPos.CENTER);
            flowPane.setVgap(6);

            Label context = new Label("Confirm releasing of all MIRS items.");
            context.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
            context.setWrapText(true);
            context.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

            flowPane.getChildren().add(context);
            dialogContent.setBody(flowPane);

            JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);
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
                    try{
                        for (MIRSItem mirsItem : requestItem){
                            Releasing releasing = new Releasing();
                            releasing.setStockID(mirsItem.getStockID());
                            releasing.setMirsID(mirsItem.getMirsID());
                            releasing.setQuantity(mirsItem.getQuantity());
                            releasing.setPrice(mirsItem.getPrice());
                            releasing.setUserID(ActiveUser.getUser().getId());
                            releasing.setStatus(Utility.RELEASED);
                            releasing.setWorkOrderNo(mirsItem.getWorkOrderNo());
                            ReleasingDAO.add(releasing);
                            Stock temp = StockDAO.get(mirsItem.getStockID()); //temp stock object for quantity deduction
                            StockDAO.deductStockQuantity(temp, mirsItem.getQuantity());
                        }

                        Utility.getActiveMIRS().setStatus(Utility.CLOSED);
                        Utility.getActiveMIRS().setDetails(details.getText());
                        MirsDAO.update(Utility.getActiveMIRS());

                        AlertDialogBuilder.messgeDialog("System Message", "MIRS items released.", stackPane, AlertDialogBuilder.INFO_DIALOG);
                        anchorpane.setDisable(true);
                    }catch (Exception e){
                        AlertDialogBuilder.messgeDialog("System Error", "Item released: " + e.getMessage(), stackPane, AlertDialogBuilder.INFO_DIALOG);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addBtn(ActionEvent event) {
        try{
            if(selectedStock == null){
                AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
                return;
            }else if(Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
                AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
                return;
            }

            MIRSItem mirsItem = new MIRSItem();
            mirsItem.setMirsID(mirsNum.getText());
            mirsItem.setStockID(selectedStock.getId());
            mirsItem.setParticulars(selectedStock.getStockName());
            mirsItem.setUnit(selectedStock.getUnit());
            mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
            mirsItem.setPrice(selectedStock.getPrice());
            mirsItem.setWorkOrderNo(work_order_number);
            mirsItem.setRemarks(remarks.getText());

            selectedStock = null; //set to null for validation
            requestItem.add(mirsItem);
            particularsTable.setItems(requestItem);


            particulars.setText("");
            quantity.setText("");
            particulars.requestFocus();
            inStock.setText("In Stock: 0");
            pending.setText("Pending: 0");
            available.setText("Available: 0");

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void bindParticularsAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SlimStock> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SlimStock> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.search(query, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        selectedStock = null;
                        quantity.setText("");
                        remarks.setText("");
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(SlimStock object) {
                        return object.getDescription();
                    }

                    @Override
                    public SlimStock fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(event -> {
            SlimStock result = event.getCompletion();
            try {
                selectedStock = StockDAO.get(result.getId());
                int av = StockDAO.countAvailable(selectedStock);
                if(av == 0) {
                    AlertDialogBuilder.messgeDialog("System Warning", "Insufficient stock.",
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                    particulars.setText("");
                    quantity.setText("");
                    particulars.requestFocus();
                    inStock.setText("In Stock: 0");
                    pending.setText("Pending: 0");
                    available.setText("Available: 0");
                    return;
                }
                for(MIRSItem added: requestItem){
                    if(added.getStockID() == result.getId()){
                        AlertDialogBuilder.messgeDialog("System Warning", "Item already added, please use edit item option instead.",
                                stackPane, AlertDialogBuilder.WARNING_DIALOG);
                        particulars.setText("");
                        quantity.setText("");
                        particulars.requestFocus();
                        inStock.setText("In Stock: 0");
                        pending.setText("Pending: 0");
                        available.setText("Available: 0");
                        return;
                    }
                }

                inStock.setText("In Stock: "+ selectedStock.getQuantity());
                pending.setText("Pending: "+ StockDAO.countPendingRequest(selectedStock));
                available.setText("Available: "+ av);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }
}
