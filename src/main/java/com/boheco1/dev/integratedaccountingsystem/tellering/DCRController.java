package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class DCRController extends MenuControllerHandler implements Initializable {


    @FXML
    private JFXComboBox<String> month_cb;

    @FXML
    private JFXTextField day_tf;

    @FXML
    private JFXTextField year_tf;

    @FXML
    private JFXButton view_btn;

    @FXML
    private JFXButton print_dcr_btn;

    @FXML
    private TableView<?> dcr_breakdown_table;

    @FXML
    private TableView<Bill> payments_table;

    @FXML
    private Label dcr_total;

    private String[] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};

    private ObservableList<Bill> bills = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setParams();

    }

    public void setParams(){
        this.month_cb.getItems().addAll(this.month);
        this.year_tf.setText(""+ Calendar.getInstance().get(Calendar.YEAR));
    }

    /**
     * Initializes the bills table
     * @return void
     */
    public void createTable(){
        TableColumn<Bill, String> column0 = new TableColumn<>("Bill #");

        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);
        column0.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column = new TableColumn<>("Account #");
        column.setPrefWidth(110);
        column.setMaxWidth(110);
        column.setMinWidth(110);
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getAccountID()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column1 = new TableColumn<>("Consumer Name");
        column1.setPrefWidth(130);
        column1.setMaxWidth(130);
        column1.setMinWidth(130);
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getConsumerName()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Bill Balance");
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Type");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerType"));
        column3.setStyle("-fx-alignment: center-right; -fx-text-fill: #6002EE;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Sys. Loss");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getSlAdjustment()+""));
        column4.setStyle("-fx-alignment: center; -fx-text-fill: #009688;");

        TableColumn<Bill, String> column5 = new TableColumn<>("PPD");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDiscount()+""));
        column5.setStyle("-fx-alignment: center-right; -fx-text-fill: #ff0000;");

        TableColumn<Bill, String> column6 = new TableColumn<>("Surcharge");
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getSurCharge()+""));
        column6.setStyle("-fx-alignment: center-right; -fx-text-fill: #ff0000;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Surcharge");
        column7.setPrefWidth(100);
        column7.setMaxWidth(100);
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        column7.setStyle("-fx-alignment: center-right; -fx-text-fill: #ff0000;");

        TableColumn<Bill, String> column8 = new TableColumn<>("Total Amount");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column8.setStyle("-fx-alignment: center-right; -fx-text-fill: #6002EE;");

        this.bills =  FXCollections.observableArrayList();
        this.payments_table.setFixedCellSize(35);
        this.payments_table.setPlaceholder(new Label("No Bills added"));

        this.payments_table.getColumns().add(column0);
        this.payments_table.getColumns().add(column);
        this.payments_table.getColumns().add(column1);
        this.payments_table.getColumns().add(column2);
        this.payments_table.getColumns().add(column3);
        this.payments_table.getColumns().add(column4);
        this.payments_table.getColumns().add(column5);
        this.payments_table.getColumns().add(column7);
    }

}
