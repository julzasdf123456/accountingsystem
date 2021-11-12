package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
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
        try {
            List<Stock> stockList = StockDAO.getList(50,0);
            stockListView.getItems().clear();
            stockListView.getItems().addAll(stockList);
            stockListView.setCellFactory(list -> new StockListController());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
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
