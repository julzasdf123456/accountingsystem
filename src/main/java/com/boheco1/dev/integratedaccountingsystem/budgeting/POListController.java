package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.PODAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class POListController extends MenuControllerHandler implements Initializable {

    @FXML
    private ComboBox<String> date_pker;

    @FXML
    private JFXButton view_btn;

    @FXML
    private Label app_status_lbl;

    @FXML
    private TabPane tabs;

    @FXML
    private Tab revision_tab;

    @FXML
    private TableView<POItem> revision_table;

    @FXML
    private Label revision_lbl;

    @FXML
    private Tab pending_tab;

    @FXML
    private TableView<POItem> pending_table;

    @FXML
    private Label pending_lbl;

    @FXML
    private Tab approved_tab;

    @FXML
    private TableView<POItem> approved_table;

    @FXML
    private Label approved_lbl;

    @FXML
    private ProgressBar progressbar;

    private ObservableList<PurchaseOrder> revisionItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<PurchaseOrder> pendingItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<PurchaseOrder> approvedItems = FXCollections.observableArrayList(new ArrayList<>());
    private EmployeeInfo currentUser = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.currentUser = ActiveUser.getUser().getEmployeeInfo();
        }catch (Exception e) {
            e.printStackTrace();
        }

        this.setYears();
        this.createRVTable(revision_table, "No POs pending for revision.", "View PO", "budgeting_edit_po", "Remarks", "Date Prepared");
        this.createRVTable(pending_table, "No POs pending approval by the General Manager.", "View PO", "budgeting_po_approval", null, "Date Prepared");
        this.createRVTable(approved_table, "No POs approved by the General Manager.", "View PO", "budgeting_po_approval", null, "Date Prepared");
        this.setInformation();

        this.view_btn.setOnAction(evt -> {
            this.setInformation();
        });
    }
    /**
     * Displays the information of the RVs
     */
    public void setInformation(){
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                String year = date_pker.getSelectionModel().getSelectedItem().toString();
                if (year != null) {
                    try{
                        //if user is GM, all approved POs
                        if (currentUser.getDesignation().toLowerCase().equals(Utility.PO_APPROVAL)) {
                            pendingItems = FXCollections.observableArrayList(PODAO.getAll(year, PurchaseOrder.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(PODAO.getAll(year, PurchaseOrder.APPROVED));
                        //if user is budget officer, all recommended RVs, certified RVs and approved RVs
                        }else if (currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)){
                            pendingItems = FXCollections.observableArrayList(PODAO.getAll(year, PurchaseOrder.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(PODAO.getAll(year, PurchaseOrder.APPROVED));
                        //if user is department manager, department RVs pending recommendation, department RVs pending certification, approved department RVs
                        }else if (currentUser.getDesignation().toLowerCase().contains(Utility.COB_REVIEWER)){
                            pendingItems = FXCollections.observableArrayList(PODAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), PurchaseOrder.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(PODAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), PurchaseOrder.APPROVED));
                        //if preparer in a department
                        }else{
                            revisionItems = FXCollections.observableArrayList(PODAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), PurchaseOrder.PENDING_REVISION));
                            pendingItems = FXCollections.observableArrayList(PODAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), PurchaseOrder.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(PODAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), PurchaseOrder.APPROVED));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        task.setOnRunning(wse -> {
            this.tabs.setVisible(false);
            this.progressbar.setVisible(true);
        });

        task.setOnSucceeded(e -> {
            this.setTable(this.revision_table, this.revisionItems, this.revision_lbl);
            this.setTable(this.pending_table, this.pendingItems, this.pending_lbl);
            this.setTable(this.approved_table, this.approvedItems, this.approved_lbl);

            //Revision Tab - Any user from the department can see except the budget officer and manager
            if (revisionItems.size() > 0
                    && !this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_RECOMMENDATION)
                    && !this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)
                    && !this.currentUser.getDesignation().toLowerCase().equals(Utility.PO_APPROVAL)) {
                if (!this.tabs.getTabs().contains(revision_tab))
                    this.tabs.getTabs().add(revision_tab);
            }else {
                if (this.tabs.getTabs().contains(revision_tab))
                    this.tabs.getTabs().remove(revision_tab);
            }
            //Pending Tab - Only the users from the department including the manager can see (excluding budget officer and general manager)
            if (pendingItems.size() == 0) {
                if (this.tabs.getTabs().contains(pending_tab))
                    this.tabs.getTabs().remove(pending_tab);
            }else {
                if (!this.tabs.getTabs().contains(pending_tab))
                    this.tabs.getTabs().add(pending_tab);
            }
            //Approved Tab - All can see
            if (approvedItems.size() == 0) {
                if (this.tabs.getTabs().contains(approved_tab))
                    this.tabs.getTabs().remove(approved_tab);
            }else {
                if (!this.tabs.getTabs().contains(approved_tab))
                    this.tabs.getTabs().add(approved_tab);
            }

            if (this.tabs.getTabs().size() == 0)
                this.tabs.setVisible(false);
            else
                this.tabs.setVisible(true);

            progressbar.setVisible(false);
        });

        task.setOnFailed(wse -> {
            revisionItems = FXCollections.observableArrayList(new ArrayList<>());
            pendingItems = FXCollections.observableArrayList(new ArrayList<>());
            approvedItems = FXCollections.observableArrayList(new ArrayList<>());

            this.setTable(this.revision_table, this.revisionItems, this.revision_lbl);
            this.setTable(this.pending_table, this.pendingItems, this.pending_lbl);
            this.setTable(this.approved_table, this.approvedItems, this.approved_lbl);

            progressbar.setVisible(false);
        });

        new Thread(task).start();
    }
    /**
     * Sets the table and label values
     * @param table the tableview to set
     * @param po  the list of items
     * @param count the label to display the row count
     */
    public void setTable(TableView table, ObservableList<PurchaseOrder> po, Label count){
        table.setItems(po);
        count.setText(po.size()+"");
    }
    /**
     * Creates the table
     * @param table the tableview to create
     * @param noItem  the string to appear when no rows
     * @param viewMenu  the string to appear when right-clicking a table row
     * @param controller the controller to call when the menu item is clicked
     * @param remarks the controller to call when the menu item is clicked
     * @param type the type of date to display
     */
    public void createRVTable(TableView table, String noItem, String viewMenu, String controller, String remarks, String type){
        TableColumn<PurchaseOrder, String> column = new TableColumn<>("PO No.");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getPoNo()));
        column.setStyle("-fx-alignment: center-left;");
        column.setPrefWidth(110);
        column.setMaxWidth(110);
        column.setMinWidth(110);

        TableColumn<PurchaseOrder, String> column1 = new TableColumn<>("To");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getTo()));
        column1.setStyle("-fx-alignment: center-left;");
        column1.setPrefWidth(300);
        column1.setMaxWidth(300);
        column1.setMinWidth(300);

        TableColumn<PurchaseOrder, String> column2 = new TableColumn<>("Address");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getAddress()));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<PurchaseOrder, String> column3 = new TableColumn<>("No. of Items");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfItems()+""));
        column3.setStyle("-fx-alignment: center;");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);

        TableColumn<PurchaseOrder, String> column4 = new TableColumn<>("Total Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmount())));
        column4.setStyle("-fx-alignment: center-right;");
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);

        TableColumn<PurchaseOrder, String> column5 = new TableColumn<>(type);
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getPoDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);

        TableColumn<PurchaseOrder, String> column6 = new TableColumn<>("Remarks");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRemarks()));
        column6.setStyle("-fx-alignment: center;");
        column6.setPrefWidth(150);
        column6.setMaxWidth(150);
        column6.setMinWidth(150);

        table.setFixedCellSize(35);
        table.setPlaceholder(new Label(noItem));

        table.getColumns().add(column);
        table.getColumns().add(column1);
        table.getColumns().add(column2);
        table.getColumns().add(column3);
        table.getColumns().add(column4);
        table.getColumns().add(column5);
        if (remarks != null) table.getColumns().add(column6);

        table.setRowFactory(tv -> {
            TableRow<PurchaseOrder> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem view = new MenuItem(viewMenu);
            view.setOnAction(actionEvent -> {
                Utility.setSelectedObject(row.getItem());
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/"+controller+".fxml"));
            });

            rowMenu.getItems().addAll(view);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(rowMenu));
            return row;
        });
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
        this.date_pker.setItems(FXCollections.observableArrayList(data));
        this.date_pker.getSelectionModel().select(currYear+"");
    }
}
