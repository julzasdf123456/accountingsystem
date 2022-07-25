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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mirsItem = (MIRSItem) Utility.getSelectedObject();
            items = StockDAO.getByDescription(StockDAO.get(mirsItem.getStockID()).getDescription());
            parentController = Utility.getParentController();
            for (SlimStock slimStock : items){
                brand.getItems().add(slimStock);
            }
            qty.setPromptText("Max. value "+mirsItem.getQuantity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void send(ActionEvent event) {

        try{
            if(qty.getText().isEmpty()) {
                AlertDialogBuilder.messgeDialog("System Message", "Quantity is required.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else{
                int q = Integer.parseInt(qty.getText());
                if(q > mirsItem.getQuantity()){
                    AlertDialogBuilder.messgeDialog("System Message", "Invalid quantity, please try again.", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else{
                    MIRSItem mirsItem = new MIRSItem();
                    mirsItem.setId(this.mirsItem.getId());
                    mirsItem.setMirsID(this.mirsItem.getMirsID());
                    mirsItem.setStockID(this.brand.getSelectionModel().getSelectedItem().getId());
                    mirsItem.setQuantity(q);
                    mirsItem.setPrice(this.mirsItem.getPrice());
                    mirsItem.setRemarks(this.mirsItem.getRemarks());
                    mirsItem.setCreatedAt(this.mirsItem.getCreatedAt());
                    mirsItem.setUpdatedAt(this.mirsItem.getUpdatedAt());
                    mirsItem.setAdditional(false);

                    this.mirsItem.setQuantity(this.mirsItem.getQuantity()-q);
                    qty.setPromptText("Max. value "+this.mirsItem.getQuantity());
                    qty.setText("");
                    parentController.receive(mirsItem);
                    if(this.mirsItem.getQuantity() == 0){
                        sendBtn.setDisable(true);
                    }
                }

            }
        }catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Message", "Error encounter: "+ e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    void selectBrand(ActionEvent event) {
        currentSelection = items.get(brand.getSelectionModel().getSelectedIndex());
        price.setText(String.format("%,.2f",currentSelection.getPrice()));
        description.setText(currentSelection.getDescription());
    }

}
