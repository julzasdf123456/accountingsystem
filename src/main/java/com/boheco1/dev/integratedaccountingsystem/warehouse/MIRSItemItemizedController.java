package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ItemizedMirsItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
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
import java.util.Map;
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
    private ItemizedMirsItem selectedAnItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Object obj = Utility.getSelectedObject();
            if (obj instanceof MIRSItem) {
                mirsItem = (MIRSItem) obj;
            }
            remarks.setTextFormatter(new TextFormatter<String>(change ->
                    change.getControlNewText().length() <= 200 ? change : null));

            remarks.setOnKeyTyped( event -> {
                charCounter.setText(remarks.getText().length()+"/200");
            } );
            Stock stock = StockDAO.get(mirsItem.getStockID());
            brand.setText(stock.getBrand());
            brand.setEditable(false);
            itemName.setText(stock.getDescription());
            saveBtn.setDisable(true);
            prepareTable();


            HashMap<String, ItemizedMirsItem> holder = Utility.getItemizedMirsItems();
            for (Map.Entry i : holder.entrySet()) {
                ItemizedMirsItem item = holder.get(i.getKey());
                if(item.getStockID().equals(mirsItem.getStockID())){
                        itemized.add(item);
                }
            }
            itemizedItemTable.refresh();

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
        if(selectedAnItem != null){
            selectedAnItem.setBrand(brand.getText());
            selectedAnItem.setSerial(serial.getText());
            selectedAnItem.setRemarks(remarks.getText());
        }else{
            ItemizedMirsItem item= new ItemizedMirsItem(Utility.generateRandomId(), mirsItem.getStockID(), mirsItem.getId(), serial.getText(), brand.getText(), remarks.getText());
            itemized.add(item);
            clearFields(null);
        }

        selectedAnItem = null;

        if(itemized.size() == mirsItem.getQuantity()){
            addBtn.setDisable(true);
            saveBtn.setDisable(false);
        }
        itemizedItemTable.refresh();
    }

    @FXML
    void clearFields(ActionEvent event) {
        selectedAnItem = null;
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
        selectedAnItem = itemizedItemTable.getSelectionModel().getSelectedItem();
        brand.setText(selectedAnItem.getBrand());
        serial.setText(selectedAnItem.getSerial());
        remarks.setText(selectedAnItem.getRemarks());
        charCounter.setText(remarks.getText().length()+"/200");
    }

    private void prepareTable(){
        TableColumn<ItemizedMirsItem, String> brandCol = new TableColumn<>("Serial");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("Serial"));
        itemized = FXCollections.observableArrayList();
        itemizedItemTable.setItems(itemized);

        itemizedItemTable.getColumns().add(brandCol);
    }

}
