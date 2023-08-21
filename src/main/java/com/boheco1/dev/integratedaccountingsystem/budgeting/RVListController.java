package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.RVDAO;
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

public class RVListController extends MenuControllerHandler implements Initializable {

    @FXML
    private Label app_status_lbl;

    @FXML
    private ComboBox date_pker;

    @FXML
    private JFXButton view_btn;

    @FXML
    private TableView<RV> revision_table;

    @FXML
    private TableView<RV> pending_table;

    @FXML
    private TableView<RV> approval_table;

    @FXML
    private TableView<RV> approved_table;

    @FXML
    private TableView<RV> certified_table;

    @FXML
    private Label revision_lbl;

    @FXML
    private Label pending_lbl;

    @FXML
    private Label approval_lbl;

    @FXML
    private Label approved_lbl;

    @FXML
    private Label certified_lbl;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private TabPane tabs;

    @FXML
    private Tab pending_tab, reviewed_tab, approved_tab, budgeted_tab, revision_tab;

    private ObservableList<RV> revisionItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<RV> pendingItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<RV> approvalItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<RV> certifiedItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<RV> approvedItems = FXCollections.observableArrayList(new ArrayList<>());
    private EmployeeInfo currentUser = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.currentUser = ActiveUser.getUser().getEmployeeInfo();
        }catch (Exception e) {
            e.printStackTrace();
        }

        this.setYears();
        this.createRVTable(revision_table, "No RVs pending for revision.", "View RV", "budgeting_edit_rv", "Remarks", "Date Prepared");
        this.createRVTable(pending_table, "No RVs pending recommendation by the dept. manager.", "View RV", "budgeting_rv_approval", null, "Date Prepared");
        this.createRVTable(approval_table, "No RVs pending certification by the budget officer.", "View RV", "budgeting_rv_approval", null, "Date Recommended");
        this.createRVTable(certified_table, "No RVs pending approval by the general manager.", "View RV", "budgeting_rv_approval", null, "Date Certified");
        this.createRVTable(approved_table, "No RVs approved by the general manager.", "View RV", "budgeting_rv_approval", null, "Date Approved");
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
                        //if user is GM, all approved RVs and RVs certified by Budget officer
                        if (currentUser.getDesignation().toLowerCase().equals(Utility.RV_APPROVAL)) {
                            certifiedItems = FXCollections.observableArrayList(RVDAO.getAll(year, RV.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(RVDAO.getAll(year, RV.APPROVED));
                        //if user is budget officer, all recommended RVs, certified RVs and approved RVs
                        }else if (currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)){
                            approvalItems = FXCollections.observableArrayList(RVDAO.getAll(year, RV.PENDING_CERTIFICATION));
                            certifiedItems = FXCollections.observableArrayList(RVDAO.getAll(year, RV.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(RVDAO.getAll(year, COB.APPROVED));
                        //if user is department manager, department RVs pending recommendation, department RVs pending certification, approved department RVs
                        }else if (currentUser.getDesignation().toLowerCase().contains(Utility.COB_REVIEWER)){
                            pendingItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_RECOMMENDATION));
                            approvalItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_CERTIFICATION));
                            certifiedItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.APPROVED));
                        //if preparer in a department
                        }else{
                            revisionItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_REVISION));
                            pendingItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_RECOMMENDATION));
                            approvalItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_CERTIFICATION));
                            certifiedItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(RVDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), RV.APPROVED));
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
            this.setTable(this.approval_table, this.approvalItems, this.approval_lbl);
            this.setTable(this.certified_table, this.certifiedItems, this.certified_lbl);
            this.setTable(this.approved_table, this.approvedItems, this.approved_lbl);

            //Revision Tab - Any user from the department can see except the budget officer and manager
            if (revisionItems.size() > 0
                    && !this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_RECOMMENDATION)
                    && !this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)
                    && !this.currentUser.getDesignation().toLowerCase().equals(Utility.RV_APPROVAL)) {
                if (!this.tabs.getTabs().contains(revision_tab))
                    this.tabs.getTabs().add(revision_tab);
            }else {
                if (this.tabs.getTabs().contains(revision_tab))
                    this.tabs.getTabs().remove(revision_tab);
            }
            //Pending Tab - Only the users from the department including the manager can see (excluding budget officer and general manager)
            if (pendingItems.size() == 0
                    || this.currentUser.getDesignation().toLowerCase().contains(Utility.RV_CERTIFICATION)
                    || this.currentUser.getDesignation().toLowerCase().equals(Utility.RV_APPROVAL)) {
                if (this.tabs.getTabs().contains(pending_tab))
                    this.tabs.getTabs().remove(pending_tab);
            }else {
                if (!this.tabs.getTabs().contains(pending_tab))
                    this.tabs.getTabs().add(pending_tab);
            }
            //Reviewed Tab - All can see (department, manager, budget officer) except the general manager
            if (approvalItems.size() == 0 || this.currentUser.getDesignation().toLowerCase().equals(Utility.RV_APPROVAL)) {
                if (this.tabs.getTabs().contains(reviewed_tab))
                    this.tabs.getTabs().remove(reviewed_tab);
            }else {
                if (!this.tabs.getTabs().contains(reviewed_tab))
                    this.tabs.getTabs().add(reviewed_tab);
            }
            //Certified Tab - All can see
            if (certifiedItems.size() == 0) {
                if (this.tabs.getTabs().contains(budgeted_tab))
                    this.tabs.getTabs().remove(budgeted_tab);
            }else {
                if (!this.tabs.getTabs().contains(budgeted_tab))
                    this.tabs.getTabs().add(budgeted_tab);
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
            approvalItems = FXCollections.observableArrayList(new ArrayList<>());
            certifiedItems = FXCollections.observableArrayList(new ArrayList<>());
            approvedItems = FXCollections.observableArrayList(new ArrayList<>());

            this.setTable(this.revision_table, this.revisionItems, this.revision_lbl);
            this.setTable(this.pending_table, this.pendingItems, this.pending_lbl);
            this.setTable(this.approval_table, this.approvalItems, this.approval_lbl);
            this.setTable(this.certified_table, this.certifiedItems, this.certified_lbl);
            this.setTable(this.approved_table, this.approvedItems, this.approved_lbl);

            progressbar.setVisible(false);
        });

        new Thread(task).start();
    }
    /**
     * Sets the table and label values
     * @param table the tableview to set
     * @param rv  the list of items
     * @param count the label to display the row count
     */
    public void setTable(TableView table, ObservableList<RV> rv, Label count){
        table.setItems(rv);
        count.setText(rv.size()+"");
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
        TableColumn<RV, String> column = new TableColumn<>("RV No.");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRvNo()));
        column.setStyle("-fx-alignment: center-left;");
        column.setPrefWidth(110);
        column.setMaxWidth(110);
        column.setMinWidth(110);

        TableColumn<RV, String> column1 = new TableColumn<>("To");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getTo()));
        column1.setStyle("-fx-alignment: center-left;");
        column1.setPrefWidth(200);
        column1.setMaxWidth(200);
        column1.setMinWidth(200);

        TableColumn<RV, String> column2 = new TableColumn<>("Purpose");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getPurpose()));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<RV, String> column3 = new TableColumn<>("No. of Items");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfItems()+""));
        column3.setStyle("-fx-alignment: center;");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);

        TableColumn<RV, String> column4 = new TableColumn<>("Total Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmount())));
        column4.setStyle("-fx-alignment: center-right;");
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);

        TableColumn<RV, String> column5 = new TableColumn<>(type);
        if (type.equals("Date Prepared")) {
            column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRvDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        }else if (type.equals("Date Recommended")) {
            column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDateRecommended().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        }else if (type.equals("Date Certified")) {
            column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDateBudgeted().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        }else{
            column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDateApproved().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        }
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);

        TableColumn<RV, String> column6 = new TableColumn<>("Remarks");
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
            TableRow<RV> row = new TableRow<>();
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
