package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StockController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private ListView<Stock> stockListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Stock> stockList = new ArrayList<>();
        stockList.add(new Stock("Digital Meter",45,4000));
        stockList.add(new Stock("Wire Cutter",50,600));
        stockList.add(new Stock("Hard Hat",100,900));
        stockList.add(new Stock("Copper Wire",455,4000));
        stockList.add(new Stock("Steel Bolt",500,600));
        stockList.add(new Stock("Cover All Suite",132,900));


        stockListView.getItems().clear();
        stockListView.getItems().addAll(stockList);
        stockListView.setCellFactory(list -> new StockListController());
    }

    @FXML
    private void newStockBtn(ActionEvent event) {
        Node node = ContentHandler.getNodeFromFxml(getClass(),"../warehouse_stock_entry.fxml");
        Utility.getContentPane().getChildren().setAll(node);
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
}