package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputValidation;
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
    private TableView<?> payments_table;

    @FXML
    private TableView<Bill> dcr_power_table;

    @FXML
    private Label dcr_total;

    private String[] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};

    private ObservableList<Bill> bills = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setParams();
        this.createTable();
        InputValidation.restrictNumbersOnly(this.year_tf);
        InputValidation.restrictNumbersOnly(this.day_tf);
        this.view_btn.setOnAction(action ->{
            int day=0, year=0, month=0;
            try{
                day = Integer.parseInt(this.day_tf.getText());
                year = Integer.parseInt(this.year_tf.getText());
                month = this.month_cb.getSelectionModel().getSelectedIndex()+1;
                this.bills = FXCollections.observableArrayList(BillDAO.getAllPaidBills(year, month, day));
                this.dcr_power_table.setItems(this.bills);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void setParams(){
        this.month_cb.getItems().addAll(this.month);
        this.month_cb.getSelectionModel().select(Calendar.getInstance().get(Calendar.MONTH));
        this.year_tf.setText(""+ Calendar.getInstance().get(Calendar.YEAR));
        this.day_tf.setText(""+Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
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
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getConsumerName()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Bill Balance");
        column2.setPrefWidth(120);
        column2.setMaxWidth(120);
        column2.setMinWidth(120);
        column2.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmountDue())));
        column2.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Type");
        column3.setPrefWidth(75);
        column3.setMaxWidth(75);
        column3.setMinWidth(75);
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerType"));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Sys. Loss");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSlAdjustment())));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("PPD");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty((Utility.formatDecimal(obj.getValue().getDiscount()))));
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column6 = new TableColumn<>("Surcharge");
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setCellValueFactory(obj -> new SimpleStringProperty((Utility.formatDecimal(obj.getValue().getSurCharge()))));
        column6.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Net-Amt.");
        column7.setPrefWidth(125);
        column7.setMaxWidth(125);
        column7.setMinWidth(125);
        column7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column7.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column8 = new TableColumn<>("Time");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(
                ((PaidBill) obj.getValue()).getPostingTime()+""
        ));
        column8.setPrefWidth(75);
        column8.setMaxWidth(75);
        column8.setMinWidth(75);
        column8.setStyle("-fx-alignment: center;");

        this.bills =  FXCollections.observableArrayList();
        this.dcr_power_table.setFixedCellSize(35);
        this.dcr_power_table.setPlaceholder(new Label("No Bills added"));

        this.dcr_power_table.getColumns().add(column0);
        this.dcr_power_table.getColumns().add(column);
        this.dcr_power_table.getColumns().add(column1);
        this.dcr_power_table.getColumns().add(column2);
        this.dcr_power_table.getColumns().add(column3);
        this.dcr_power_table.getColumns().add(column4);
        this.dcr_power_table.getColumns().add(column5);
        this.dcr_power_table.getColumns().add(column6);
        this.dcr_power_table.getColumns().add(column7);
        this.dcr_power_table.getColumns().add(column8);
    }

}
