package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchConsumerController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private TableView<ConsumerInfo> consumersTable;
    private ObservableList<ConsumerInfo> consumers = null;
    private ConsumerInfo consumerInfo = null;
    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.consumersTable.setRowFactory(tv -> {
            TableRow<ConsumerInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    consumerInfo = row.getItem();
                    this.parentController.receive(consumerInfo);
                }
            });
            return row ;
        });
        this.parentController = Utility.getParentController();
        this.query_tf.requestFocus();
    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void search(){
        String query = this.query_tf.getText();
        try {
            this.consumers = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecords(query));
            this.consumersTable.setItems(this.consumers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the consumers table
     * @return void
     */
    public void createTable() {

        TableColumn<ConsumerInfo, String> column1 = new TableColumn<>("Account No");
        column1.setMinWidth(123);
        column1.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ConsumerInfo, String> column2 = new TableColumn<>("Consumer Name");
        column2.setMinWidth(175);
        column2.setCellValueFactory(new PropertyValueFactory<>("consumerName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<ConsumerInfo, String> column3 = new TableColumn<>("Address");
        column3.setMinWidth(250);
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<ConsumerInfo, String> column4 = new TableColumn<>("Account Type");
        column4.setMinWidth(125);
        column4.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<ConsumerInfo, String> column5 = new TableColumn<>("Status");
        column5.setMinWidth(125);
        column5.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));
        column5.setStyle("-fx-alignment: center;");


        this.consumers = FXCollections.observableArrayList();
        this.consumersTable.setPlaceholder(new Label("No consumer records was searched."));

        this.consumersTable.getColumns().add(column1);
        this.consumersTable.getColumns().add(column2);
        this.consumersTable.getColumns().add(column3);
        this.consumersTable.getColumns().add(column4);
        this.consumersTable.getColumns().add(column5);
    }
}
