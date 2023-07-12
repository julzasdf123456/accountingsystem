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
import javafx.scene.paint.Paint;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
    private JFXComboBox<FundSource> fs_cb;

    @FXML
    private JFXComboBox<String> type_cb;

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
    private TableView cob_items;
    private ObservableList<COBItem> items = FXCollections.observableArrayList(new ArrayList<>());
    private APP app = null;
    private DeptThreshold threshold = null;
    private double totalAppropriations = 0, cobAmount = 0, appropFromDB = 0;
    private String totalBudget = "Current Total Appropriation: ₱ %.2f out of ₱ %.2f Department Threshold!";
    private String dept;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> types = FXCollections.observableArrayList(COBItem.TYPES);
        this.type_cb.setItems(types);
        this.type_cb.getSelectionModel().select(4);
        this.type_cb.getSelectionModel().selectedItemProperty().addListener((observableValue, o, n) -> {
            this.cob_items.getColumns().clear();
            if (n.equals(COBItem.TYPES[0])) {
                this.createTable(true);
            }else if (n.equals(COBItem.TYPES[1])) {
                this.createRepresentationTable();
            }else if (n.equals(COBItem.TYPES[2])) {
                this.createSalariesTable();
            }else if (n.equals(COBItem.TYPES[3])) {
                this.createTravelsTable();
            }else{
                this.createTable(false);
            }
            this.cob_items.refresh();
        });
        try {
            this.fs_cb.setItems(FXCollections.observableArrayList(AppDAO.getFundSources()));
            this.app = AppDAO.getOpen(true);
            this.threshold = DeptThresholdDAO.find(this.app.getAppId(), ActiveUser.getUser().getEmployeeInfo().getDepartmentID());
            this.totalAppropriations = DeptThresholdDAO.getTotalAppropriations(threshold);
            this.appropFromDB = this.totalAppropriations;
            EmployeeInfo emp = ActiveUser.getUser().getEmployeeInfo();
            this.dept = emp.getDepartment().getDepartmentName();
            this.cn_tf.setText(this.app.getYear()+"-"+dept+"-"+ (CobDAO.countCob(dept)+1));
            this.board_res_tf.setText(this.app.getBoardRes());
            this.prepared_tf.setText(emp.getEmployeeFirstName()+" "+emp.getEmployeeLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.app != null) {
            this.threshold_lbl.setText(String.format(this.totalBudget, totalAppropriations, threshold.getThreshAmount()));
        }else{
            this.threshold_lbl.setText("");
        }

        this.createTable(false);

        this.add_btn.setOnAction(evt -> {
            try {
                String n = this.type_cb.getSelectionModel().getSelectedItem();
                if (n.equals(COBItem.TYPES[0])) {
                    this.showAddItemForm();
                }else if (n.equals(COBItem.TYPES[1])) {
                    this.showAddReprForm();
                }else if (n.equals(COBItem.TYPES[2])) {
                    this.showAddSalaryForm();
                }else if (n.equals(COBItem.TYPES[3])) {
                    this.showAddTravelForm();
                }else{
                    this.showAddItemForm();
                }
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

            MenuItem child = new MenuItem("=>");
            child.setOnAction(actionEvent -> {
                for (Object item:  this.cob_items.getItems()) {
                    COBItem curr = (COBItem) item;
                    if (curr.getCost() == 0
                            && !curr.getcItemId().equals(row.getItem().getcItemId())) {
                        row.getItem().setScopeId(curr.getcItemId());
                        break;
                    }
                }
                this.cob_items.refresh();
            });

            MenuItem parent = new MenuItem("<=");
            parent.setOnAction(actionEvent -> {
                row.getItem().setScopeId(null);
                this.cob_items.refresh();
            });

            MenuItem dist = new MenuItem("Distribute Per Qtr");
            dist.setOnAction(actionEvent -> {
                double qtr = (row.getItem().getCost() * row.getItem().getQty()) / 4;
                row.getItem().setQtr1(qtr);
                row.getItem().setQtr2(qtr);
                row.getItem().setQtr3(qtr);
                row.getItem().setQtr4(qtr);
                this.cob_items.refresh();
                this.setAmount();
            });

            MenuItem q1 = new MenuItem("Set to Qtr 1");
            q1.setOnAction(actionEvent -> {
                row.getItem().setQtr1(row.getItem().getCost() * row.getItem().getQty());
                row.getItem().setQtr2(0);
                row.getItem().setQtr3(0);
                row.getItem().setQtr4(0);
                this.cob_items.refresh();
                this.setAmount();
            });

            MenuItem q2 = new MenuItem("Set to Qtr 2");
            q2.setOnAction(actionEvent -> {
                row.getItem().setQtr2(row.getItem().getCost() * row.getItem().getQty());
                row.getItem().setQtr1(0);
                row.getItem().setQtr3(0);
                row.getItem().setQtr4(0);
                this.cob_items.refresh();
                this.setAmount();
            });

            MenuItem q3 = new MenuItem("Set to Qtr 3");
            q3.setOnAction(actionEvent -> {
                row.getItem().setQtr3(row.getItem().getCost() * row.getItem().getQty());
                row.getItem().setQtr2(0);
                row.getItem().setQtr1(0);
                row.getItem().setQtr4(0);
                this.cob_items.refresh();
                this.setAmount();
            });

            MenuItem q4 = new MenuItem("Set to Qtr 4");
            q4.setOnAction(actionEvent -> {
                row.getItem().setQtr4(row.getItem().getCost() * row.getItem().getQty());
                row.getItem().setQtr2(0);
                row.getItem().setQtr3(0);
                row.getItem().setQtr1(0);
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

            rowMenu.getItems().addAll(remove, new SeparatorMenuItem(), child, parent, new SeparatorMenuItem(), dist, q1, q2, q3, q4, new SeparatorMenuItem(), removeAll);

            row.contextMenuProperty().bind(
            Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(rowMenu));
            return row;
        });

        this.submit_btn.setOnAction(evt ->{
            this.submitCOB();
        });

        this.reset_btn.setOnAction(evt ->{
            reset();
        });

        //Pass this controller to the Add Item controller
        Utility.setParentController(this);
    }

    /**
     * Initializes the COB Items (Supplies and Materials, and other budget which follows Description, Cost, Unit, Quantity, Amount) table
     * @return void
     */
    public void createTable(boolean isBenefits){
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

        TableColumn<COBItem, String> column31 = new TableColumn<>("Times");
        column31.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfTimes() > 0 ? obj.getValue().getNoOfTimes()+"" : ""));
        column31.setPrefWidth(50);
        column31.setMaxWidth(50);
        column31.setMinWidth(50);
        column31.setStyle("-fx-alignment: center;");

        TableColumn<COBItem, String> column4 = new TableColumn<>("Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
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
        if (isBenefits) this.cob_items.getColumns().add(column31);
        this.cob_items.getColumns().add(column4);
        this.cob_items.getColumns().add(column5);
        this.cob_items.getColumns().add(column6);
        this.cob_items.getColumns().add(column7);
        this.cob_items.getColumns().add(column8);
    }

    /**
     * Initializes the COB Items (Representation) table
     * @return void
     */
    public void createRepresentationTable(){
        TableColumn<Representation, String> column = new TableColumn<>("Particulars");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getScopeId() == null ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Representation, String> column1 = new TableColumn<>("Est. Cost");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<Representation, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRemarks() != null ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Representation, String> column3 = new TableColumn<>("Qty");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() > 0 ? obj.getValue().getQty()+"" : ""));
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");

        TableColumn<Representation, String> column4 = new TableColumn<>("Basic Allowance");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Representation, String> column41 = new TableColumn<>("Reimb. Allowance");
        column41.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getReimbursableAllowance()) : ""));
        column41.setPrefWidth(150);
        column41.setMaxWidth(150);
        column41.setMinWidth(150);
        column41.setStyle("-fx-alignment: center-right;");

        TableColumn<Representation, String> column42 = new TableColumn<>("Other Allowance");
        column42.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getOtherAllowance()) : ""));
        column42.setPrefWidth(150);
        column42.setMaxWidth(150);
        column42.setMinWidth(150);
        column42.setStyle("-fx-alignment: center-right;");

        this.cob_items.setFixedCellSize(35);
        this.cob_items.setPlaceholder(new Label("No Items added"));

        this.cob_items.getColumns().add(column);
        this.cob_items.getColumns().add(column1);
        this.cob_items.getColumns().add(column2);
        this.cob_items.getColumns().add(column3);
        this.cob_items.getColumns().add(column4);
        this.cob_items.getColumns().add(column41);
        this.cob_items.getColumns().add(column42);
    }

    /**
     * Initializes the COB Items (Travels) table
     * @return void
     */
    public void createTravelsTable(){
        TableColumn<Travel, String> column = new TableColumn<>("Particulars");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getScopeId() == null ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Travel, String> column1 = new TableColumn<>("Travel Cost");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRemarks() != null ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(75);
        column2.setMaxWidth(75);
        column2.setMinWidth(75);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Travel, String> column3 = new TableColumn<>("Pax");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() > 0 ? obj.getValue().getQty()+"" : ""));
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");

        TableColumn<Travel, String> column4 = new TableColumn<>("Days");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getNoOfDays()+"" : ""));
        column4.setPrefWidth(50);
        column4.setMaxWidth(50);
        column4.setMinWidth(50);
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column41 = new TableColumn<>("Times");
        column41.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getNoOfTimes()+"" : ""));
        column41.setPrefWidth(50);
        column41.setMaxWidth(50);
        column41.setMinWidth(50);
        column41.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column42 = new TableColumn<>("Transp.");
        column42.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalTransport()) : ""));
        column42.setPrefWidth(75);
        column42.setMaxWidth(75);
        column42.setMinWidth(75);
        column42.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column43 = new TableColumn<>("Lodging");
        column43.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalLodging()) : ""));
        column43.setPrefWidth(75);
        column43.setMaxWidth(75);
        column43.setMinWidth(75);
        column43.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column44 = new TableColumn<>("Reg. Fee");
        column44.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getRegistration()) : ""));
        column44.setPrefWidth(75);
        column44.setMaxWidth(75);
        column44.setMinWidth(75);
        column44.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column45 = new TableColumn<>("Incidental");
        column45.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalIncidental()) : ""));
        column45.setPrefWidth(75);
        column45.setMaxWidth(75);
        column45.setMinWidth(75);
        column45.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column46 = new TableColumn<>("Mode");
        column46.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getMode() : ""));
        column46.setPrefWidth(50);
        column46.setMaxWidth(50);
        column46.setMinWidth(50);
        column46.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column5 = new TableColumn<>("1st Qtr");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr1() != 0 ? Utility.formatDecimal(obj.getValue().getQtr1()) : ""));
        column5.setPrefWidth(75);
        column5.setMaxWidth(75);
        column5.setMinWidth(75);
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column6 = new TableColumn<>("2nd Qtr");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr2() != 0 ? Utility.formatDecimal(obj.getValue().getQtr2()) : ""));
        column6.setPrefWidth(75);
        column6.setMaxWidth(75);
        column6.setMinWidth(75);
        column6.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column7 = new TableColumn<>("3rd Qtr");
        column7.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr3() != 0 ? Utility.formatDecimal(obj.getValue().getQtr3()) : ""));
        column7.setPrefWidth(75);
        column7.setMaxWidth(75);
        column7.setMinWidth(75);
        column7.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column8 = new TableColumn<>("4th Qtr");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQtr4() != 0 ? Utility.formatDecimal(obj.getValue().getQtr4()) : ""));
        column8.setPrefWidth(75);
        column8.setMaxWidth(75);
        column8.setMinWidth(75);
        column8.setStyle("-fx-alignment: center-right;");

        this.cob_items.setFixedCellSize(35);
        this.cob_items.setPlaceholder(new Label("No Items added"));

        this.cob_items.getColumns().add(column);
        this.cob_items.getColumns().add(column1);
        this.cob_items.getColumns().add(column2);
        this.cob_items.getColumns().add(column3);
        this.cob_items.getColumns().add(column4);
        this.cob_items.getColumns().add(column41);
        this.cob_items.getColumns().add(column42);
        this.cob_items.getColumns().add(column43);
        this.cob_items.getColumns().add(column44);
        this.cob_items.getColumns().add(column45);
        this.cob_items.getColumns().add(column46);
        this.cob_items.getColumns().add(column5);
        this.cob_items.getColumns().add(column6);
        this.cob_items.getColumns().add(column7);
        this.cob_items.getColumns().add(column8);
    }

    /**
     * Initializes the COB Items (Salaries) table
     * @return void
     */
    public void createSalariesTable(){
        TableColumn<Salary, String> column = new TableColumn<>("Particulars");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getScopeId() == null ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Salary, String> column1 = new TableColumn<>("Basic Salary");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRemarks() != null ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(75);
        column2.setMaxWidth(75);
        column2.setMinWidth(75);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Salary, String> column3 = new TableColumn<>("Qty");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() > 0 ? obj.getValue().getQty()+"" : ""));
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");

        TableColumn<Salary, String> column42 = new TableColumn<>("Long.");
        column42.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getLongetivity()) : ""));
        column42.setPrefWidth(85);
        column42.setMaxWidth(85);
        column42.setMinWidth(85);
        column42.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column43 = new TableColumn<>("SSS/PhilH");
        column43.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getsSSPhilH()) : ""));
        column43.setPrefWidth(85);
        column43.setMaxWidth(85);
        column43.setMinWidth(85);
        column43.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column44 = new TableColumn<>("Overtime");
        column44.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getOvertime()) : ""));
        column44.setPrefWidth(85);
        column44.setMaxWidth(85);
        column44.setMinWidth(85);
        column44.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column45 = new TableColumn<>("Cash Gift");
        column45.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCashGift()) : ""));
        column45.setPrefWidth(75);
        column45.setMaxWidth(75);
        column45.setMinWidth(75);
        column45.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column46 = new TableColumn<>("13th Mo.");
        column46.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getBonus13()) : ""));
        column46.setPrefWidth(85);
        column46.setMaxWidth(85);
        column46.setMinWidth(85);
        column46.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column47 = new TableColumn<>("Annual Total");
        column47.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAnnualTotal()) : ""));
        column47.setPrefWidth(85);
        column47.setMaxWidth(85);
        column47.setMinWidth(85);
        column47.setStyle("-fx-alignment: center-right;");

        this.cob_items.setFixedCellSize(35);
        this.cob_items.setPlaceholder(new Label("No Items added"));

        this.cob_items.getColumns().add(column);
        this.cob_items.getColumns().add(column1);
        this.cob_items.getColumns().add(column2);
        this.cob_items.getColumns().add(column3);
        this.cob_items.getColumns().add(column42);
        this.cob_items.getColumns().add(column43);
        this.cob_items.getColumns().add(column44);
        this.cob_items.getColumns().add(column45);
        this.cob_items.getColumns().add(column46);
        this.cob_items.getColumns().add(column47);
    }

    /**
     * Insert COB
     * @return void
     */
    public void submitCOB(){
        final String cn = this.cn_tf.getText();
        final String activity = this.activity_tf.getText();
        double amount = 0;
        try {
            amount = Double.parseDouble(this.totals_tf.getText().replace(",", ""));
        }catch (Exception e){

        }
        if (cn.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the control number! Format: YEAR-DEPT-NO",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (activity.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the major activity title for this budget!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.fs_cb.getSelectionModel().getSelectedItem() == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please select the fund source!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.items.size() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add the projected expenses for this COB!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (amount == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add the projected expenses for this COB!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else {
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Submit COB", "This process is final. Confirm submission?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            final double amt = amount;
            accept.setOnAction(__ -> {
                dialog.close();
                try {
                    final String fsid = this.fs_cb.getSelectionModel().getSelectedItem().getFsId();
                    COB cob = new COB();
                    cob.setAppId(this.app.getAppId());
                    cob.setCobId(cn);
                    cob.setFsId(fsid);
                    cob.setActivity(activity);
                    cob.setAmount(amt);
                    cob.setItems(this.items);
                    CobDAO.createCOB(cob);
                    reset();
                    this.cn_tf.setText(this.app.getYear()+"-"+dept+"-"+ (CobDAO.countCob(dept)+1));
                    AlertDialogBuilder.messgeDialog("Submit COB", "The COB was successfully submitted and awaiting review by your department head!",
                            Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                } catch (SQLException e) {
                    AlertDialogBuilder.messgeDialog("System Error", "An SQL error occurred: " + e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred: " + e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        }
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
        ctrl.setType(this.type_cb.getSelectionModel().getSelectedItem());
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Add Item (Representation) UI
     * @return void
     */
    public void showAddReprForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_repr.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddReprController ctrl = fxmlLoader.getController();
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Add Item (Travel) UI
     * @return void
     */
    public void showAddTravelForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_travel.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddTravelController ctrl = fxmlLoader.getController();
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Add Item (Salary) UI
     * @return void
     */
    public void showAddSalaryForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_salaries.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddSalaryController ctrl = fxmlLoader.getController();
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
        this.cobAmount = total;
        this.totals_tf.setText(Utility.formatDecimal(total));
        this.q1_tf.setText(Utility.formatDecimal(qtr1));
        this.q2_tf.setText(Utility.formatDecimal(qtr2));
        this.q3_tf.setText(Utility.formatDecimal(qtr3));
        this.q4_tf.setText(Utility.formatDecimal(qtr4));

        this.threshold_lbl.setText(String.format(this.totalBudget, total+totalAppropriations, threshold.getThreshAmount()));
    }

    public void reset(){
        this.items = FXCollections.observableArrayList(new ArrayList<>());
        this.cob_items.setItems(this.items);
        this.cobAmount = 0;
        this.totals_tf.setText("");
        this.q1_tf.setText("");
        this.q2_tf.setText("");
        this.q3_tf.setText("");
        this.q4_tf.setText("");
        this.activity_tf.setText("");
        this.fs_cb.getSelectionModel().clearSelection();
        this.threshold_lbl.setText(String.format(this.totalBudget, this.appropFromDB, threshold.getThreshAmount()));
    }

    @Override
    public void receive(Object o) {
        if (o instanceof COBItem) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    COBItem item = (COBItem) o;
                    item.setSequence(items.size()+1);
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
