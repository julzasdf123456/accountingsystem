package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.DeptThresholdDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Paint;

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
    private Label threshold_lbl;

    @FXML
    private JFXTextField totals_tf;

    @FXML
    private JFXTextField q1_tf;

    @FXML
    private JFXTextField q2_tf;

    @FXML
    private JFXTextField q3_tf;

    @FXML
    private JFXTextField q4_tf;

    @FXML
    private JFXTextField prepared_tf;

    @FXML
    private JFXTextField reviewed_tf;

    @FXML
    private JFXTextField approved_tf;

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton revised_btn;

    private COB current;
    private EmployeeInfo currentUser = null;

    private DeptThreshold threshold = null;
    private double totalAppropriations = 0, cobAmount = 0, appropFromDB = 0;
    private String totalBudget = "Current Total Appropriation: ₱ %.2f out of ₱ %.2f Department Threshold!";

    private ObservableList<COBItem> items = FXCollections.observableArrayList(new ArrayList<>());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.current = (COB) Utility.getSelectedObject();

        //Set current user
        this.currentUser = null;
        try {
            this.currentUser = ActiveUser.getUser().getEmployeeInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Set Controls for current active user
        this.setControls();

        //Set budget details
        this.setDetails();

        //Approved by Budget Officer or Reviewed by Department Manager
        this.add_btn.setOnAction(evt ->{
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Approve COB", "This process is final. Confirm Approval?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                try {
                    //If user is dept manager
                    if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_REVIEWER)) {
                        CobDAO.reviewCob(this.current);
                        //Set the name in the textfield
                        this.reviewed_tf.setText(this.currentUser.getEmployeeFirstName()+" "+this.currentUser.getEmployeeLastName());
                    //If user is the budget offer
                    } else if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_APPROVAL)) {
                        CobDAO.approveCob(this.current);
                        //Set the name in the textfield
                        this.approved_tf.setText(this.currentUser.getEmployeeFirstName()+" "+this.currentUser.getEmployeeLastName());
                    }
                    //Disable the buttons
                    this.add_btn.setVisible(false);
                    this.revised_btn.setVisible(false);
                    this.add_btn.setDisable(true);
                    this.revised_btn.setDisable(true);
                    AlertDialogBuilder.messgeDialog("System Information", "The prepared budget was approved!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                }catch (Exception e){
                    AlertDialogBuilder.messgeDialog("System Error", "An error was encountered due to: "+e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    e.printStackTrace();
                }
                dialog.close();
            });
        });

        this.revised_btn.setOnAction(evt ->{
            JFXButton accept = new JFXButton("Revise");
            JFXTextArea input = new JFXTextArea();
            JFXDialog dialog = DialogBuilder.showInputDialog("Revise COB", "Remarks", input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setOnAction(e -> {
                if(input.getText().length() == 0) {
                    AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid remarks!",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else {
                    try {
                        this.current.setRemarks(input.getText());
                        CobDAO.reviseCob(this.current);
                        this.add_btn.setVisible(false);
                        this.revised_btn.setVisible(false);
                        this.add_btn.setDisable(true);
                        this.revised_btn.setDisable(true);
                        AlertDialogBuilder.messgeDialog("System Information", "The prepared budget was sent back to the preparer with the remarks!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                    } catch (Exception ex) {
                        AlertDialogBuilder.messgeDialog("System Error", "An error was encountered due to: "+ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        ex.printStackTrace();
                    }
                }
                dialog.close();
            });
        });
    }
    /**
     * Initializes add/revise buttons for users
     * @return void
     */
    public void setControls(){
        //Disable button functions for users who are not managers or budget officer
        this.add_btn.setVisible(false);
        this.revised_btn.setVisible(false);
        this.add_btn.setDisable(true);
        this.revised_btn.setDisable(true);
        //If user is dept manager and current COB is not reviewed, enable buttons, otherwise
        if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_REVIEWER)){
            if (this.current.getReviewed() == null) {
                this.add_btn.setVisible(true);
                this.revised_btn.setVisible(true);
                this.add_btn.setDisable(false);
                this.revised_btn.setDisable(false);
            }
        //If user is the budget offer and current COB is not reviewed or it is approved, disable buttons
        }else if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_APPROVAL)){
            if (this.current.getReviewed() == null || this.current.getApproved() != null) {
                this.add_btn.setVisible(false);
                this.revised_btn.setVisible(false);
                this.add_btn.setDisable(true);
                this.revised_btn.setDisable(true);
            }else{
                this.add_btn.setVisible(true);
                this.revised_btn.setVisible(true);
                this.add_btn.setDisable(false);
                this.revised_btn.setDisable(false);
            }
        }
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
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()) : ""));
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

    public void setDetails(){
        try {
            this.current.setItems(CobItemDAO.getItems(this.current));
            this.cn_tf.setText(this.current.getCobId());
            this.activity_tf.setText(this.current.getActivity());
            this.board_res_tf.setText(AppDAO.get(this.current.getAppId()).getBoardRes());
            this.fs_tf.setText(this.current.getFundSource().getSource());
            this.type_tf.setText(this.current.getType());

            if (this.current.getType().equals(COBItem.TYPES[0])) {
                this.createTable();
            }else if (this.current.getType().equals(COBItem.TYPES[1])) {
                this.createRepresentationTable();
            }else if (this.current.getType().equals(COBItem.TYPES[2])) {
                this.createSalariesTable();
            }else if (this.current.getType().equals(COBItem.TYPES[3])) {
                this.createTravelsTable();
            }else{
                this.createTable();
            }
            this.items = FXCollections.observableArrayList(this.current.getItems());
            this.cob_items.setItems(this.items);
            this.cob_items.refresh();

            this.prepared_tf.setText(this.current.getPrepared().getEmployeeFirstName()+" "+this.current.getPrepared().getEmployeeLastName()+" ("+this.current.getDatePrepared()+")");
            this.reviewed_tf.setText(this.current.getReviewed() != null ? this.current.getReviewed().getEmployeeFirstName()+" "+this.current.getReviewed().getEmployeeLastName()+" ("+this.current.getDateReviewed()+")" : "");
            this.approved_tf.setText(this.current.getApproved() != null ? this.current.getApproved().getEmployeeFirstName()+" "+this.current.getApproved().getEmployeeLastName()+" ("+this.current.getDateApproved()+")" : "");
            this.threshold = DeptThresholdDAO.find(this.current.getAppId(), this.current.getPrepared().getDepartmentID());
            this.totalAppropriations = DeptThresholdDAO.getTotalAppropriations(threshold);
            this.appropFromDB = this.totalAppropriations;

            this.setAmount();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        this.totals_tf.setText(Utility.formatDecimal(this.current.getAmount()));
        this.q1_tf.setText(Utility.formatDecimal(qtr1));
        this.q2_tf.setText(Utility.formatDecimal(qtr2));
        this.q3_tf.setText(Utility.formatDecimal(qtr3));
        this.q4_tf.setText(Utility.formatDecimal(qtr4));

        this.threshold_lbl.setText(String.format(this.totalBudget, this.totalAppropriations, this.threshold.getThreshAmount()));
    }
}
