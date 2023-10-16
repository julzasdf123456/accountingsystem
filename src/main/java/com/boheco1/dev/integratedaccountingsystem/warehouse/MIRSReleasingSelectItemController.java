package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

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
    private TableView<MIRSItem> transformerTable;

    private ObjectTransaction parentController = null;
    private MIRSItem mirsItem;
    private List<SlimStock> comboBoxItem;
    private SlimStock currentSelection;

    private ObservableList<MIRSItem> transformerTableItem;

    private double maxQtyAllowed = 0;
    private double maxItemPerBrand = 0;

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
                for (SlimStock slimStock : comboBoxItem){
                    //System.out.println(slimStock.getBrand()+":"+slimStock.getPrice());
                    /*if(slimStock.isControlled()) {
                        String key = slimStock.getId();
                        double value = 0;
                        if (selected_items.containsKey(key)) {
                            value = selected_items.get(key);
                            slimStock.setQuantity(slimStock.getQuantity() - value);
                        }
                    }*/
                    System.out.println(slimStock.getDescription());
                    comboBoxBrand.getItems().add(slimStock);
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
        TableColumn<MIRSItem, String> column1 = new TableColumn<>("Brand");
        column1.setCellValueFactory(new PropertyValueFactory<>("brand"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MIRSItem, String> column2 = new TableColumn<>("Serial");
        column2.setCellValueFactory(new PropertyValueFactory<>("serial"));
        column2.setStyle("-fx-alignment: center-left;");

        transformerTable.getColumns().add(column1);
        transformerTable.getColumns().add(column2);
        transformerTableItem =  FXCollections.observableArrayList();
        transformerTable.setItems(transformerTableItem);
    }

    @FXML
    void saveToTable(ActionEvent event) {

        try{

            if(transformerTableItem.size() == maxQtyAllowed){
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


                /*if(q > mirsItem.getQuantity()) {//check quantity of current item
                    AlertDialogBuilder.messgeDialog("System Message", "Maximum allowed quantity is: " + Utility.formatDecimal(mirsItem.getQuantity()), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else if(q > maxItem ){
                    AlertDialogBuilder.messgeDialog("System Message", "Insufficient stock, please try again.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else{*/
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

                    transformerTableItem.add(mirsItem);
                    System.out.println(transformerTableItem.size() +" > "+maxQtyAllowed);
                    transformerTable.refresh();
                    //update quantity of the selected item for releasing
                    this.mirsItem.setQuantity(this.mirsItem.getQuantity()-1);

                    currentSelection.setQuantity(currentSelection.getQuantity()-1);
                    serial.setText("");
                    remarks.setText("");
                    comboBoxBrand.getSelectionModel().clearSelection();
                    //qty.setPromptText("");
                    //qty.setText("");
                    //description.setText("");
                   // price.setText("");

                    //parentController.receive(mirsItem);
                    /*String key = this.brand.getSelectionModel().getSelectedItem().getId();
                    if (selected_items.containsKey(key)){
                        double value = selected_items.get(key);
                        double new_qty = value + 1;
                        selected_items.put(key, new_qty);
                    }else{
                        selected_items.put(key, 1.0);
                    }
                    brand.valueProperty().set(null);*/
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
        }
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
        parentController.receive(transformerTableItem);
        ModalBuilderForWareHouse.MODAL_CLOSE();
    }

}
