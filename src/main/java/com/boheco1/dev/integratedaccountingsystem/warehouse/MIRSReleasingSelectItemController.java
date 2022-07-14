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
    private JFXComboBox<String> brand;

    @FXML
    private TextField price;

    @FXML
    private TextArea description;

    @FXML
    private TextField qty;

    private ObjectTransaction parentController = null;
    private MIRSItem mirsItem;
    private List<SlimStock> items;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mirsItem = (MIRSItem) Utility.getSelectedObject();
            items = StockDAO.getByDescription(StockDAO.get(mirsItem.getStockID()).getDescription());
            parentController = Utility.getParentController();
            for (SlimStock slimStock : items){
                brand.getItems().add(slimStock.getBrand().toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void send(ActionEvent event) {
        parentController.receive(mirsItem);
    }

    @FXML
    void selectBrand(ActionEvent event) {
        SlimStock stock = items.get(brand.getSelectionModel().getSelectedIndex());
        price.setText(String.format("%,.2f",stock.getPrice()));
        description.setText(stock.getDescription());
    }

}
