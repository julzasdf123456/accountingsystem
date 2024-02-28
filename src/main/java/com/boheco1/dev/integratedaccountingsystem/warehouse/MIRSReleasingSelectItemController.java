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

    private TableColumn<ItemizedMirsItem, String> columnBrand;
    private TableColumn<ItemizedMirsItem, String> columnSerial;
    private TableColumn<ItemizedMirsItem, String> columnRemarks;

    private ObjectTransaction parentController = null;
    private MIRSItem mirsItem;
    private List<SlimStock> comboBoxItem;
    private SlimStock currentSelection;

    private ObservableList<ItemizedMirsItem> itemizedItemObservableList;
    ObservableList<ItemizedMirsItem> brand;
    private double requestedQty = 0;
    private double maxItemPerBrand = 0;
    private int counter=0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            parentController = Utility.getParentController();
            Object obj = Utility.getSelectedObject();
            if (obj instanceof MIRSItem) {
                mirsItem = (MIRSItem) obj;
                boolean mirsItemIscontrolled = StockDAO.get(mirsItem.getStockID()).isControlled();
                comboBoxItem = StockDAO.getByDescription(StockDAO.get(mirsItem.getStockID()).getDescription(), mirsItemIscontrolled);
                description.setText(mirsItem.getParticulars());
                requestedQty = mirsItem.getQuantity();
                //requestQtyLabel.setText("Request: "+maxQtyAllowed);
                //selected_items = (HashMap<String, Double>) Utility.getDictionary();

                itemizedItemObservableList =  FXCollections.observableArrayList();
                brand = FXCollections.observableArrayList();
                for (SlimStock slimStock : comboBoxItem){
                    ItemizedMirsItem itemizedMirsItem = new ItemizedMirsItem();
                    itemizedMirsItem.setBrand(slimStock.getBrand());
                    itemizedMirsItem.setId(slimStock.getId());
                    itemizedMirsItem.setPrice(slimStock.getPrice());
                    //comboBoxBrand.getItems().add(slimStock);
                    brand.add(itemizedMirsItem);
                }

                for (int i = 0; i< requestedQty; i++){
                    //Add blank item in the table
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

        columnBrand = new TableColumn<>("Brand");
        columnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        columnBrand.setStyle("-fx-alignment: center-left;");
        columnBrand.setCellFactory(col -> new BrandTableCell(brand));

        columnSerial = new TableColumn<>("Serial");
        columnSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        columnSerial.setStyle("-fx-alignment: center-left;");
        columnSerial.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSerial.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setSerial(event.getNewValue());
            // Prevent the table from losing focus
            event.getTableView().requestFocus();
        });
        columnSerial.setEditable(true);

        columnRemarks = new TableColumn<>("Remarks");
        columnRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        columnRemarks.setStyle("-fx-alignment: center-left;");
        columnRemarks.setCellFactory(TextFieldTableCell.forTableColumn());
        columnRemarks.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setRemarks(event.getNewValue());
            // Prevent the table from losing focus
            event.getTableView().requestFocus();
        });
        columnRemarks.setEditable(true);

        itemizedItemTable.getSelectionModel().setCellSelectionEnabled(true);
        itemizedItemTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        itemizedItemTable.getColumns().add(columnBrand);
        itemizedItemTable.getColumns().add(columnSerial);
        itemizedItemTable.getColumns().add(columnRemarks);
        itemizedItemTable.setItems(itemizedItemObservableList);
        itemizedItemTable.refresh();
    }


    @FXML
    private void submitForReleasing(ActionEvent event) {
        ObservableList<MIRSItem> mirsItems =  FXCollections.observableArrayList();
       for(ItemizedMirsItem i : itemizedItemObservableList){
            if(i.getBrand() == null || i.getSerial() == null) continue;


            MIRSItem mirsItem = new MIRSItem();
            mirsItem.setParticulars(this.mirsItem.getParticulars());
            mirsItem.setId(this.mirsItem.getId());
            mirsItem.setMirsID(this.mirsItem.getMirsID());

            mirsItem.setStockID(this.mirsItem.getStockID());//the stock ID of the request item
            mirsItem.setActualStockId(i.getId());//the actual stock ID of the released item
            mirsItem.setQuantity(1);
            mirsItem.setPrice(i.getPrice());
            mirsItem.setRemarks(this.mirsItem.getRemarks());
            mirsItem.setCreatedAt(this.mirsItem.getCreatedAt());
            mirsItem.setUpdatedAt(this.mirsItem.getUpdatedAt());
            mirsItem.setAdditional(false);

            //Start attributes from ItemizedMirsItem
            mirsItem.setBrand(i.getBrand());
            mirsItem.setSerial(i.getSerial());
            mirsItem.setRemarks(i.getRemarks());
            //End attributes from ItemizedMirsItem

            mirsItems.add(mirsItem);
            counter++; //count how many items contains a serial number
        }

        this.mirsItem.setQuantity(this.mirsItem.getQuantity()-counter);//deduct requested quantity base on how many items where added in the table
        parentController.receive(mirsItems);
        ModalBuilderForWareHouse.MODAL_CLOSE();
    }

}
