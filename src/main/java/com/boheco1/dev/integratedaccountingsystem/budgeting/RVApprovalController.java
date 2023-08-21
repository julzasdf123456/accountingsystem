package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RVApprovalController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXButton approve_btn;

    @FXML
    private JFXButton revised_btn;

    @FXML
    private TableView<RVItem> rv_items;

    @FXML
    private Label threshold_lbl;

    @FXML
    private JFXTextField totals_tf;

    @FXML
    private JFXTextField prepared_tf;

    @FXML
    private JFXTextField reviewed_tf;

    @FXML
    private JFXTextField budget_tf;

    @FXML
    private JFXTextField approved_tf;

    @FXML
    private JFXTextField rvno_tf;

    @FXML
    private JFXTextField to_tf;

    @FXML
    private JFXTextField purpose_tf;

    @FXML
    private JFXTextField date_tf;

    private RV current;
    private EmployeeInfo currentUser = null;
    private ObservableList<RVItem> items = FXCollections.observableArrayList(new ArrayList<>());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.current = (RV) Utility.getSelectedObject();

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
        this.approve_btn.setOnAction(evt -> {

            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Approve RV", "This process is final. Confirm Approval?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                try {
                    //If user is the general manager
                    if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().equals(Utility.RV_APPROVAL)) {
                        RVDAO.approveRV(this.current);
                        this.approved_tf.setText(this.currentUser.getEmployeeFirstName() + " " + this.currentUser.getEmployeeLastName());
                    //If user is dept manager
                    }else if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_RECOMMENDATION)) {
                        RVDAO.recommendRV(this.current);
                        this.reviewed_tf.setText(this.currentUser.getEmployeeFirstName() + " " + this.currentUser.getEmployeeLastName());
                    //If user is the budget offer
                    } else if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)) {
                        RVDAO.certifyRV(this.current);
                        this.budget_tf.setText(this.currentUser.getEmployeeFirstName() + " " + this.currentUser.getEmployeeLastName());
                    }
                    //Disable the buttons
                    this.approve_btn.setVisible(false);
                    this.revised_btn.setVisible(false);
                    this.approve_btn.setDisable(true);
                    this.revised_btn.setDisable(true);
                    AlertDialogBuilder.messgeDialog("System Information", "The prepared RV was approved!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "An error was encountered due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    e.printStackTrace();
                }
                dialog.close();
            });
        });

        this.revised_btn.setOnAction(evt -> {
            JFXButton accept = new JFXButton("Revise");
            JFXTextArea input = new JFXTextArea();
            JFXDialog dialog = DialogBuilder.showInputDialog("Revise RV", "Remarks", input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setOnAction(e -> {
                if(input.getText().length() == 0) {
                    AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid remarks!",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else {
                    try {
                        this.current.setRemarks(input.getText());
                        RVDAO.reviseRV(this.current);
                        this.approve_btn.setVisible(false);
                        this.revised_btn.setVisible(false);
                        this.approve_btn.setDisable(true);
                        this.revised_btn.setDisable(true);
                        AlertDialogBuilder.messgeDialog("System Information", "The prepared RV was sent back to the preparer with the remarks!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
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
     *
     * @return void
     */
    public void setControls() {
        //Disable button functions for users who are not managers or budget officer or general manager
        this.approve_btn.setVisible(false);
        this.revised_btn.setVisible(false);
        this.approve_btn.setDisable(true);
        this.revised_btn.setDisable(true);

        //If user is the general manager and current RV is not certified or it is approved, disable buttons
        if (this.currentUser !=null&&this.currentUser.getDesignation().toLowerCase().equals(Utility.RV_APPROVAL)){
            //If amount is 5000 or more, else dont show the buttons
            if (this.current.getAmount() >= 5000) {
                if (this.current.getBudgetOfficer() == null || this.current.getApproved() != null) {
                    this.approve_btn.setVisible(false);
                    this.revised_btn.setVisible(false);
                    this.approve_btn.setDisable(true);
                    this.revised_btn.setDisable(true);
                } else {
                    this.approve_btn.setVisible(true);
                    this.revised_btn.setVisible(true);
                    this.approve_btn.setDisable(false);
                    this.revised_btn.setDisable(false);
                }
            }
        //If user is dept manager and current RV is not reviewed, enable buttons, otherwise
        }else if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_RECOMMENDATION)){
            if (this.current.getRecommended() == null) {
                this.approve_btn.setVisible(true);
                this.revised_btn.setVisible(true);
                this.approve_btn.setDisable(false);
                this.revised_btn.setDisable(false);
            }
        //If user is the budget officer and current RV is not reviewed or it is certified by budget officer, disable buttons
        }else if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)) {
            if (this.current.getRecommended() == null || this.current.getBudgetOfficer() != null) {
                this.approve_btn.setVisible(false);
                this.revised_btn.setVisible(false);
                this.approve_btn.setDisable(true);
                this.revised_btn.setDisable(true);
            } else {
                this.approve_btn.setVisible(true);
                this.revised_btn.setVisible(true);
                this.approve_btn.setDisable(false);
                this.revised_btn.setDisable(false);
            }
        }
    }
    /**
     * Initializes the COB Items (Supplies and Materials, and other budget which follows Description, Cost, Unit, Quantity, Amount) table
     * @return void
     */
    public void createTable(){
        TableColumn<RVItem, String> column0 = new TableColumn<>("COB No.");
        column0.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getCobId() : ""));
        column0.setStyle("-fx-alignment: center-left;");
        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);

        TableColumn<RVItem, String> column = new TableColumn<>("Articles");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getLevel() == 1 ? obj.getValue().getDescription() : "  "+obj.getValue().getDescription()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<RVItem, String> column1 = new TableColumn<>("Price (based on COB)");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getCost()) : ""));
        column1.setPrefWidth(175);
        column1.setMaxWidth(175);
        column1.setMinWidth(175);
        column1.setStyle("-fx-alignment: center-right;");

        TableColumn<RVItem, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<RVItem, String> column3 = new TableColumn<>("Qty");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getQty() > 0 ? obj.getValue().getQty()+"" : ""));
        column3.setPrefWidth(75);
        column3.setMaxWidth(75);
        column3.setMinWidth(75);
        column3.setStyle("-fx-alignment: center;");


        TableColumn<RVItem, String> column4 = new TableColumn<>("Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCost() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        column4.setPrefWidth(200);
        column4.setMaxWidth(200);
        column4.setMinWidth(200);
        column4.setStyle("-fx-alignment: center-right;");

        this.rv_items.setFixedCellSize(35);
        this.rv_items.setPlaceholder(new Label("No Items added"));

        this.rv_items.getColumns().add(column0);
        this.rv_items.getColumns().add(column);
        this.rv_items.getColumns().add(column1);
        this.rv_items.getColumns().add(column2);
        this.rv_items.getColumns().add(column3);
        this.rv_items.getColumns().add(column4);
    }

    public void setDetails(){
        try {
            this.current.setItems(RVItemDAO.getItems(this.current));
            this.rvno_tf.setText(this.current.getRvNo());
            this.to_tf.setText(this.current.getTo());
            this.purpose_tf.setText(this.current.getPurpose());
            this.date_tf.setText(this.current.getRvDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));


            this.createTable();

            this.items = FXCollections.observableArrayList(this.current.getItems());
            this.rv_items.setItems(this.items);
            this.rv_items.refresh();

            this.totals_tf.setText(Utility.formatDecimal(this.current.getAmount()));

            this.prepared_tf.setText(this.current.getRequisitioner().getEmployeeFirstName()+" "+this.current.getRequisitioner().getEmployeeLastName()+" ("+this.current.getRvDate()+")");
            this.reviewed_tf.setText(this.current.getRecommended() != null ? this.current.getRecommended().getEmployeeFirstName()+" "+this.current.getRecommended().getEmployeeLastName()+" ("+this.current.getDateRecommended()+")" : "");
            this.budget_tf.setText(this.current.getBudgetOfficer() != null ? this.current.getBudgetOfficer().getEmployeeFirstName()+" "+this.current.getBudgetOfficer().getEmployeeLastName()+" ("+this.current.getDateBudgeted()+")" : "");
            this.approved_tf.setText(this.current.getApproved() != null ? this.current.getApproved().getEmployeeFirstName()+" "+this.current.getApproved().getEmployeeLastName()+" ("+this.current.getDateApproved()+")" : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
