package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.io.IOException;

public class StockListController extends ListCell<Stock> {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label stockName;
    @FXML
    private Label qty;
    @FXML
    private Label price;


    @FXML
    private JFXButton updateBtn;
    @FXML
    private JFXButton trashBtn;
    @FXML
    private JFXButton logsBtn;

    private Stock model;
    private FXMLLoader mLLoader;


    @FXML
    private void logsBtn(ActionEvent event) {
        JOptionPane.showMessageDialog(null,"Logs");
    }

    @FXML
    private void trashBtn(ActionEvent event) {

        JOptionPane.showMessageDialog(null,"Trash");
    }

    @FXML
    private void updateBtn(ActionEvent event) {
        JOptionPane.showMessageDialog(null,"Update");
    }

    @Override
    protected void updateItem(Stock model, boolean empty) {
        super.updateItem(model, empty);
        updateSelected(false);
        this.model = model;

        if(empty || model == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("../warehouse_stock_list.fxml"));

                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            stockName.setText(model.getStockName().toUpperCase());
            qty.setText("Qty: "+model.getQuantity());
            price.setText("Price: "+model.getPrice());
            setText(null);
            setGraphic(anchorPane);
        }
    }
}
