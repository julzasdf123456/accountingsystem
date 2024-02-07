package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class SearchConsumerController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private Label status_lbl;

    @FXML
    private TableView<ConsumerInfo> consumersTable;
    private ObservableList<ConsumerInfo> consumers = null;
    private ConsumerInfo consumerInfo = null;
    private ObjectTransaction parentController = null;
    private Task<Void> task;
    private boolean searching = false;
    private long lastPressProcessed = 0;
    private Timer t = new Timer();
    private TimerTask tt;

    public Connection con;
    public Thread searchThread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            con = DB.getConnection(Utility.DB_BILLING);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.createTable();
        this.consumersTable.setRowFactory(tv -> {
            TableRow<ConsumerInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty()) {
                    System.out.println("CLICKED");
                    consumerInfo = row.getItem();
                    this.parentController.receive(consumerInfo);
                    try {
                        if (BillDAO.getConsumerBills(consumerInfo, false, con).size() == 0) {
                            System.out.println("FETCHED");
                            this.setMessage("Consumer has no unpaid bills!");
                        } else {
                            this.setMessage("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return row ;
        });
        if (searchThread != null && searchThread.isAlive())
            searchThread.interrupt();

        this.parentController = Utility.getParentController();
        this.query_tf.requestFocus();

        this.query_tf.setOnKeyPressed(keyEvent -> {
            if (searchThread != null && searchThread.isAlive())
                searchThread.interrupt();
        });

        this.query_tf.setOnKeyReleased(keyEvent -> {
            searchThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (query_tf.getText().length() > 2) {
                        String query = query_tf.getText();
                        try {
                            consumers = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecords(query, con));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        consumersTable.setItems(consumers);
                    }
                }
            });
            searchThread.start();

        });
    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void search(){
        String query = this.query_tf.getText();
        try {
            this.consumers = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecords(query, con));
            this.consumersTable.setItems(this.consumers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Set the message on the label to notify users!
     * @return void
     */
    public void setMessage(String msg){
        this.status_lbl.setText(msg);
    }
    /**
     * Initializes the consumers table
     * @return void
     */
    public void createTable() {

        TableColumn<ConsumerInfo, String> column1 = new TableColumn<>("Account No");
        column1.setMinWidth(88);
        column1.setMaxWidth(88);
        column1.setPrefWidth(88);
        column1.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ConsumerInfo, String> column2 = new TableColumn<>("Consumer Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("consumerName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<ConsumerInfo, String> column3 = new TableColumn<>("Address");
        column3.setMinWidth(336);
        column3.setMaxWidth(336);
        column3.setPrefWidth(336);
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<ConsumerInfo, String> column4 = new TableColumn<>("Account Type");
        column4.setMinWidth(125);
        column4.setMaxWidth(125);
        column4.setPrefWidth(125);
        column4.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<ConsumerInfo, String> column5 = new TableColumn<>("Status");
        column5.setMinWidth(106);
        column5.setMaxWidth(106);
        column5.setPrefWidth(106);
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

    public JFXTextField getSearch_tf(){
        return this.query_tf;
    }
}
