package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MR;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewMRItemHistoryController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField desc_tf, stock_id_tf, cost_tf, qty_tf;

    @FXML
    private TableView mr_items_table;


    private MR mr = null;

    private ObservableList<MR> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mr = Utility.getSelectedMR();
        this.setMRs();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    public void initializeTable() {
        TableColumn<MR, String> column1 = new TableColumn<>("MR ID");
        column1.setMinWidth(174);
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column2 = new TableColumn<>("Employeee Name");
        column2.setMinWidth(175);
        column2.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column3 = new TableColumn<>("LastName");
        column3.setMinWidth(175);
        column3.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column4 = new TableColumn<>("Date of MR");
        column4.setMinWidth(125);
        column4.setCellValueFactory(new PropertyValueFactory<>("dateOfMR"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column5 = new TableColumn<>("Date Returned");
        column5.setMinWidth(125);
        column5.setCellValueFactory(new PropertyValueFactory<>("dateOfReturn"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column6 = new TableColumn<>("Status");
        column6.setMinWidth(100);
        column6.setCellValueFactory(new PropertyValueFactory<>("status"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column7 = new TableColumn<>("Remarks");
        column7.setMinWidth(150);
        column7.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        column7.setStyle("-fx-alignment: center;");



        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column2);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        this.mr_items_table.getColumns().add(column5);
        this.mr_items_table.getColumns().add(column6);
        this.mr_items_table.getColumns().add(column7);
    }

    public void setMRs(){
        this.desc_tf.setText(this.mr.getExtItem());
        this.stock_id_tf.setText(this.mr.getStockId());
        this.cost_tf.setText(this.mr.getPrice()+"");
        this.qty_tf.setText(this.mr.getQuantity()+"");
        this.initializeTable();
        this.populateTable();
    }

    public void populateTable() {
        try {
            Platform.runLater(() -> {
                try {
                    mrItems = FXCollections.observableList(MrDAO.getMRsByDescription(this.mr.getExtItem(), this.mr.getStockId()));
                    this.mr_items_table.getItems().setAll(mrItems);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
