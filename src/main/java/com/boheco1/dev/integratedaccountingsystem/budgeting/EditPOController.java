package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.PODAO;
import com.boheco1.dev.integratedaccountingsystem.dao.POItemDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.RVItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditPOController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private TableView<POItem> po_items;

    @FXML
    private JFXTextField totals_tf;

    @FXML
    private JFXTextField prepared_tf;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private JFXButton submit_btn;

    @FXML
    private ProgressBar progressBar;

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
    private JFXButton add_btn;

    @FXML
    private JFXButton remove_btn;

    private ObservableList<POItem> items = FXCollections.observableArrayList(new ArrayList<>());
    private double poAmount = 0;
    private int no = 1;
    private PurchaseOrder current = null;
    private EmployeeInfo currentUser = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.current = (PurchaseOrder) Utility.getSelectedObject();

        this.createTable();

        //Set budget details
        this.setDetails();

        this.add_btn.setOnAction(evt -> {
            try {
                this.showAddItemForm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.remove_btn.setOnAction(evt -> {
            POItem selected = this.po_items.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    this.deleteItem(selected);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "A System error occurred: " + e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            }else{
                AlertDialogBuilder.messgeDialog("Delete RV Item", "Please select the item to delete and try again!",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
        this.po_items.setRowFactory(tv -> {
            TableRow<POItem> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem remove = new MenuItem("Remove Item");
            remove.setOnAction(actionEvent -> {
                deleteItem(row.getItem());
            });

            rowMenu.getItems().addAll(remove);

            row.contextMenuProperty().bind(
            Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(rowMenu));
            return row;
        });

        this.submit_btn.setOnAction(evt ->{
            this.submitPO();
        });

        this.reset_btn.setOnAction(evt ->{
            Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_user_po_list.fxml"));
        });

        //Pass this controller to the Add Item controller
        Utility.setParentController(this);

        this.progressBar.setVisible(false);
    }
    public void setDetails(){
        try{
            this.current.setItems(POItemDAO.getItems(this.current));
            this.items = FXCollections.observableArrayList(this.current.getItems());
            this.poAmount = this.current.getAmount();
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
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<POItem, String> column3 = new TableColumn<>("Description");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() > 0 ? obj.getValue().getDescription() : ""));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<POItem, String> column4 = new TableColumn<>("RV No");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() > 0 ? obj.getValue().getRVNo() : ""));
        column4.setStyle("-fx-alignment: center-left;");
        column4.setPrefWidth(110);
        column4.setMaxWidth(110);
        column4.setMinWidth(110);

        TableColumn<POItem, String> column5 = new TableColumn<>("Unit Price");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() != 0 ? Utility.formatDecimal(obj.getValue().getPOPrice()) : ""));
        column5.setPrefWidth(175);
        column5.setMaxWidth(175);
        column5.setMinWidth(175);
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<POItem, String> column6 = new TableColumn<>("Amount");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAmount() != 0 ? Utility.formatDecimal(obj.getValue().getAmount()) : ""));
        column6.setPrefWidth(200);
        column6.setMaxWidth(200);
        column6.setMinWidth(200);
        column6.setStyle("-fx-alignment: center-right;");

        this.po_items.setFixedCellSize(35);
        this.po_items.setPlaceholder(new Label("No Items added"));

        //this.po_items.getColumns().add(column);
        this.po_items.getColumns().add(column1);
        this.po_items.getColumns().add(column2);
        this.po_items.getColumns().add(column3);
        this.po_items.getColumns().add(column4);
        this.po_items.getColumns().add(column5);
        this.po_items.getColumns().add(column6);
    }
    /**
     * Insert PO
     * @return void
     */
    public void submitPO(){
        final String pono = this.pono_tf.getText();
        final String to = this.to_tf.getText();
        final String address = this.address_tf.getText();
        final String contact = this.contact_tf.getText();
        final String terms = this.terms_tf.getText();

        if (pono.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the PO number! Format: YEAR-DEPT-NO",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (to.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the company/business/provider for this purchase order!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (address.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the address!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (contact.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the contact details!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (terms.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the terms!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.poAmount == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add the requested item(s)!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.items.size() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add the requested item(s)!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else {
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Submit Purchase Order", "This process is final. Confirm submission?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                dialog.close();
                try {
                    PurchaseOrder po = new PurchaseOrder();
                    po.setPoNo(pono);
                    po.setTo(to);
                    po.setAddress(address);
                    po.setContact(contact);
                    po.setTerms(terms);
                    po.setAmount(poAmount);
                    PODAO.submitRevisedPO(po);
                    this.add_btn.setDisable(true);
                    this.submit_btn.setDisable(true);
                    this.remove_btn.setDisable(true);
                    this.add_btn.setVisible(false);
                    this.submit_btn.setVisible(false);
                    this.remove_btn.setVisible(false);
                    AlertDialogBuilder.messgeDialog("Submit PO", "The Purchase Order was successfully submitted and awaiting review by the General Manager!",
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_po_item.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddPOItemController ctrl = fxmlLoader.getController();
        ctrl.setCurrentList(items);
        dialog.show();
    }

    public void setTable() {
        this.po_items.setItems(this.items);
        this.po_items.refresh();
    }

    public void setAmount(){
        double total = 0;

        for (POItem i : this.items){
            total += i.getAmount();
        }
        this.poAmount = total;
        this.totals_tf.setText(Utility.formatDecimal(total));
    }

    public void reset(){
        this.items = FXCollections.observableArrayList(new ArrayList<>());
        this.po_items.setItems(this.items);
        this.poAmount = 0;
        this.totals_tf.setText("");
        this.terms_tf.setText("30 Days");
        this.to_tf.setText("");
        this.address_tf.setText("");
        this.contact_tf.setText("");
    }

    @Override
    public void receive(Object o) {
        if (o instanceof POItem) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                POItem item = (POItem) o;
                try {
                    //item.setNo(no);
                    //no++;
                    if (items.size() == 0) {
                        item.setSequence(items.size() + 1);
                    }else{
                        item.setSequence(items.get(items.size() - 1).getSequence() + 1);
                    }
                    POItemDAO.add(current, item);
                    items.add(item);
                } catch (Exception e) {
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

    /**
     * Method to delete a PO item, automatically update table and values
     * @param item - PO item reference
     */
    public void deleteItem(POItem item){
        JFXButton accept = new JFXButton("Proceed");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Delete PO Item", "This process is final. Confirm delete?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(__ -> {
            try{
                int row = this.po_items.getSelectionModel().getSelectedIndex();
                POItemDAO.delete(current, item);
                this.items.remove(row);
                this.setAmount();
                this.po_items.refresh();
                AlertDialogBuilder.messgeDialog("Delete PO Item", "The PO item was successfully removed from the Purchase Order!",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
            }catch (Exception e){
                AlertDialogBuilder.messgeDialog("System Error", "An SQL error occurred: " + e.getMessage(),
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
            dialog.close();
        });
    }
}
