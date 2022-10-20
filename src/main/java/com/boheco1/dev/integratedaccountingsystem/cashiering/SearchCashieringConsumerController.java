package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.CRMQueue;
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

public class SearchCashieringConsumerController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private TableView<CRMQueue> consumersTable;
    private ObservableList<CRMQueue> consumers = null;
    private CRMQueue consumerInfo = null;
    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.consumersTable.setRowFactory(tv -> {
            TableRow<CRMQueue> row = new TableRow<>();
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
            this.consumers = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecordFromCRM(query));
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

        TableColumn<CRMQueue, String> column1 = new TableColumn<>("Consumer Name");
        column1.setMinWidth(200);
        column1.setMaxWidth(200);
        column1.setPrefWidth(200);
        column1.setCellValueFactory(new PropertyValueFactory<>("consumerName"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<CRMQueue, String> column2 = new TableColumn<>("Address");
        column2.setMinWidth(200);
        column2.setMaxWidth(200);
        column2.setPrefWidth(200);
        column2.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));
        column2.setStyle("-fx-alignment: center-left;");


        TableColumn<CRMQueue, String> column3 = new TableColumn<>("Purpose");
        column3.setCellValueFactory(new PropertyValueFactory<>("transactionPurpose"));
        column3.setStyle("-fx-alignment: center-left;");


        this.consumers = FXCollections.observableArrayList();
        this.consumersTable.setPlaceholder(new Label("No consumer records was searched."));

        this.consumersTable.getColumns().add(column1);
        this.consumersTable.getColumns().add(column2);
        this.consumersTable.getColumns().add(column3);
    }
}
