package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputValidation;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
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
    private HBox btnHolder;

    @FXML
    private TextArea details, purpose;

    @FXML
    private JFXTextField particulars, remarks, quantity;

    @FXML
    private Label available, inStock, pending;

    @FXML
    private Label mirsNum, date, requisitioner, dm, gm;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, unitCol, particularsCol, remarksCol, editCol, deleteCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;
    @FXML

    private TableView particularsTable;
    private Stock selectedStock = null;
    private ObservableList<MIRSItem> requestItem = null;

    private boolean isEditingItem = false;
    private MIRSItem selectedMirsItem;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindParticularsAutocomplete(particulars);
        InputValidation.restrictNumbersOnly(quantity);
        try {
            MIRS mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSItem> mirsItemList = MirsDAO.getItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

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
                    return new SimpleStringProperty(StockDAO.get(cellData.getValue().getStockID()).getStockName());
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

            editCol.setStyle("-fx-alignment: center;");
            Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> editBtn
                    = //
                    new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                        @Override
                        public TableCell call(final TableColumn<MIRSItem, String> param) {
                            final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                                Button btn = new Button("");
                                FontIcon icon = new FontIcon("mdi2f-file-document-edit");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                    btn.setStyle("-fx-background-color: #2196f3");
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
                                                selectedStock = StockDAO.get(selectedMirsItem.getStockID());
                                                int av = StockDAO.countAvailable(selectedStock);
                                                particulars.setText(selectedStock.getStockName());
                                                quantity.setText(""+ selectedMirsItem.getQuantity());
                                                remarks.setText(selectedMirsItem.getRemarks());
                                                inStock.setText("In Stock: "+ selectedStock.getQuantity());
                                                pending.setText("Pending: "+ StockDAO.countPendingRequest(selectedStock));
                                                available.setText("Available: "+ av);
                                                System.err.println(selectedStock.getId());
                                                isEditingItem = true;
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
            editCol.setCellFactory(editBtn);

            deleteCol.setStyle("-fx-alignment: center;");
            Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> removeBtn
                    = //
                    new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                        @Override
                        public TableCell call(final TableColumn<MIRSItem, String> param) {
                            final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                                Button btn = new Button("");
                                FontIcon icon = new FontIcon("mdi2d-delete");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                    btn.setStyle("-fx-background-color: #f44336");
                                    btn.setGraphic(icon);
                                    btn.setGraphicTextGap(5);
                                    btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        btn.setOnAction(event -> {
                                            MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                            try {
                                                requestItem.remove(mirsItem);
                                                particularsTable.setItems(requestItem);
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
            deleteCol.setCellFactory(removeBtn);
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
            Utility.getActiveMIRS().setStatus("Closed");
            Utility.getActiveMIRS().setDetails(details.getText());
            MirsDAO.update(Utility.getActiveMIRS());

            for (MIRSItem mirsItem : requestItem){
                Releasing releasing = new Releasing();
                releasing.setStockID(mirsItem.getStockID());
                releasing.setMirsID(mirsItem.getMirsID());
                releasing.setQuantity(mirsItem.getQuantity());
                releasing.setPrice(mirsItem.getPrice());
                releasing.setUserID(ActiveUser.getUser().getId());
                releasing.setStatus("Released");
                ReleasingDAO.add(releasing);
                Stock temp = StockDAO.get(mirsItem.getStockID()); //temp stock object for quantity deduction
                StockDAO.deductStockQuantity(temp, mirsItem.getQuantity());
            }

            AlertDialogBuilder.messgeDialog("System Message", "MIRS items released.", stackPane, AlertDialogBuilder.INFO_DIALOG);
            btnHolder.setDisable(true);
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

            if(isEditingItem){
                for(MIRSItem added: requestItem) {
                    System.err.println(added.getStockID()+", "+selectedStock.getId());
                    if (added.getStockID().equals(selectedStock.getId()) ) {
                        requestItem.remove(selectedMirsItem);
                        particularsTable.setItems(requestItem);
                        break;
                    }
                }
            }

            MIRSItem mirsItem = new MIRSItem();
            mirsItem.setMirsID(mirsNum.getText());
            mirsItem.setStockID(selectedStock.getId());
            mirsItem.setParticulars(selectedStock.getStockName());
            mirsItem.setUnit(selectedStock.getUnit());
            mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
            mirsItem.setPrice(selectedStock.getPrice());
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
                        return object.getStockName();
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
