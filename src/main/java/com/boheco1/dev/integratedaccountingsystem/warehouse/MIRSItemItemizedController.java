package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ItemizedMirsItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSItemItemizedController implements Initializable {

    @FXML
    private Label itemName;

    @FXML
    private JFXTextField brand;

    @FXML
    private JFXTextField serial;

    @FXML
    private JFXTextArea remarks;

    @FXML
    private Label charCounter;

    @FXML
    private JFXButton clearBtn;

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton removeBtn;

    @FXML
    private TableView<ItemizedMirsItem> itemizedItemTable;

    @FXML
    private JFXButton saveBtn;

    private MIRSItem mirsItem;
    private ObservableList<ItemizedMirsItem> itemized;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mirsItem = (MIRSItem) Utility.getSelectedObject();
            remarks.setTextFormatter(new TextFormatter<String>(change ->
                    change.getControlNewText().length() <= 200 ? change : null));

            remarks.setOnKeyTyped( event -> {
                charCounter.setText(remarks.getText().length()+"/200");
            } );

            itemName.setText(StockDAO.get(mirsItem.getStockID()).getDescription());
            saveBtn.setDisable(true);
            prepareTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    void addToTable(ActionEvent event) {
        if(brand.getText().isEmpty() || serial.getText().isEmpty()){
            AlertDialogBuilder.messgeDialog("Input validation", "Please provide all required information, and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }
        ItemizedMirsItem item= new ItemizedMirsItem(Utility.generateRandomId(), mirsItem.getId(), serial.getText(), brand.getText(), remarks.getText());
        itemized.add(item);
        itemizedItemTable.refresh();
        clearFields(null);

        if(itemized.size() == mirsItem.getQuantity()){
            addBtn.setDisable(true);
            saveBtn.setDisable(false);
        }


    }

    @FXML
    void clearFields(ActionEvent event) {
        brand.setText("");
        serial.setText("");
        remarks.setText("");
        charCounter.setText(remarks.getText().length()+"/200");
    }

    @FXML
    void removeFromTable(ActionEvent event) {
        if(itemizedItemTable.getSelectionModel().getSelectedItem() != null){
            addBtn.setDisable(false);
            saveBtn.setDisable(true);
            ItemizedMirsItem selected = itemizedItemTable.getSelectionModel().getSelectedItem();
            itemized.remove(selected);
            itemizedItemTable.refresh();
            clearFields(null);
        }else{
            AlertDialogBuilder.messgeDialog("System Message", "No table item selected.",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }
    }

    @FXML
    void saveItemizedItem(ActionEvent event) throws Exception {
        for(ItemizedMirsItem item : itemizedItemTable.getItems()){
            HashMap<String, ItemizedMirsItem> holder = Utility.getItemizedMirsItems();
            if(holder.containsKey(item.getId())){
                holder.replace(item.getId(), item);
            }else {
                holder.put(item.getId(), item);
            }
        }

        AlertDialogBuilder.messgeDialog("System Message", "Mirs item was successfully itemized.",
                Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);

        clearFields(null);
        saveBtn.setDisable(true);
        clearBtn.setDisable(true);
        addBtn.setDisable(true);
        removeBtn.setDisable(true);
    }

    @FXML
    void displayInfoWhenTableIsClick(MouseEvent event) {
        ItemizedMirsItem item = itemizedItemTable.getSelectionModel().getSelectedItem();
        brand.setText(item.getBrand());
        serial.setText(item.getSerial());
        remarks.setText(item.getRemarks());
        charCounter.setText(remarks.getText().length()+"/200");
    }

    private void prepareTable(){
        TableColumn<ItemizedMirsItem, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("Brand"));
        itemized = FXCollections.observableArrayList();
        itemizedItemTable.setItems(itemized);

        itemizedItemTable.getColumns().add(brandCol);
    }

}
