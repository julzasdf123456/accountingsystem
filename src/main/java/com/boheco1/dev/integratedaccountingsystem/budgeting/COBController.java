package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.DeptThresholdDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class COBController extends MenuControllerHandler implements Initializable, ObjectTransaction {
    @FXML
    private JFXButton submit_btn;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private Label category_lbl, threshold_lbl;

    @FXML
    private JFXButton import_btn;

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton remove_btn;

    @FXML
    private JFXTextField cn_tf;

    @FXML
    private JFXTextField activity_tf;

    @FXML
    private JFXTextField board_res_tf;

    @FXML
    private JFXComboBox<?> fs_cb;

    @FXML
    private JFXComboBox<?> type_cb;

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
    @FXML
    private TableView cob_items;
    private ObservableList<COBItem> items = FXCollections.observableArrayList(new ArrayList<>());
    private APP app = null;
    private DeptThreshold threshold = null;
    private double totalAppropriations = 0;
    private String totalBudget = "Current Total Appropriation: ₱ %.2f out of ₱ %.2f Department Threshold!";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.app = AppDAO.getOpen(true);
            this.threshold = DeptThresholdDAO.find(this.app.getAppId(), ActiveUser.getUser().getEmployeeInfo().getDepartmentID());
            this.totalAppropriations = DeptThresholdDAO.getTotalAppropriations(threshold);
            String dept = ActiveUser.getUser().getEmployeeInfo().getDepartment().getDepartmentName();
            this.cn_tf.setText(this.app.getYear()+"-"+dept+"-"+ (CobDAO.countCob(dept)+1));
            this.board_res_tf.setText(this.app.getBoardRes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.app != null) {
            this.threshold_lbl.setText(String.format(this.totalBudget, totalAppropriations, threshold.getThreshAmount()));
        }else{
            this.threshold_lbl.setText("");
        }

        this.createTable();

        this.add_btn.setOnAction(evt -> {
            try {
                showAddItemForm();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.remove_btn.setOnAction(evt -> {
            try {
                int row = this.cob_items.getSelectionModel().getSelectedIndex();
                this.items.remove(row);
                this.cob_items.refresh();
                this.setTable();
                this.setAmount();
            }catch (Exception e){
                //Do nothing
            }
        });
        this.cob_items.setRowFactory(tv -> {
            TableRow<COBItem> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem remove = new MenuItem("Remove Item");
            remove.setOnAction(actionEvent -> {
                this.cob_items.getItems().remove(row.getItem());
                this.cob_items.refresh();
                this.setAmount();
            });

            MenuItem dist = new MenuItem("Distribute");
            dist.setOnAction(actionEvent -> {
                double qtr = (row.getItem().getCost() * row.getItem().getQty()) / 4;
                row.getItem().setQtr1(qtr);
                row.getItem().setQtr2(qtr);
                row.getItem().setQtr3(qtr);
                row.getItem().setQtr4(qtr);
                this.cob_items.refresh();
                this.setAmount();
            });

            MenuItem removeAll = new MenuItem("Clear Items");
            removeAll.setOnAction(actionEvent -> {
                this.items = FXCollections.observableArrayList(new ArrayList<>());
                this.cob_items.setItems(this.items);
                this.cob_items.refresh();
                this.setAmount();
            });

            rowMenu.getItems().addAll(remove, new SeparatorMenuItem(), dist, new SeparatorMenuItem(), removeAll);

            row.contextMenuProperty().bind(
            Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(rowMenu));
            return row;
        });
        //Pass this controller to the Add Item controller
        Utility.setParentController(this);
    }

    /**
     * Initializes the DCR transactions table
     * @return void
     */
    public void createTable(){
        TableColumn<COBItem, String> column = new TableColumn<>("Particulars");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getScopeId() == null ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<COBItem, String> column1 = new TableColumn<>("Est. Cost");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRemarks() != null ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column3 = new TableColumn<>("Qty");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() > 0 ? obj.getValue().getQty()+"" : ""));
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column4 = new TableColumn<>("Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()*obj.getValue().getQty()) : ""));
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column5 = new TableColumn<>("1st Qtr");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr1() != 0 ? Utility.formatDecimal(obj.getValue().getQtr1()) : ""));
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column6 = new TableColumn<>("2nd Qtr");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr2() != 0 ? Utility.formatDecimal(obj.getValue().getQtr2()) : ""));
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column7 = new TableColumn<>("3rd Qtr");
        column7.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr3() != 0 ? Utility.formatDecimal(obj.getValue().getQtr3()) : ""));
        column7.setPrefWidth(100);
        column7.setMaxWidth(100);
        column7.setMinWidth(100);
        column7.setStyle("-fx-alignment: center-right;");

        TableColumn<COBItem, String> column8 = new TableColumn<>("4th Qtr");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr4() != 0 ? Utility.formatDecimal(obj.getValue().getQtr4()) : ""));
        column8.setPrefWidth(100);
        column8.setMaxWidth(100);
        column8.setMinWidth(100);
        column8.setStyle("-fx-alignment: center-right;");

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

    /**
     * Displays Add Item UI
     * @return void
     */
    public void showAddItemForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_item.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddItemController ctrl = fxmlLoader.getController();
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }
    public void setTable() {
        this.cob_items.setItems(this.items);
    }

    public void setAmount(){
        double total = 0, qtr1 = 0, qtr2 = 0, qtr3 = 0, qtr4 = 0;

        for (COBItem i : this.items){
            total += i.getCost() * i.getQty();
            qtr1 += i.getQtr1();
            qtr2 += i.getQtr2();
            qtr3 += i.getQtr3();
            qtr4 += i.getQtr4();
        }
        this.totals_tf.setText(Utility.formatDecimal(total));
        this.q1_tf.setText(Utility.formatDecimal(qtr1));
        this.q2_tf.setText(Utility.formatDecimal(qtr2));
        this.q3_tf.setText(Utility.formatDecimal(qtr3));
        this.q4_tf.setText(Utility.formatDecimal(qtr4));

        this.threshold_lbl.setText(String.format(this.totalBudget, total+totalAppropriations, threshold.getThreshAmount()));
    }

    @Override
    public void receive(Object o) {
        if (o instanceof COBItem) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    COBItem item = (COBItem) o;
                    items.add(item);
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                //progressBar.setVisible(true);
            });

            task.setOnSucceeded(wse -> {
                setAmount();
                setTable();
            });

            task.setOnFailed(wse -> {
                //progressBar.setVisible(false);
            });

            new Thread(task).start();
        }
    }
}
