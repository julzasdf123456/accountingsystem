package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.APP;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
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
    private TableView<COB> revision_table;

    @FXML
    private TableView<COB> pending_table;

    @FXML
    private TableView<COB> approval_table;

    @FXML
    private TableView<COB> approved_table;

    @FXML
    private Label revision_lbl;

    @FXML
    private Label pending_lbl;

    @FXML
    private Label approval_lbl;

    @FXML
    private Label approved_lbl;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private TabPane tabs;

    @FXML
    private Tab pending_tab, reviewed_tab, approved_tab, revision_tab;

    private ObservableList<COB> revisionItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<COB> pendingItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<COB> approvalItems = FXCollections.observableArrayList(new ArrayList<>());
    private ObservableList<COB> approvedItems = FXCollections.observableArrayList(new ArrayList<>());

    private APP current;
    private EmployeeInfo currentUser = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.current = null;
        try {
            this.current = AppDAO.getOpen(true);
            this.currentUser = ActiveUser.getUser().getEmployeeInfo();
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (current != null) {
            app_status_lbl.setText("Budget preparation for CY " + current.getYear() + " (" + current.getBoardRes() + ") is currently opened! You can prepare the budget now!");
        } else {
            app_status_lbl.setText("");
        }

        this.setYears();
        this.createRevisionTable();
        this.createPendingTable();
        this.createApprovalTable();
        this.createApprovedTable();
        this.setInformation();

        this.view_btn.setOnAction(evt -> {
            this.setInformation();
        });
    }
    /**
     * Displays the information of the COBs
     */
    public void setInformation(){
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                String year = date_pker.getSelectionModel().getSelectedItem().toString();
                if (year != null) {
                    try{
                        revisionItems = FXCollections.observableArrayList(CobDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), COB.PENDING_REVISION));
                        pendingItems = FXCollections.observableArrayList(CobDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), COB.PENDING_REVIEW));

                        //Show all pending approval and approved COBs from all departments if user is budget officer
                        if (currentUser != null && currentUser.getDesignation().toLowerCase().contains(Utility.COB_APPROVAL)){
                            approvalItems = FXCollections.observableArrayList(CobDAO.getAll(year, COB.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(CobDAO.getAll(year, COB.APPROVED));
                        }else{
                            //Show only departmental pending review and approved COBs to users who have same department
                            approvalItems = FXCollections.observableArrayList(CobDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), COB.PENDING_APPROVAL));
                            approvedItems = FXCollections.observableArrayList(CobDAO.getAll(year, ActiveUser.getUser().getEmployeeInfo().getDepartment(), COB.APPROVED));
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
            this.setTable(this.approved_table, this.approvedItems, this.approved_lbl);

            //Revision Tab - Any user from the department can see except the budget officer and manager
            if (revisionItems.size() > 0
                    && !this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_REVIEWER)
                    && !this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_APPROVAL)) {
                if (!this.tabs.getTabs().contains(revision_tab))
                    this.tabs.getTabs().add(revision_tab);
            }else {
                if (this.tabs.getTabs().contains(revision_tab))
                    this.tabs.getTabs().remove(revision_tab);
            }
            //Pending Tab - Only the users from the department including the manager can see
            if (pendingItems.size() == 0 || this.currentUser.getDesignation().toLowerCase().contains(Utility.COB_APPROVAL)) {
                if (this.tabs.getTabs().contains(pending_tab))
                    this.tabs.getTabs().remove(pending_tab);
            }else {
                if (!this.tabs.getTabs().contains(pending_tab))
                    this.tabs.getTabs().add(pending_tab);
            }
            //Reviewed Tab - All can see (department, manager, budget officer)
            if (approvalItems.size() == 0) {
                if (this.tabs.getTabs().contains(reviewed_tab))
                    this.tabs.getTabs().remove(reviewed_tab);
            }else {
                if (!this.tabs.getTabs().contains(reviewed_tab))
                    this.tabs.getTabs().add(reviewed_tab);
            }

            //Approved Tab - All can see (department, manager, budget officer)
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
            approvedItems = FXCollections.observableArrayList(new ArrayList<>());

            this.setTable(this.revision_table, this.revisionItems, this.revision_lbl);
            this.setTable(this.pending_table, this.pendingItems, this.pending_lbl);
            this.setTable(this.approval_table, this.approvalItems, this.approval_lbl);
            this.setTable(this.approved_table, this.approvedItems, this.approved_lbl);

            progressbar.setVisible(false);
        });

        new Thread(task).start();
    }
    /**
     * Sets the table and label values
     * @param table the tableview to set
     * @param cobs  the list of items
     * @param count the label to display the row count
     */
    public void setTable(TableView table, ObservableList<COB> cobs, Label count){
        table.setItems(cobs);
        count.setText(cobs.size()+"");
    }
    /**
     * Initializes the pending table
     * @return void
     */
    public void createRevisionTable(){
        TableColumn<COB, String> column = new TableColumn<>("Control No.");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCobId()));
        column.setStyle("-fx-alignment: center-left;");
        column.setPrefWidth(100);
        column.setMaxWidth(100);
        column.setMinWidth(100);

        TableColumn<COB, String> column1 = new TableColumn<>("Major Activity");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getActivity()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<COB, String> column2 = new TableColumn<>("Budget Type");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getType()));
        column2.setStyle("-fx-alignment: center-left;");
        column2.setPrefWidth(150);
        column2.setMaxWidth(150);
        column2.setMinWidth(150);

        TableColumn<COB, String> column3 = new TableColumn<>("No. of Items");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfItems()+""));
        column3.setStyle("-fx-alignment: center;");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);

        TableColumn<COB, String> column4 = new TableColumn<>("Total Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmount())));
        column4.setStyle("-fx-alignment: center-right;");
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);

        TableColumn<COB, String> column5 = new TableColumn<>("Date Prepared");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDatePrepared().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);

        TableColumn<COB, String> column6 = new TableColumn<>("Remarks");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getRemarks()));
        column6.setStyle("-fx-alignment: center;");
        column6.setPrefWidth(150);
        column6.setMaxWidth(150);
        column6.setMinWidth(150);

        this.revision_table.setFixedCellSize(35);
        this.revision_table.setPlaceholder(new Label("No Revision COBs."));

        this.revision_table.getColumns().add(column);
        this.revision_table.getColumns().add(column1);
        this.revision_table.getColumns().add(column2);
        this.revision_table.getColumns().add(column3);
        this.revision_table.getColumns().add(column4);
        this.revision_table.getColumns().add(column5);
        this.revision_table.getColumns().add(column6);

        this.revision_table.setRowFactory(tv -> {
            TableRow<COB> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem view = new MenuItem("Edit Budget");
            view.setOnAction(actionEvent -> {
                Utility.setSelectedObject(row.getItem());
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_edit_cob.fxml"));
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
     * Initializes the pending table
     * @return void
     */
    public void createPendingTable() {
        TableColumn<COB, String> column = new TableColumn<>("Control No.");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCobId()));
        column.setStyle("-fx-alignment: center-left;");
        column.setPrefWidth(100);
        column.setMaxWidth(100);
        column.setMinWidth(100);

        TableColumn<COB, String> column1 = new TableColumn<>("Major Activity");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getActivity()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<COB, String> column2 = new TableColumn<>("Budget Type");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getType()));
        column2.setStyle("-fx-alignment: center-left;");
        column2.setPrefWidth(150);
        column2.setMaxWidth(150);
        column2.setMinWidth(150);

        TableColumn<COB, String> column3 = new TableColumn<>("No. of Items");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfItems() + ""));
        column3.setStyle("-fx-alignment: center;");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);

        TableColumn<COB, String> column4 = new TableColumn<>("Total Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmount())));
        column4.setStyle("-fx-alignment: center-right;");
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);

        TableColumn<COB, String> column5 = new TableColumn<>("Date Prepared");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDatePrepared().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);

        TableColumn<COB, String> column6 = new TableColumn<>("Status");
        column6.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getStatus()));
        column6.setStyle("-fx-alignment: center;");
        column6.setPrefWidth(150);
        column6.setMaxWidth(150);
        column6.setMinWidth(150);

        this.pending_table.setFixedCellSize(35);
        this.pending_table.setPlaceholder(new Label("No Pending COBs."));

        this.pending_table.getColumns().add(column);
        this.pending_table.getColumns().add(column1);
        this.pending_table.getColumns().add(column2);
        this.pending_table.getColumns().add(column3);
        this.pending_table.getColumns().add(column4);
        this.pending_table.getColumns().add(column5);
        this.pending_table.getColumns().add(column6);

        this.pending_table.setRowFactory(tv -> {
            TableRow<COB> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem view = new MenuItem("View Budget");
            view.setOnAction(actionEvent -> {
                Utility.setSelectedObject(row.getItem());
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_cob_approval.fxml"));
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
     * Initializes the approved table
     * @return void
     */
    public void createApprovalTable(){
        TableColumn<COB, String> column = new TableColumn<>("Control No.");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCobId()));
        column.setStyle("-fx-alignment: center-left;");
        column.setPrefWidth(100);
        column.setMaxWidth(100);
        column.setMinWidth(100);

        TableColumn<COB, String> column1 = new TableColumn<>("Major Activity");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getActivity()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<COB, String> column2 = new TableColumn<>("Budget Type");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getType()));
        column2.setStyle("-fx-alignment: center-left;");
        column2.setPrefWidth(150);
        column2.setMaxWidth(150);
        column2.setMinWidth(150);

        TableColumn<COB, String> column3 = new TableColumn<>("No. of Items");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfItems()+""));
        column3.setStyle("-fx-alignment: center;");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);

        TableColumn<COB, String> column4 = new TableColumn<>("Total Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmount())));
        column4.setStyle("-fx-alignment: center-right;");
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);

        TableColumn<COB, String> column5 = new TableColumn<>("Date Reviewed");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDateReviewed().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);

        this.approval_table.setFixedCellSize(35);
        this.approval_table.setPlaceholder(new Label("No Reviewed COBs."));

        this.approval_table.getColumns().add(column);
        this.approval_table.getColumns().add(column1);
        this.approval_table.getColumns().add(column2);
        this.approval_table.getColumns().add(column3);
        this.approval_table.getColumns().add(column4);
        this.approval_table.getColumns().add(column5);

        this.approval_table.setRowFactory(tv -> {
            TableRow<COB> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem view = new MenuItem("View Budget");
            view.setOnAction(actionEvent -> {
                Utility.setSelectedObject(row.getItem());
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_cob_approval.fxml"));
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
     * Initializes the approved table
     * @return void
     */
    public void createApprovedTable(){
        TableColumn<COB, String> column = new TableColumn<>("Control No.");
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getCobId()));
        column.setStyle("-fx-alignment: center-left;");
        column.setPrefWidth(100);
        column.setMaxWidth(100);
        column.setMinWidth(100);

        TableColumn<COB, String> column1 = new TableColumn<>("Major Activity");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getActivity()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<COB, String> column2 = new TableColumn<>("Budget Type");
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getType()));
        column2.setStyle("-fx-alignment: center-left;");
        column2.setPrefWidth(150);
        column2.setMaxWidth(150);
        column2.setMinWidth(150);

        TableColumn<COB, String> column3 = new TableColumn<>("No. of Items");
        column3.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNoOfItems()+""));
        column3.setStyle("-fx-alignment: center;");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);

        TableColumn<COB, String> column4 = new TableColumn<>("Total Amount");
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmount())));
        column4.setStyle("-fx-alignment: center-right;");
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setMinWidth(150);

        TableColumn<COB, String> column5 = new TableColumn<>("Date Approved");
        column5.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDateApproved().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(150);
        column5.setMaxWidth(150);
        column5.setMinWidth(150);

        this.approved_table.setFixedCellSize(35);
        this.approved_table.setPlaceholder(new Label("No Approved COBs."));

        this.approved_table.getColumns().add(column);
        this.approved_table.getColumns().add(column1);
        this.approved_table.getColumns().add(column2);
        this.approved_table.getColumns().add(column3);
        this.approved_table.getColumns().add(column4);
        this.approved_table.getColumns().add(column5);

        this.approved_table.setRowFactory(tv -> {
            TableRow<COB> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem view = new MenuItem("View Budget");
            view.setOnAction(actionEvent -> {
                Utility.setSelectedObject(row.getItem());
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_cob_approval.fxml"));
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
        this.date_pker.getSelectionModel().select((currYear+1)+"");
    }
}
