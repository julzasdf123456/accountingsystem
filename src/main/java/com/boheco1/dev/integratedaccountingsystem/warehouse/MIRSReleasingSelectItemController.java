package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.*;

public class MIRSReleasingSelectItemController implements Initializable {

    @FXML
    private JFXComboBox<SlimStock> comboBoxBrand;

    @FXML
    private TextField serial;

    @FXML
    private TextArea description;

    @FXML
    private TextField remarks;


    @FXML
    private Label requestQtyLabel;

    @FXML
    private Label availableLabel;

    @FXML
    private TableView<ItemizedMirsItem> itemizedItemTable;

    private ObjectTransaction parentController = null;
    private MIRSItem mirsItem;
    private List<SlimStock> comboBoxItem;
    private SlimStock currentSelection;

    private ObservableList<ItemizedMirsItem> itemizedItemObservableList;
    ObservableList<ItemizedMirsItem> brand;
    private double maxQtyAllowed = 0;
    private double maxItemPerBrand = 0;
    private int counter=0;

    //private HashMap<String, Double> selected_items;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Object obj = Utility.getSelectedObject();
            if (obj instanceof MIRSItem) {
                mirsItem = (MIRSItem) obj;
                boolean mirsItemIscontrolled = StockDAO.get(mirsItem.getStockID()).isControlled();
                comboBoxItem = StockDAO.getByDescription(StockDAO.get(mirsItem.getStockID()).getDescription(), mirsItemIscontrolled);
                description.setText(mirsItem.getParticulars());
                maxQtyAllowed = mirsItem.getQuantity();
                requestQtyLabel.setText("Request: "+maxQtyAllowed);
                //selected_items = (HashMap<String, Double>) Utility.getDictionary();

                System.out.println(mirsItemIscontrolled+": "+mirsItem.getParticulars());
                parentController = Utility.getParentController();

                itemizedItemObservableList =  FXCollections.observableArrayList();
                brand = FXCollections.observableArrayList();
                for (SlimStock slimStock : comboBoxItem){
                    ItemizedMirsItem itemizedMirsItem = new ItemizedMirsItem();
                    itemizedMirsItem.setBrand(slimStock.getBrand());
                    //System.out.println(slimStock.getDescription());
                    comboBoxBrand.getItems().add(slimStock);
                    brand.add(itemizedMirsItem);
                }

                for (int i = 0;i<maxQtyAllowed;i++){
                    ItemizedMirsItem itemizedMirsItem = new ItemizedMirsItem();
                    itemizedItemObservableList.add(itemizedMirsItem);
                }

                initializedTable();

            }else{
                AlertDialogBuilder.messgeDialog("System Message", "Object received is not a MIRSItem", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializedTable(){


        TableColumn<ItemizedMirsItem, String> column1 = new TableColumn<>("Brand");
        column1.setCellValueFactory(new PropertyValueFactory<>("brand"));
        column1.setStyle("-fx-alignment: center-left;");
        column1.setCellFactory(col -> new CustomTableCell(brand));

        TableColumn<ItemizedMirsItem, String> column2 = new TableColumn<>("Serial");
        column2.setCellValueFactory(new PropertyValueFactory<>("serial"));
        column2.setStyle("-fx-alignment: center-left;");
        column2.setCellFactory(TextFieldTableCell.forTableColumn());
        column2.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setSerial(event.getNewValue());
            // Prevent the table from losing focus
            event.getTableView().requestFocus();
        });
        column2.setEditable(true);

        TableColumn<ItemizedMirsItem, String> column3 = new TableColumn<>("Remarks");
        column3.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        column3.setStyle("-fx-alignment: center-left;");
        column3.setCellFactory(TextFieldTableCell.forTableColumn());
        column3.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setRemarks(event.getNewValue());
            // Prevent the table from losing focus
            event.getTableView().requestFocus();
        });
        column3.setEditable(true);

        itemizedItemTable.getSelectionModel().setCellSelectionEnabled(true);
        itemizedItemTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        itemizedItemTable.getColumns().add(column1);
        itemizedItemTable.getColumns().add(column2);
        itemizedItemTable.getColumns().add(column3);
        itemizedItemTable.setItems(itemizedItemObservableList);
        itemizedItemTable.refresh();
    }

    @FXML
    void saveToTable(ActionEvent event) {

        for(ItemizedMirsItem i : itemizedItemObservableList){
            System.out.println(i.getBrand() +", "+ i.getSerial() +", "+ i.getRemarks());
        }
        /*
        try{

            if(itemizedItemObservableList.size() == maxQtyAllowed){
                AlertDialogBuilder.messgeDialog("System Message", "Number of request quantity all ready fulfilled.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }
            if(comboBoxBrand.getSelectionModel().getSelectedItem() == null) {
                AlertDialogBuilder.messgeDialog("System Message", "Please select a brand.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }else if(serial.getText().isEmpty()) {
                AlertDialogBuilder.messgeDialog("System Message", "Serial number is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }else if(maxItemPerBrand <= 0) {
                AlertDialogBuilder.messgeDialog("System Message", "Out of stock.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }else{
                //double q = Double.parseDouble(qty.getText());

                pataka lang ko ani nga code

                //if(q > mirsItem.getQuantity()) {//check quantity of current item
               //     AlertDialogBuilder.messgeDialog("System Message", "Maximum allowed quantity is: " + Utility.formatDecimal(mirsItem.getQuantity()), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
               // }else if(q > maxItem ){
               //     AlertDialogBuilder.messgeDialog("System Message", "Insufficient stock, please try again.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
               // }else{
                    MIRSItem mirsItem = new MIRSItem();
                    mirsItem.setParticulars(this.mirsItem.getParticulars());
                    mirsItem.setId(this.mirsItem.getId());
                    mirsItem.setMirsID(this.mirsItem.getMirsID());

                    mirsItem.setStockID(this.mirsItem.getStockID());//the stock ID of the request item
                    mirsItem.setActualStockId(comboBoxBrand.getSelectionModel().getSelectedItem().getId());//the actual stock ID of the released item
                    mirsItem.setQuantity(1);
                    mirsItem.setPrice(this.comboBoxBrand.getSelectionModel().getSelectedItem().getPrice());
                    mirsItem.setRemarks(this.mirsItem.getRemarks());
                    mirsItem.setCreatedAt(this.mirsItem.getCreatedAt());
                    mirsItem.setUpdatedAt(this.mirsItem.getUpdatedAt());
                    mirsItem.setAdditional(false);

                    //Start attributes from ItemizedMirsItem
                    mirsItem.setBrand(comboBoxBrand.getSelectionModel().getSelectedItem().getBrand());
                    mirsItem.setSerial(serial.getText());
                    mirsItem.setRemarks(remarks.getText());
                    //End attributes from ItemizedMirsItem

                    itemizedItemObservableList.add(mirsItem);
                    System.out.println(itemizedItemObservableList.size() +" > "+maxQtyAllowed);
                    itemizedItemTable.refresh();
                    //update quantity of the selected item for releasing


                    currentSelection.setQuantity(currentSelection.getQuantity()-1);
                    serial.setText("");
                    remarks.setText("");
                    comboBoxBrand.getSelectionModel().clearSelection();
                    //qty.setPromptText("");
                    //qty.setText("");
                    //description.setText("");
                   // price.setText("");

                    //parentController.receive(mirsItem);
                    //tring key = this.brand.getSelectionModel().getSelectedItem().getId();
                    //if (selected_items.containsKey(key)){
                   //     double value = selected_items.get(key);
                   //     double new_qty = value + 1;
                   //     selected_items.put(key, new_qty);
                   // }else{
                   //     selected_items.put(key, 1.0);
                   // }
                  //  brand.valueProperty().set(null);
                counter++; //count how many items are added in the table
                maxItemPerBrand = 0;
                availableLabel.setText("Available: "+maxItemPerBrand);
                   // ModalBuilderForWareHouse.MODAL_CLOSE();
                //}

            }
        }catch (NumberFormatException e){
            AlertDialogBuilder.messgeDialog("System Message", "Invalid quantity, please try again.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
        catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Message", "Error encounter: "+ e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }*/
    }

    @FXML
    private void selectBrand(ActionEvent event) {
        if(comboBoxBrand.getSelectionModel().getSelectedIndex() != -1) {
            currentSelection = comboBoxItem.get(comboBoxBrand.getSelectionModel().getSelectedIndex());
            //price.setText(String.format("%,.2f", currentSelection.getPrice()));
            //description.setText(currentSelection.getDescription());

            //qty.setPromptText("Max. value " + Utility.formatDecimal(mirsItem.getQuantity()));
            maxItemPerBrand = currentSelection.getQuantity();
            availableLabel.setText("Available: "+maxItemPerBrand);
            //qty.setText("");
            //qty.setDisable(maxItem <= 0);
        }
    }


    @FXML
    private void submitForReleasing(ActionEvent event) {
        this.mirsItem.setQuantity(this.mirsItem.getQuantity()-counter);//deduct requested quantity base on how many items where added in the table
        parentController.receive(itemizedItemObservableList);
        ModalBuilderForWareHouse.MODAL_CLOSE();
    }

}
