package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.RVDAO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RVController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton remove_btn;

    @FXML
    private TableView<RVItem> rv_items;

    @FXML
    private Label threshold_lbl;

    @FXML
    private TextField totals_tf;

    @FXML
    private JFXTextField prepared_tf;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private JFXButton submit_btn;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private JFXTextField rvno_tf;

    @FXML
    private JFXTextField to_tf;

    @FXML
    private JFXTextField purpose_tf;

    @FXML
    private JFXTextField date_tf;

    @FXML
    private CheckBox certify_cb;

    private ObservableList<RVItem> items = FXCollections.observableArrayList(new ArrayList<>());
    private EmployeeInfo emp = null;
    private Department dept = null;

    private double rvAmount = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.submit_btn.setDisable(true);

        this.certify_cb.setOnAction(evt -> {
            this.submit_btn.setDisable(!this.certify_cb.isSelected());
        });
        this.setDetails();

        this.createTable();

        this.add_btn.setOnAction(evt -> {
            try {
                this.showAddItemForm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.remove_btn.setOnAction(evt -> {
            try {
                int row = this.rv_items.getSelectionModel().getSelectedIndex();
                this.items.remove(row);
                this.rv_items.refresh();
                this.setTable();
                this.setAmount();
            }catch (Exception e){
                //Do nothing
            }
        });
        this.rv_items.setRowFactory(tv -> {
            TableRow<RVItem> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem remove = new MenuItem("Remove Item");
            remove.setOnAction(actionEvent -> {
                this.rv_items.getItems().remove(row.getItem());
                this.rv_items.refresh();
                this.setAmount();
            });


            MenuItem removeAll = new MenuItem("Clear Items");
            removeAll.setOnAction(actionEvent -> {
                this.items = FXCollections.observableArrayList(new ArrayList<>());
                this.rv_items.setItems(this.items);
                this.rv_items.refresh();
                this.setAmount();
            });

            rowMenu.getItems().addAll(remove, new SeparatorMenuItem(), removeAll);


            row.contextMenuProperty().bind(
            Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(rowMenu));
            return row;
        });

        this.submit_btn.setOnAction(evt ->{
            this.submitRV();
        });

        this.reset_btn.setOnAction(evt ->{
            this.reset();
        });

        //Pass this controller to the Add Item controller
        Utility.setParentController(this);

        this.progressBar.setVisible(false);
    }
    public void setDetails(){
        try{
            this.emp = ActiveUser.getUser().getEmployeeInfo();
            this.dept = this.emp.getDepartment();
            String d = this.dept.getDepartmentName();
            LocalDate date = Utility.serverDate();
            this.date_tf.setText(date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            this.rvno_tf.setText(date.getYear()+"-"+this.dept.getDepartmentName()+"-"+ (RVDAO.countRv(d)+1));
            this.prepared_tf.setText(this.emp.getEmployeeFirstName()+" "+this.emp.getEmployeeLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the RV Items (Supplies and Materials, and other budget which follows Description, Cost, Unit, Quantity, Amount) table
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
    /**
     * Insert RV
     * @return void
     */
    public void submitRV(){
        final String rvno = this.rvno_tf.getText();
        final String to = this.to_tf.getText();
        final String purpose = this.purpose_tf.getText();

        if (rvno.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the RV number! Format: YEAR-DEPT-NO",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (to.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the TO field title for this request!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (purpose.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide the purpose of this request!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.rvAmount == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add the requested item(s)!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.items.size() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add the requested item(s)!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else {
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Submit Requisition Voucher", "This process is final. Confirm submission?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                dialog.close();
                try {
                    RV rv = new RV();
                    rv.setRvNo(rvno);
                    rv.setTo(to);
                    rv.setPurpose(purpose);
                    rv.setAmount(rvAmount);
                    rv.setItems(this.items);
                    RVDAO.createRV(rv);
                    reset();
                    LocalDate date = Utility.serverDate();
                    this.date_tf.setText(date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    this.rvno_tf.setText(date.getYear()+"-"+this.dept.getDepartmentName()+"-"+ (RVDAO.countRv(this.dept.getDepartmentName())+1));
                    AlertDialogBuilder.messgeDialog("Submit RV", "The RV was successfully submitted and awaiting review by your department head!",
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../budgeting/budgeting_add_rv_item.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        AddRVItemController ctrl = fxmlLoader.getController();
        ctrl.setCurrentList(items);
        dialog.show();
    }

    public void setTable() {
        this.rv_items.setItems(this.items);
        this.rv_items.refresh();
    }

    public void setAmount(){
        double total = 0;

        for (COBItem i : this.items){
            total += i.getAmount();
        }
        this.rvAmount = total;
        this.totals_tf.setText(Utility.formatDecimal(total));
    }

    public void reset(){
        this.items = FXCollections.observableArrayList(new ArrayList<>());
        this.rv_items.setItems(this.items);
        this.rvAmount = 0;
        this.totals_tf.setText("");
        this.purpose_tf.setText("");
        this.to_tf.setText("The General Manager");
        this.certify_cb.setSelected(false);
        this.submit_btn.setDisable(true);
    }

    @Override
    public void receive(Object o) {
        if (o instanceof COBItem) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    //boolean found = false;
                    RVItem item = (RVItem) o;
                    /*int index = 0;
                    for (RVItem c : items) {
                        if (c.getcItemId().equals(item.getcItemId()) && c.getCobId().equals(item.getCobId())){
                            int requested = item.getQty();
                            items.get(index).setQty(c.getQty()+requested);
                            found = true;
                            break;
                        }
                        index++;
                    }
                    if (!found){*/
                        item.setSequence(items.size()+1);
                        items.add(item);
                    //}
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
                    List<RVItem> imported = (List<RVItem>) o;
                    for(RVItem i : imported){
                        i.setcItemId(Utility.generateRandomId());
                    }
                    items = FXCollections.observableArrayList(imported);
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
}
