package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private JFXComboBox<SlimStock> brand;

    @FXML
    private TextField price;

    @FXML
    private TextArea description;

    @FXML
    private TextField qty;

    @FXML
    private JFXButton sendBtn;

    private ObjectTransaction parentController = null;
    private MIRSItem mirsItem;
    private List<SlimStock> items;
    private SlimStock currentSelection;

    private int counter = 0;
    private double maxItem = 0;

    private HashMap<String, Double> selected_items;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Object obj = Utility.getSelectedObject();
            if (obj instanceof MIRSItem) {
                mirsItem = (MIRSItem) obj;
                items = StockDAO.getByDescription(StockDAO.get(mirsItem.getStockID()).getDescription());
            }
            selected_items = (HashMap<String, Double>) Utility.getDictionary();

            parentController = Utility.getParentController();
            for (SlimStock slimStock : items){
                System.out.println(slimStock.getBrand()+":"+slimStock.getPrice());
                if(slimStock.getBrand()!=null) {
                    String key = slimStock.getId();
                    double value = 0;
                    if (selected_items.containsKey(key)) {
                        value = selected_items.get(key);
                        slimStock.setQuantity(slimStock.getQuantity() - value);
                    }
                    brand.getItems().add(slimStock);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void send(ActionEvent event) {

        try{
            if(brand.getSelectionModel().getSelectedItem() == null) {
                AlertDialogBuilder.messgeDialog("System Message", "Please select a brand.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else if(qty.getText().isEmpty()) {
                AlertDialogBuilder.messgeDialog("System Message", "Quantity is required.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else{
                double q = Double.parseDouble(qty.getText());


                if(q > mirsItem.getQuantity()) {//check quantity of current item
                    AlertDialogBuilder.messgeDialog("System Message", "Maximum allowed quantity is: " + Utility.formatDecimal(mirsItem.getQuantity()), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else if(q > maxItem ){
                    AlertDialogBuilder.messgeDialog("System Message", "Insufficient stock, please try again.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else{
                    MIRSItem mirsItem = new MIRSItem();
                    mirsItem.setParticulars(this.mirsItem.getParticulars());
                    mirsItem.setId(this.mirsItem.getId());
                    mirsItem.setMirsID(this.mirsItem.getMirsID());
                    mirsItem.setStockID(this.brand.getSelectionModel().getSelectedItem().getId());
                    mirsItem.setQuantity(q);
                    mirsItem.setPrice(this.brand.getSelectionModel().getSelectedItem().getPrice());
                    mirsItem.setRemarks(this.mirsItem.getRemarks());
                    mirsItem.setCreatedAt(this.mirsItem.getCreatedAt());
                    mirsItem.setUpdatedAt(this.mirsItem.getUpdatedAt());
                    mirsItem.setAdditional(false);

                    //update quantity of the selected item for releasing
                    this.mirsItem.setQuantity(this.mirsItem.getQuantity()-q);

                    currentSelection.setQuantity(currentSelection.getQuantity()-q);
                    qty.setPromptText("");
                    qty.setText("");
                    description.setText("");
                    price.setText("");
                    parentController.receive(mirsItem);
                    String key = this.brand.getSelectionModel().getSelectedItem().getId();
                    if (selected_items.containsKey(key)){
                        double value = selected_items.get(key);
                        double new_qty = value + q;
                        selected_items.put(key, new_qty);
                    }else{
                        selected_items.put(key, q);
                    }
                    brand.valueProperty().set(null);
                    maxItem = 0;
                    sendBtn.setDisable(this.mirsItem.getQuantity() == 0);
                    ModalBuilderForWareHouse.MODAL_CLOSE();
                }

            }
        }catch (NumberFormatException e){
            AlertDialogBuilder.messgeDialog("System Message", "Invalid quantity, please try again.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
        catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Message", "Error encounter: "+ e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    void selectBrand(ActionEvent event) {
        if(brand.getSelectionModel().getSelectedIndex() != -1) {
            currentSelection = items.get(brand.getSelectionModel().getSelectedIndex());
            price.setText(String.format("%,.2f", currentSelection.getPrice()));
            description.setText(currentSelection.getDescription());
            qty.setPromptText("Max. value " + Utility.formatDecimal(mirsItem.getQuantity()));
            maxItem = currentSelection.getQuantity();
            qty.setText("");
            qty.setDisable(maxItem <= 0);
        }
    }

}
