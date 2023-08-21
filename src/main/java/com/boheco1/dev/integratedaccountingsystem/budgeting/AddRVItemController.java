package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import com.boheco1.dev.integratedaccountingsystem.objects.RVItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddRVItemController extends MenuControllerHandler implements Initializable {

    @FXML
    private ComboBox<String> year_pker;

    @FXML
    private ComboBox<String> cn_datepkr;

    @FXML
    private JFXButton view_btn;

    @FXML
    private Label status_lbl;

    @FXML
    private TableView<COBItem> cob_items;

    @FXML
    private JFXTextField activity_tf, type_tf, fs_tf;

    @FXML
    private JFXTextField desc_tf;

    @FXML
    private JFXTextField cost_tf;

    @FXML
    private JFXTextField unit_tf;

    @FXML
    private JFXTextField qty_tf;

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private ProgressBar progressbar;
    private ObjectTransaction parentController = null;
    private List<RVItem> current;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputHelper.restrictNumbersOnly(this.qty_tf);
        this.add_btn.setDisable(true);
        this.view_btn.setDisable(true);
        this.setYears();
        this.createTable();
        this.year_pker.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            this.setControlNumbers();
        });
        this.setControlNumbers();
        this.cn_datepkr.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (this.cn_datepkr.getSelectionModel().isEmpty()){
                this.view_btn.setDisable(true);
            }else{
                this.view_btn.setDisable(false);
            }
        });

        this.qty_tf.setOnKeyReleased(evt -> {
            int qty = 0;
            try {
                qty = Integer.parseInt(this.qty_tf.getText());
            }catch (Exception e){

            }
        });

        this.view_btn.setOnAction(evt -> {
            List<COBItem> cob_items = new ArrayList<>();

            try {
                COB current = CobDAO.get(this.cn_datepkr.getSelectionModel().getSelectedItem());
                this.activity_tf.setText(current.getActivity());
                this.fs_tf.setText(current.getFundSource().getSource());
                this.type_tf.setText(current.getType());
                cob_items = CobItemDAO.getItems(current);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.cob_items.setItems(FXCollections.observableArrayList(cob_items));
        });

        this.cob_items.setRowFactory(tv -> {
            TableRow<COBItem> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem add = new MenuItem("Add Item");
            add.setOnAction(actionEvent -> {
                this.status_lbl.setText("");
                if (row.getItem().getRemaining() > 0) {
                    this.desc_tf.setText(row.getItem().getDescription());
                    this.cost_tf.setText(Utility.formatDecimal(row.getItem().getCost()));
                    this.unit_tf.setText(row.getItem().getRemarks());
                    this.qty_tf.setText(row.getItem().getRemaining() + "");
                    this.qty_tf.requestFocus();
                    this.add_btn.setDisable(false);
                }else{
                    this.status_lbl.setText("The current item is currently fully requisitioned!");
                }
            });

            rowMenu.getItems().addAll(add);


            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(rowMenu));
            return row;
        });
        this.add_btn.setOnAction(evt -> {
            addItem();
        });
        this.reset_btn.setOnAction(evt ->{
            resetItem();
        });

        this.parentController = Utility.getParentController();
    }
    public void resetItem(){
        this.desc_tf.setText("");
        this.cost_tf.setText("");
        this.unit_tf.setText("");
        this.qty_tf.setText("");
        this.add_btn.setDisable(true);
    }
    /**
     * Creates the year drop down list
     */
    public void setYears(){
        ArrayList data = new ArrayList();
        String current = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int currYear = Integer.parseInt(current);
        int year = 2023;
        for (int i = year; i <= year + 10; i++){
            data.add(i+"");
        }
        this.year_pker.setItems(FXCollections.observableArrayList(data));
        this.year_pker.getSelectionModel().select((currYear+1)+"");
    }
    public void setControlNumbers(){
        this.view_btn.setDisable(true);
        String year = this.year_pker.getSelectionModel().getSelectedItem();
        ArrayList data = new ArrayList<>();
        try {
            List<COB> cobs = CobDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), COB.APPROVED);
            for(COB c : cobs){
                data.add(c.getCobId());
            }
            this.cn_datepkr.setItems(FXCollections.observableArrayList(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the COB Items (Supplies and Materials, and other budget which follows Description, Cost, Unit, Quantity, Amount) table
     * @return void
     */
    public void createTable(){
        TableColumn<COBItem, String> column0 = new TableColumn<>("COB No.");
        column0.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCobId()));
        column0.setStyle("-fx-alignment: center-left;");
        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);

        TableColumn<COBItem, String> column = new TableColumn<>("Description");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getLevel() == 1 ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<COBItem, String> column1 = new TableColumn<>("Est. Cost");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(75);
        column2.setMaxWidth(75);
        column2.setMinWidth(75);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column3 = new TableColumn<>("Qty");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() > 0 ? obj.getValue().getRemaining()+"" : ""));
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");


        this.cob_items.setFixedCellSize(35);
        this.cob_items.setPlaceholder(new Label("No Items added"));

        this.cob_items.getColumns().add(column0);
        this.cob_items.getColumns().add(column);
        this.cob_items.getColumns().add(column1);
        this.cob_items.getColumns().add(column2);
        this.cob_items.getColumns().add(column3);
    }
    public void addItem(){
        this.status_lbl.setText("");
        String qty_str = this.qty_tf.getText();
        COBItem item = this.cob_items.getSelectionModel().getSelectedItem();
        int qty = 0, remaining = item.getRemaining();
        try{
            qty = Integer.parseInt(qty_str);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Dont allow requested quantity to exceed remaining item quantity
        if (qty == 0 ) {
            this.status_lbl.setText("The minimum quantity should be 1!");
            this.qty_tf.requestFocus();
        }else if (qty > remaining) {
            this.status_lbl.setText("The maximum quantity should be "+remaining+"!");
            this.qty_tf.requestFocus();
        }else{
            RVItem rvItem = new RVItem();
            rvItem.setcItemId(item.getcItemId());
            rvItem.setDescription(item.getDescription());
            rvItem.setRemarks(item.getRemarks());
            rvItem.setCost(item.getCost());
            rvItem.setQty(qty);
            rvItem.setCobId(item.getCobId());
            this.parentController.receive(rvItem);
            if (qty == remaining)
                this.cob_items.getItems().remove(this.cob_items.getSelectionModel().getSelectedIndex());
            else {
                COBItem selected = this.cob_items.getSelectionModel().getSelectedItem();
                selected.setRvQty(qty);
                this.cob_items.refresh();
            }
            this.cob_items.getSelectionModel().clearSelection();
            this.resetItem();
        }
    }

    public void setCurrentList(List<RVItem> items){
        this.current = items;
    }
}
