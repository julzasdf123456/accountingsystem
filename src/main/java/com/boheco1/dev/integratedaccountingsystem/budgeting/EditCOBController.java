package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditCOBController extends MenuControllerHandler implements Initializable, ObjectTransaction {
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

    @FXML
    private Label remarks_lbl;

    @FXML
    private ProgressBar progressBar;

    private ObservableList<COBItem> items = FXCollections.observableArrayList(new ArrayList<>());
    private APP app = null;
    private DeptThreshold threshold = null;
    private double totalAppropriations = 0, cobAmount = 0, appropFromDB = 0;
    private String totalBudget = "Current Total Appropriation: ₱ %.2f out of ₱ %.2f Department Threshold!";
    private String dept;
    private COB current = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.current = (COB) Utility.getSelectedObject();

        ObservableList<String> types = FXCollections.observableArrayList(COBItem.TYPES);
        this.type_cb.setItems(types);
        this.type_cb.getSelectionModel().select(4);
        this.type_cb.getSelectionModel().selectedItemProperty().addListener((observableValue, o, n) -> {
            this.cob_items.getColumns().clear();
            this.cob_items.getItems().clear();
            if (n.equals(COBItem.TYPES[0])) {
                this.createTable();
            }else if (n.equals(COBItem.TYPES[1])) {
                this.createRepresentationTable();
            }else if (n.equals(COBItem.TYPES[2])) {
                this.createSalariesTable();
            }else if (n.equals(COBItem.TYPES[3])) {
                this.createTravelsTable();
            }else{
                this.createTable();
            }
            this.cob_items.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            this.cob_items.refresh();
        });

        //Combobox is disabled, type cannot be changed
        this.type_cb.setDisable(true);
        this.cn_tf.setEditable(false);

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

        this.createTable();

        this.add_btn.setOnAction(evt -> {
            try {
                String n = this.type_cb.getSelectionModel().getSelectedItem();
                if (n.equals(COBItem.TYPES[0])) {
                    this.showAddItemForm(null);
                }else if (n.equals(COBItem.TYPES[1])) {
                    this.showAddReprForm(null);
                }else if (n.equals(COBItem.TYPES[2])) {
                    this.showAddSalaryForm(null);
                }else if (n.equals(COBItem.TYPES[3])) {
                    this.showAddTravelForm(null);
                }else{
                    this.showAddItemForm(null);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.remove_btn.setOnAction(evt -> {
            COBItem selected = (COBItem) this.cob_items.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    this.deleteItem(selected);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "A System error occurred: " + e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            }else{
                AlertDialogBuilder.messgeDialog("Delete COB Item", "Please select the item to delete and try again!",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });

        this.cob_items.setRowFactory(tv -> {
            TableRow<COBItem> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem edit = new MenuItem("Edit Item");
            edit.setOnAction(actionEvent -> {
                try {
                    String n = this.type_cb.getSelectionModel().getSelectedItem();
                    if (n.equals(COBItem.TYPES[0])) {
                        this.showAddItemForm(row.getItem());
                    }else if (n.equals(COBItem.TYPES[1])) {
                        this.showAddReprForm((Representation) row.getItem());
                    }else if (n.equals(COBItem.TYPES[2])) {
                        this.showAddSalaryForm((Salary) row.getItem());
                    }else if (n.equals(COBItem.TYPES[3])) {
                        this.showAddTravelForm((Travel) row.getItem());
                    }else{
                        this.showAddItemForm(row.getItem());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            MenuItem remove = new MenuItem("Remove Item");
            remove.setOnAction(actionEvent -> {
                this.deleteItem(row.getItem());
            });

            MenuItem child = new MenuItem("=>");
            child.setOnAction(actionEvent -> {
                try {
                    CobItemDAO.changeLevel(row.getItem(), 2);
                    row.getItem().setLevel(2);
                    this.cob_items.refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem parent = new MenuItem("<=");
            parent.setOnAction(actionEvent -> {
                try {
                    CobItemDAO.changeLevel(row.getItem(), 1);
                    row.getItem().setLevel(1);
                    this.cob_items.refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem dist = new MenuItem("Distribute Per Qtr");
            dist.setOnAction(actionEvent -> {
                try {
                    double qtr = (row.getItem().getAmount()) / 4;
                    row.getItem().setQtr1(qtr);
                    row.getItem().setQtr2(qtr);
                    row.getItem().setQtr3(qtr);
                    row.getItem().setQtr4(qtr);
                    CobItemDAO.changeQtrAmount(row.getItem());
                    this.cob_items.refresh();
                    this.setAmount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem q1 = new MenuItem("Set to Qtr 1");
            q1.setOnAction(actionEvent -> {
                try {
                    row.getItem().setQtr1(row.getItem().getAmount());
                    row.getItem().setQtr2(0);
                    row.getItem().setQtr3(0);
                    row.getItem().setQtr4(0);
                    CobItemDAO.changeQtrAmount(row.getItem());
                    this.cob_items.refresh();
                    this.setAmount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem q2 = new MenuItem("Set to Qtr 2");
            q2.setOnAction(actionEvent -> {
                try {
                    row.getItem().setQtr2(row.getItem().getAmount());
                    row.getItem().setQtr1(0);
                    row.getItem().setQtr3(0);
                    row.getItem().setQtr4(0);
                    CobItemDAO.changeQtrAmount(row.getItem());
                    this.cob_items.refresh();
                    this.setAmount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem q3 = new MenuItem("Set to Qtr 3");
            q3.setOnAction(actionEvent -> {
                try {
                    row.getItem().setQtr3(row.getItem().getAmount());
                    row.getItem().setQtr2(0);
                    row.getItem().setQtr1(0);
                    row.getItem().setQtr4(0);
                    CobItemDAO.changeQtrAmount(row.getItem());
                    this.cob_items.refresh();
                    this.setAmount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem q4 = new MenuItem("Set to Qtr 4");
            q4.setOnAction(actionEvent -> {
                try {
                    row.getItem().setQtr4(row.getItem().getAmount());
                    row.getItem().setQtr2(0);
                    row.getItem().setQtr3(0);
                    row.getItem().setQtr1(0);
                    CobItemDAO.changeQtrAmount(row.getItem());
                    this.cob_items.refresh();
                    this.setAmount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            MenuItem removeAll = new MenuItem("Clear Items");
            removeAll.setOnAction(actionEvent -> {
                this.items = FXCollections.observableArrayList(new ArrayList<>());
                this.cob_items.setItems(this.items);
                this.cob_items.refresh();
                this.setAmount();
            });

            String n = this.type_cb.getSelectionModel().getSelectedItem();
            if (n.equals(COBItem.TYPES[1]) || n.equals(COBItem.TYPES[2])) {
                rowMenu.getItems().addAll(edit, new SeparatorMenuItem(), remove, new SeparatorMenuItem(), child, parent, new SeparatorMenuItem(), removeAll);
            }else {
                rowMenu.getItems().addAll(edit, new SeparatorMenuItem(), remove, new SeparatorMenuItem(), child, parent, new SeparatorMenuItem(), dist, q1, q2, q3, q4, new SeparatorMenuItem(), removeAll);
            }

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
            Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_user_cob_list.fxml"));
        });

        this.import_btn.setOnAction(evt ->{
            try {
                this.showImportForm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Pass this controller to the Add Item controller
        Utility.setParentController(this);

        this.setDetails();

        this.progressBar.setVisible(false);
    }

    /**
     * Initializes the COB Items (Supplies and Materials, and other budget which follows Description, Cost, Unit, Quantity, Amount) table
     * @return void
     */
    public void createTable(){
        TableColumn<COBItem, String> column = new TableColumn<>("Particulars");
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
        column31.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getNoOfTimes()+"" : ""));
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
        this.cob_items.getColumns().add(column31);
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
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getLevel() == 1 ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Representation, String> column1 = new TableColumn<>("Amount");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<Representation, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getRemarks()+"" : ""));
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
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getRepresentationAllowance()) : ""));
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
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getLevel() == 1 ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Travel, String> column1 = new TableColumn<>("Travel Cost");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalTravel()) : ""));
        column1.setPrefWidth(75);
        column1.setMaxWidth(75);
        column1.setMinWidth(75);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<Travel, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getRemarks()+"" : ""));
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
        column44.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalRegistration()) : ""));
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

        TableColumn<Travel, String> columnT = new TableColumn<>("Total");
        columnT.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        columnT.setPrefWidth(75);
        columnT.setMaxWidth(75);
        columnT.setMinWidth(75);
        columnT.setStyle("-fx-alignment: center-right;");

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
        this.cob_items.getColumns().add(columnT);
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
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getLevel() == 1 ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Salary, String> column1 = new TableColumn<>("Basic Salary");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalSalary()) : ""));
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getRemarks()+"" : ""));
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
        column42.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalLongetivity()) : ""));
        column42.setPrefWidth(85);
        column42.setMaxWidth(85);
        column42.setMinWidth(85);
        column42.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column43 = new TableColumn<>("SSS/PhilH");
        column43.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalSSS()) : ""));
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
        column45.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalCashGift()) : ""));
        column45.setPrefWidth(75);
        column45.setMaxWidth(75);
        column45.setMinWidth(75);
        column45.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column46 = new TableColumn<>("13th Mo.");
        column46.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getTotalBonus()) : ""));
        column46.setPrefWidth(85);
        column46.setMaxWidth(85);
        column46.setMinWidth(85);
        column46.setStyle("-fx-alignment: center-right;");

        TableColumn<Salary, String> column47 = new TableColumn<>("Annual Total");
        column47.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAnnualTotal()) : ""));
        column47.setPrefWidth(110);
        column47.setMaxWidth(110);
        column47.setMinWidth(110);
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
            accept.setOnAction(__ -> {
                dialog.close();
                try {
                    final String fsid = this.fs_cb.getSelectionModel().getSelectedItem().getFsId();
                    current.setFsId(fsid);
                    current.setActivity(activity);
                    CobDAO.submitRevisedCob(current);
                    AlertDialogBuilder.messgeDialog("Submit COB", "The COB was successfully submitted and awaiting review by your department head!",
                            Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                    this.add_btn.setDisable(true);
                    this.submit_btn.setDisable(true);
                    this.remove_btn.setDisable(true);
                    this.import_btn.setDisable(true);
                    this.add_btn.setVisible(false);
                    this.submit_btn.setVisible(false);
                    this.remove_btn.setVisible(false);
                    this.import_btn.setVisible(false);
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
     * Displays Import Item UI
     * @return void
     */
    public void showImportForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_import.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
    }

    /**
     * Displays Add Item UI
     * @return void
     */
    public void showAddItemForm(COBItem item) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_item.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddItemController ctrl = fxmlLoader.getController();
        ctrl.setType(this.type_cb.getSelectionModel().getSelectedItem());
        String n = this.type_cb.getSelectionModel().getSelectedItem();
        if (!n.equals(COBItem.TYPES[4])) {
            ctrl.getCheck().setSelected(false);
            ctrl.disableUnit(true);
            ctrl.getCheck().setVisible(false);
        }else{
            ctrl.getCheck().setSelected(true);
            ctrl.disableUnit(false);
            ctrl.getCheck().setVisible(true);
        }
        //If edit
        if (item != null) {
            ctrl.setItem(item);
            ctrl.showDetails();
            ctrl.getAdd_btn().setOnAction(evt -> {
                ctrl.updateItem(current);
            });
            ctrl.getAdd_btn().setText("Update");
            ctrl.getReset_btn().setDisable(true);
            ctrl.getReset_btn().setVisible(false);
        }else{
            ctrl.getAdd_btn().setText("Add");
            ctrl.getReset_btn().setDisable(false);
            ctrl.getReset_btn().setVisible(true);
        }

        dialog.setOnDialogOpened((event) -> {
            ctrl.getDescription_tf().requestFocus();
        });
        dialog.show();
    }

    /**
     * Displays Add Item (Representation) UI
     * @return void
     */
    public void showAddReprForm(Representation item) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_repr.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddReprController ctrl = fxmlLoader.getController();
        //If edit
        if (item != null) {
            ctrl.setItem(item);
            ctrl.showDetails();
            ctrl.getAdd_btn().setOnAction(evt -> {
                ctrl.updateItem(current);
            });
            ctrl.getAdd_btn().setText("Update");
            ctrl.getReset_btn().setDisable(true);
            ctrl.getReset_btn().setVisible(false);
        }else{
            ctrl.getAdd_btn().setText("Add");
            ctrl.getReset_btn().setDisable(false);
            ctrl.getReset_btn().setVisible(true);
        }
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Add Item (Travel) UI
     * @return void
     */
    public void showAddTravelForm(Travel item) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_travel.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddTravelController ctrl = fxmlLoader.getController();
        //If edit
        if (item != null) {
            ctrl.setItem(item);
            ctrl.showDetails();
            ctrl.getAdd_btn().setOnAction(evt -> {
                ctrl.updateItem(current);
            });
            ctrl.getAdd_btn().setText("Update");
            ctrl.getReset_btn().setDisable(true);
            ctrl.getReset_btn().setVisible(false);
        }else{
            ctrl.getAdd_btn().setText("Add");
            ctrl.getReset_btn().setDisable(false);
            ctrl.getReset_btn().setVisible(true);
        }
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Add Item (Salary) UI
     * @return void
     */
    public void showAddSalaryForm(Salary item) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_salaries.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddSalaryController ctrl = fxmlLoader.getController();
        //If edit
        if (item != null) {
            ctrl.setItem(item);
            ctrl.showDetails();
            ctrl.getAdd_btn().setOnAction(evt -> {
                ctrl.updateItem(current);
            });
            ctrl.getAdd_btn().setText("Update");
            ctrl.getReset_btn().setDisable(true);
            ctrl.getReset_btn().setVisible(false);
        }else{
            ctrl.getAdd_btn().setText("Add");
            ctrl.getReset_btn().setDisable(false);
            ctrl.getReset_btn().setVisible(true);
        }
        dialog.setOnDialogOpened((event) -> { ctrl.getDescription_tf().requestFocus(); });
        dialog.show();
    }

    public void setTable() {
        this.cob_items.setItems(this.items);
    }

    public void setAmount(){
        double total = 0, qtr1 = 0, qtr2 = 0, qtr3 = 0, qtr4 = 0;

        for (COBItem i : this.items){
            total += i.getAmount();
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
                    try {
                        COBItem item = (COBItem) o;
                        item.setSequence(items.size() + 1);
                        CobItemDAO.add(current, item);
                        items.add(item);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                progressBar.setVisible(true);
            });

            task.setOnSucceeded(wse -> {
                setAmount();
                setTable();
                progressBar.setVisible(false);
            });

            task.setOnFailed(wse -> {
                progressBar.setVisible(false);
            });

            new Thread(task).start();

        }else if (o instanceof List){
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        List<COBItem> imported = (List<COBItem>) o;
                        for (COBItem i : imported) {
                            i.setcItemId(Utility.generateRandomId());
                            items.add(i);
                        }
                        CobItemDAO.add(current, imported);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                progressBar.setVisible(true);
            });

            task.setOnSucceeded(wse -> {
                setAmount();
                setTable();
                progressBar.setVisible(false);
            });

            task.setOnFailed(wse -> {
                progressBar.setVisible(false);
            });

            new Thread(task).start();
        }
    }

    public void setDetails(){
        try {
            this.current.setItems(CobItemDAO.getItems(this.current));
            this.remarks_lbl.setText(this.current.getRemarks());

            this.cn_tf.setText(this.current.getCobId());
            this.activity_tf.setText(this.current.getActivity());
            this.board_res_tf.setText(AppDAO.get(this.current.getAppId()).getBoardRes());
            int index = 0, select = -1;
            for(FundSource f : this.fs_cb.getItems()){
                if (f.getSource().equals(this.current.getFundSource().getSource())){
                    select = index;
                }
                index++;
            }
            this.fs_cb.getSelectionModel().select(select);
            this.type_cb.getSelectionModel().select(this.current.getType());

            this.items = FXCollections.observableArrayList(this.current.getItems());
            this.cob_items.setItems(this.items);
            this.cob_items.refresh();

            this.prepared_tf.setText(this.current.getPrepared().getEmployeeFirstName()+" "+this.current.getPrepared().getEmployeeLastName()+" ("+this.current.getDatePrepared()+")");
            this.threshold = DeptThresholdDAO.find(this.current.getAppId(), this.current.getPrepared().getDepartmentID());
            this.totalAppropriations = DeptThresholdDAO.getTotalAppropriations(threshold);
            this.appropFromDB = this.totalAppropriations;

            this.setAmount();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to delete a COB item, automatically update table and values
     * @param item - COB item reference
     */
    public void deleteItem(COBItem item){
        JFXButton accept = new JFXButton("Proceed");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Delete COB Item", "This process is final. Confirm delete?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(__ -> {
            try{
                int row = this.cob_items.getSelectionModel().getSelectedIndex();
                this.items.remove(row);
                CobItemDAO.delete(current, item);
                this.setAmount();
                this.cob_items.refresh();
                AlertDialogBuilder.messgeDialog("Delete COB Item", "The COB item was successfully removed from the COB!",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
            }catch (Exception e){
                AlertDialogBuilder.messgeDialog("System Error", "An SQL error occurred: " + e.getMessage(),
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
            dialog.close();
        });
    }
    public void refreshItems(){
        this.cob_items.refresh();
        setAmount();
    }
}
