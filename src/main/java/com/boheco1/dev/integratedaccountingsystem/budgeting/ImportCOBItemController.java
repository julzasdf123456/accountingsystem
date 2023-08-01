package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ImportCOBItemController extends MenuControllerHandler implements Initializable {
    @FXML
    private TableView<COB> cob_table;

    @FXML
    private JFXButton import_btn, view_btn;

    @FXML
    private JFXComboBox<String> type_cb;

    private ObservableList<COB> approvedItems = FXCollections.observableArrayList(new ArrayList<>());

    private ObjectTransaction parentController = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.import_btn.setDisable(true);
        ObservableList<String> types = FXCollections.observableArrayList(COBItem.TYPES);
        this.type_cb.setItems(types);
        this.createApprovedTable();

        this.type_cb.getSelectionModel().selectedItemProperty().addListener((observableValue, o, n) -> {
            this.setInformation(n);
        });

        this.view_btn.setOnAction(evt -> {
            if (this.type_cb.getSelectionModel().getSelectedItem() != null)
                this.setInformation(this.type_cb.getSelectionModel().getSelectedItem());
        });

        this.import_btn.setOnAction(evt -> {
            COB selected = this.cob_table.getSelectionModel().getSelectedItem();
            try {
                List<COBItem> items = CobItemDAO.getItems(selected);
                this.parentController = Utility.getParentController();
                this.parentController.receive(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Displays the information of the COBs
     */
    public void setInformation(String type){
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                approvedItems = FXCollections.observableArrayList(CobDAO.getAll(ActiveUser.getUser().getEmployeeInfo().getDepartment(), COB.APPROVED, type));
                return null;
            }
        };

        task.setOnRunning(wse -> {
            this.view_btn.setDisable(true);
            this.type_cb.setDisable(true);
        });

        task.setOnSucceeded(e -> {
            this.setTable(this.cob_table, this.approvedItems);
            this.view_btn.setDisable(false);
            this.type_cb.setDisable(false);
        });

        task.setOnFailed(wse -> {
            approvedItems = FXCollections.observableArrayList(new ArrayList<>());
            this.setTable(this.cob_table, this.approvedItems);
            this.view_btn.setDisable(false);
            this.type_cb.setDisable(false);
        });

        new Thread(task).start();
    }
    /**
     * Sets the table and label values
     * @param table the tableview to set
     * @param cobs  the list of items
     */
    public void setTable(TableView table, ObservableList<COB> cobs){
        table.setItems(cobs);
    }
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
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);

        this.cob_table.setFixedCellSize(35);
        this.cob_table.setPlaceholder(new Label("No approved COBs belonging to the selected category."));

        this.cob_table.getColumns().add(column);
        this.cob_table.getColumns().add(column1);
        this.cob_table.getColumns().add(column3);
        this.cob_table.getColumns().add(column4);
        this.cob_table.getColumns().add(column5);

        this.cob_table.setRowFactory(tv -> {
            TableRow<COB> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem view = new MenuItem("Set as Import Data");
            view.setOnAction(actionEvent -> {
                this.import_btn.setDisable(false);
            });

            rowMenu.getItems().addAll(view);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(rowMenu));
            return row;
        });
    }
}
