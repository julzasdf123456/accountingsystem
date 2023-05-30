package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class COBApprovalController extends MenuControllerHandler implements Initializable {

    @FXML
    private TableView cob_items;

    @FXML
    private JFXTextField cn_tf;

    @FXML
    private JFXTextField activity_tf;

    @FXML
    private JFXTextField board_res_tf;

    @FXML
    private JFXTextField fs_tf;

    @FXML
    private JFXTextField type_tf;

    @FXML
    private JFXTextField status_tf;

    @FXML
    private TextField totals_tf;

    @FXML
    private TextField q1_tf;

    @FXML
    private TextField q2_tf;

    @FXML
    private TextField q3_tf;

    @FXML
    private TextField q4_tf;

    @FXML
    private JFXTextField prepared_tf;

    @FXML
    private JFXTextField reviewed_tf;

    @FXML
    private JFXTextField approved_tf;

    private ObservableList<COBItem> items = FXCollections.observableArrayList(new ArrayList<>());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        init();
        setAmount();
    }

    /**
     * Initializes the DCR transactions table
     * @return void
     */
    public void createTable(){
        TableColumn<COBItem, String> column = new TableColumn<>("Particulars");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getScopeId() == null ? obj.getValue().getParticulars() : "  "+obj.getValue().getParticulars()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<COBItem, String> column1 = new TableColumn<>("Est. Cost");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getPrice() != 0 ? obj.getValue().getPrice()+"" : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<COBItem, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getUnit() != null ? obj.getValue().getUnit()+"" : ""));
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column3 = new TableColumn<>("Qty");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() != 0 ? obj.getValue().getQty()+"" : ""));
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column4 = new TableColumn<>("Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getCost()+"" : ""));
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column5 = new TableColumn<>("1st Qtr");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr1() != 0 ? obj.getValue().getQtr1()+"" : ""));
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column6 = new TableColumn<>("2nd Qtr");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr2() != 0 ? obj.getValue().getQtr2()+"" : ""));
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column7 = new TableColumn<>("3rd Qtr");
        column7.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr3() != 0 ? obj.getValue().getQtr3()+"" : ""));
        column7.setPrefWidth(100);
        column7.setMaxWidth(100);
        column7.setMinWidth(100);
        column7.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column8 = new TableColumn<>("4th Qtr");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr4() != 0 ? obj.getValue().getQtr4()+"" : ""));
        column8.setPrefWidth(100);
        column8.setMaxWidth(100);
        column8.setMinWidth(100);
        column8.setStyle("-fx-alignment: center;");

        this.cob_items.setFixedCellSize(35);
        this.cob_items.setPlaceholder(new Label("No Items added"));

        this.cob_items.getColumns().add(column);
        this.cob_items.getColumns().add(column1);
        this.cob_items.getColumns().add(column2);
        this.cob_items.getColumns().add(column3);
        this.cob_items.getColumns().add(column4);
        this.cob_items.getColumns().add(column5);
        this.cob_items.getColumns().add(column6);
        this.cob_items.getColumns().add(column7);
        this.cob_items.getColumns().add(column8);
    }

    public void init(){
        COBItem item1 = new COBItem();
        item1.setParticulars("Office Supplies");

        COBItem item2C = new COBItem();
        item2C.setParticulars("Bond Paper");
        item2C.setPrice(500);
        item2C.setUnit("Rim");
        item2C.setQty(8);
        item2C.setCost(item2C.getPrice()*item2C.getQty());
        item2C.setQtr1(item2C.getPrice()*2);
        item2C.setQtr2(item2C.getPrice()*2);
        item2C.setQtr3(item2C.getPrice()*2);
        item2C.setQtr4(item2C.getPrice()*2);
        item2C.setScopeId("1");

        COBItem item3C = new COBItem();
        item3C.setParticulars("Epson Ink");
        item3C.setPrice(1500);
        item3C.setUnit("Pieces");
        item3C.setQty(2);
        item3C.setCost(item3C.getPrice()*item3C.getQty());
        item3C.setQtr1(item3C.getPrice()*1);
        item3C.setQtr3(item3C.getPrice()*1);
        item3C.setScopeId("1");

        this.items.add(item1);
        this.items.add(item2C);
        this.items.add(item3C);
        this.cob_items.setItems(this.items);
    }

    public void setAmount(){
        double total = 0, qtr1 = 0, qtr2 = 0, qtr3 = 0, qtr4 = 0;
        for (COBItem i : this.items){
            total += i.getCost();
            qtr1 += i.getQtr1();
            qtr2 += i.getQtr2();
            qtr3 += i.getQtr3();
            qtr4 += i.getQtr4();
        }
        this.totals_tf.setText(total+"");
        this.q1_tf.setText(qtr1+"");
        this.q2_tf.setText(qtr2+"");
        this.q3_tf.setText(qtr3+"");
        this.q4_tf.setText(qtr4+"");
    }
}
