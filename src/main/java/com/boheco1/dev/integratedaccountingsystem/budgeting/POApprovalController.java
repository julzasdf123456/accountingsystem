package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.PODAO;
import com.boheco1.dev.integratedaccountingsystem.dao.POItemDAO;
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
import javafx.scene.control.*;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class POApprovalController extends MenuControllerHandler implements Initializable {

    @FXML
    private TableView<POItem> po_items;

    @FXML
    private JFXTextField totals_tf;

    @FXML
    private JFXTextField prepared_tf;

    @FXML
    private JFXTextField approved_tf;

    @FXML
    private JFXTextField to_tf;

    @FXML
    private JFXTextField pono_tf;

    @FXML
    private JFXTextField address_tf;

    @FXML
    private JFXTextField contact_tf;

    @FXML
    private JFXTextField date_tf;

    @FXML
    private JFXTextField terms_tf;

    @FXML
    private JFXButton approve_btn;

    @FXML
    private JFXButton revise_btn;

    private ObservableList<POItem> items = FXCollections.observableArrayList(new ArrayList<>());

    private double poAmount = 0;
    private int no = 1;
    private PurchaseOrder current = null;
    private EmployeeInfo currentUser = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.current = (PurchaseOrder) Utility.getSelectedObject();

        //Set current user
        this.currentUser = null;
        try {
            this.currentUser = ActiveUser.getUser().getEmployeeInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.createTable();

        //Set Controls for current active user
        this.setControls();

        //Set budget details
        this.setDetails();

        this.approve_btn.setOnAction(evt -> {
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Approve Purchase Order", "This process is final. Confirm Approval?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                try {
                    //If user is the general manager
                    if (this.currentUser != null && this.currentUser.getDesignation().toLowerCase().equals(Utility.PO_APPROVAL)) {
                        PODAO.approvePO(this.current);
                        this.approved_tf.setText((this.currentUser.getEmployeeFirstName() + " " + this.currentUser.getEmployeeLastName()).toUpperCase());
                    }
                    //Disable the buttons
                    this.approve_btn.setVisible(false);
                    this.revise_btn.setVisible(false);
                    this.approve_btn.setDisable(true);
                    this.revise_btn.setDisable(true);
                    AlertDialogBuilder.messgeDialog("System Information", "The prepared PO was approved!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "An error was encountered due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    e.printStackTrace();
                }
                dialog.close();
            });
        });

        this.revise_btn.setOnAction(evt -> {
            JFXButton accept = new JFXButton("Revise");
            JFXTextArea input = new JFXTextArea();
            JFXDialog dialog = DialogBuilder.showInputDialog("Revise Purchase Order", "Remarks", input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setOnAction(e -> {
                if(input.getText().length() == 0) {
                    AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid remarks!",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else {
                    try {
                        this.current.setRemarks(input.getText());
                        PODAO.revisePO(this.current);
                        this.approve_btn.setVisible(false);
                        this.revise_btn.setVisible(false);
                        this.approve_btn.setDisable(true);
                        this.revise_btn.setDisable(true);
                        AlertDialogBuilder.messgeDialog("System Information", "The prepared PO was sent back to the preparer with the remarks!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
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
        //Disable button functions for users who are not general manager
        this.approve_btn.setVisible(false);
        this.revise_btn.setVisible(false);
        this.approve_btn.setDisable(true);
        this.revise_btn.setDisable(true);

        //If user is the general manager and current PO is approved, disable buttons
        if (this.currentUser !=null && this.currentUser.getDesignation().toLowerCase().equals(Utility.PO_APPROVAL)){
            if (this.current.getGeneralManager() != null) {
                this.approve_btn.setVisible(false);
                this.revise_btn.setVisible(false);
                this.approve_btn.setDisable(true);
                this.revise_btn.setDisable(true);
            } else {
                this.approve_btn.setVisible(true);
                this.revise_btn.setVisible(true);
                this.approve_btn.setDisable(false);
                this.revise_btn.setDisable(false);
            }
        }
    }
    public void setDetails(){
        try{
            this.current.setItems(POItemDAO.getItems(this.current));
            this.items = FXCollections.observableArrayList(this.current.getItems());
            this.po_items.setItems(this.items);
            this.po_items.refresh();
            this.totals_tf.setText(Utility.formatDecimal(this.current.getAmount()));
            this.pono_tf.setText(this.current.getPoNo());
            this.date_tf.setText(this.current.getPoDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            this.to_tf.setText(this.current.getTo());
            this.address_tf.setText(this.current.getAddress());
            this.contact_tf.setText(this.current.getContact());
            this.terms_tf.setText(this.current.getTerms());
            this.prepared_tf.setText((this.current.getPreparer().getEmployeeFirstName()+" "+this.current.getPreparer().getEmployeeLastName()).toUpperCase());
            this.approved_tf.setText((this.current.getGeneralManager() != null ? this.current.getGeneralManager().getEmployeeFirstName()+" "+this.current.getGeneralManager().getEmployeeLastName() : "").toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the PO Items (Supplies and Materials, and other budget which follows Description, Cost, Unit, Quantity, Amount) table
     * @return void
     */
    public void createTable(){
        TableColumn<POItem, String> column = new TableColumn<>("No");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() != 0 ? obj.getValue().getNo()+"" : ""));
        column.setPrefWidth(50);
        column.setMaxWidth(50);
        column.setMinWidth(50);
        column.setStyle("-fx-alignment: center;");

        TableColumn<POItem, String> column1 = new TableColumn<>("Qty");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() > 0 ? obj.getValue().getPOQty()+"" : ""));
        column1.setPrefWidth(75);
        column1.setMaxWidth(75);
        column1.setMinWidth(75);
        column1.setStyle("-fx-alignment: center;");

        TableColumn<POItem, String> column2 = new TableColumn<>("Unit");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() != 0 ? obj.getValue().getRemarks()+"" : ""));
        column2.setPrefWidth(75);
        column2.setMaxWidth(75);
        column2.setMinWidth(75);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<POItem, String> column3 = new TableColumn<>("Description");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() > 0 ? obj.getValue().getDescription() : ""));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<POItem, String> column31 = new TableColumn<>("Details");
        column31.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() > 0 ? obj.getValue().getDetails() : ""));
        column31.setStyle("-fx-alignment: center-left;");

        TableColumn<POItem, String> column4 = new TableColumn<>("RV No");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() > 0 ? obj.getValue().getRVNo() : ""));
        column4.setStyle("-fx-alignment: center-left;");
        column4.setPrefWidth(120);
        column4.setMaxWidth(120);
        column4.setMinWidth(120);

        TableColumn<POItem, String> column5 = new TableColumn<>("Unit Price");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() != 0 ? Utility.formatDecimal(obj.getValue().getPOPrice()) : ""));
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<POItem, String> column6 = new TableColumn<>("Amount");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        column6.setPrefWidth(150);
        column6.setMaxWidth(150);
        column6.setMinWidth(150);
        column6.setStyle("-fx-alignment: center-right;");

        this.po_items.setFixedCellSize(35);
        this.po_items.setPlaceholder(new Label("No Items added"));

        //this.po_items.getColumns().add(column);
        this.po_items.getColumns().add(column1);
        this.po_items.getColumns().add(column2);
        this.po_items.getColumns().add(column3);
        this.po_items.getColumns().add(column31);
        this.po_items.getColumns().add(column4);
        this.po_items.getColumns().add(column5);
        this.po_items.getColumns().add(column6);
    }

    public void setAmount(){
        double total = 0;

        for (POItem i : this.items){
            total += i.getAmount();
        }
        this.poAmount = total;
        this.totals_tf.setText(Utility.formatDecimal(total));
    }
}
