package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class SearchCashieringConsumerController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label labelTo;

    @FXML
    private JFXTextField searchTf;

    @FXML
    private JFXToggleButton toggleSearch;

    @FXML
    private DatePicker searchDateFrom, searchDateTo;

    @FXML
    private TableView searchResultTable;
    //private ObservableList<CRMQueue> result = null;
    private Object resultInfo = null;
    private ObjectTransaction parentController = null;

    private List<TransactionDetails> transactionDetails;
    private TransactionHeader transactionHeader;

    private Teller teller;
    private int countAccount;
    private boolean taskIsRunning = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       // this.createTable();

        searchDateTo.setVisible(false);
        labelTo.setVisible(false);

        this.searchResultTable.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    processRequest(row.getItem());
                }
            });
            return row ;
        });

        this.parentController = Utility.getParentController();
        this.searchTf.requestFocus();
        this.toggleSearch.setSelected(Utility.TOGGLE_SEARCH);
        try {
            this.searchDateFrom.setValue(Utility.serverDate());
            this.searchDateTo.setValue(Utility.serverDate());
        } catch (Exception e) {
            this.searchDateFrom.setValue(LocalDate.now());
            this.searchDateTo.setValue(LocalDate.now());
            throw new RuntimeException(e);
        }

        if(this.toggleSearch.isSelected()) {
            this.toggleSearch.setText("Search Consumer");
            this.searchDateFrom.setVisible(false);
            this.searchDateTo.setVisible(false);
            this.labelTo.setVisible(false);
            this.searchTf.setPromptText("Reference Number/Last Name/First Name/Address");

            ObservableList<CRMQueue> result = null;
            try {
                result = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecordFromCRMList());
                this.searchResultTable.setItems(result);
                createTable(result);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("Consumer Record from CRM", e.getMessage(),
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                throw new RuntimeException(e);

            }
        }else {
            this.toggleSearch.setText("Search Teller");
            this.searchDateFrom.setVisible(true);
            this.searchDateTo.setVisible(true);
            this.labelTo.setVisible(true);
            this.searchTf.setPromptText("Username/Last Name/First Name");
        }

        searchDateFrom.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is not null before updating the second DatePicker
            if (newValue != null) {
                searchDateTo.setValue(newValue);
                search();
                searchTf.requestFocus();
            }
        });
    /*    searchDateFrom.getEditor().setOnKeyReleased(e ->{
            if (e.getCode() == KeyCode.ENTER) {
                searchDateTo.setValue(searchDateFrom.getValue());
                search();
                searchTf.requestFocus();
            }
        });

        searchDateTo.getEditor().setOnKeyReleased(e ->{
            if (e.getCode() == KeyCode.ENTER) {
                searchDateFrom.setValue(searchDateTo.getValue());
                search();
                searchTf.requestFocus();
            }
        });*/

        searchTf.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchResultTable.requestFocus();
                if(countTableItem() > 0){
                    Object selected = searchResultTable.getSelectionModel().getSelectedItem();
                    if(selected == null){
                        searchResultTable.getSelectionModel().selectFirst();
                    }else {
                        processRequest(selected);
                    }
                }
            }else {
                if(searchTf.getText().length() >= 3 || searchTf.getText().length() < 3){
                    search();
                }
            }
        });

        searchResultTable.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Object selected = searchResultTable.getSelectionModel().getSelectedItem();
                processRequest(selected);
            }
        });

        search();

    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    private void search(){
        String query = this.searchTf.getText();
        if(this.toggleSearch.isSelected()){
            try {
                ObservableList<CRMQueue> result = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecordFromCRMList(query));
                this.searchResultTable.setItems(result);
                createTable(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                ObservableList<EmployeeInfo> result = FXCollections.observableArrayList(EmployeeDAO.getEmployeeInfo(query));
                this.searchResultTable.setItems(result);
                createTable(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Initializes the consumers table
     * @return void
     */
    public void createTable(ObservableList result) {
        if (result.isEmpty())
            return;
        searchResultTable.getColumns().clear();
        if(result.get(0) instanceof CRMQueue){
            TableColumn<CRMQueue, String> consumerCol = new TableColumn<>("Consumer");
            consumerCol.setCellValueFactory(new PropertyValueFactory<>("consumerName"));
            consumerCol.setMinWidth(220);
            consumerCol.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> purposeCol = new TableColumn<>("Purpose");
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("transactionPurpose"));
            purposeCol.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));
            addressCol.setStyle("-fx-alignment: center-left;");

            this.searchResultTable.getColumns().add(consumerCol);
            this.searchResultTable.getColumns().add(purposeCol);
            this.searchResultTable.getColumns().add(addressCol);
        } else if (result.get(0) instanceof EmployeeInfo) {
            TableColumn<EmployeeInfo, String> tellerCol = new TableColumn<>("Teller");
            tellerCol.setCellValueFactory(new PropertyValueFactory<>("signatoryNameFormat"));
            tellerCol.setStyle("-fx-alignment: center-left;");
            this.searchResultTable.getColumns().add(tellerCol);
        }
        this.searchResultTable.setPlaceholder(new Label("No consumer records was searched."));
    }

    @FXML
    private  void toggleSearch(ActionEvent event) {
        searchResultTable.getColumns().clear();
        Utility.TOGGLE_SEARCH = toggleSearch.isSelected();
        if(this.toggleSearch.isSelected()) {
            this.toggleSearch.setText("Search Consumer");
            this.searchDateFrom.setVisible(false);
            this.searchDateTo.setVisible(false);
            this.labelTo.setVisible(false);
            this.searchTf.setPromptText("Reference Number/Last Name/First Name/Address");

        }else {
            this.toggleSearch.setText("Search Teller");
            this.searchDateFrom.setVisible(true);
            this.searchDateTo.setVisible(true);
            this.labelTo.setVisible(true);
            this.searchTf.setPromptText("Username/Last Name/First Name");
            searchResultTable.getItems().clear();
        }
        search();
    }

    private int countTableItem(){
        return searchResultTable.getItems().size();
    }

    private void processRequest(Object row){
       if(taskIsRunning)
           return;

        resultInfo = row;
        if(resultInfo instanceof EmployeeInfo){
            EmployeeInfo employeeInfo = (EmployeeInfo) resultInfo;
            if(Utility.checkPeriodIsLocked(searchDateFrom.getValue(), Utility.getStackPane())) return;
            try {
                JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        int monthFrom = searchDateFrom.getValue().getMonthValue();
                        int dayFrom = searchDateFrom.getValue().getDayOfMonth();
                        int yearFrom = searchDateFrom.getValue().getYear();

                        int monthTo = searchDateTo.getValue().getMonthValue();
                        int dayTo = searchDateTo.getValue().getDayOfMonth();
                        int yearTo = searchDateTo.getValue().getYear();

                        List<ORItemSummary> orItemSummaries =null;
                        LocalDate period = LocalDate.of(yearFrom, monthFrom, 1);
                        User tellerAccount = UserDAO.get(employeeInfo.getId());
                        transactionHeader = TransactionHeaderDAO.get(ActiveUser.getUser().getUserName(), searchDateFrom.getValue(), employeeInfo.getId());
                        if(transactionHeader==null) {
                            orItemSummaries = CashierDAO.getOrItems(yearFrom, monthFrom, dayFrom, yearTo, monthTo, dayTo, tellerAccount.getUserName());
                            countAccount = CashierDAO.countAccount(yearFrom, monthFrom, dayFrom, yearTo, monthTo, dayTo, tellerAccount.getUserName());
                        }else {
                            transactionDetails = TransactionDetailsDAO.get(searchDateFrom.getValue(), "OR", ActiveUser.getUser(), employeeInfo.getId());
                            orItemSummaries = new ArrayList<>();
                            for(TransactionDetails td : transactionDetails){
                                if(!td.getParticulars().equals("Grand Total")){
                                    ORItemSummary os = new ORItemSummary(td.getAccountCode(),td.getParticulars(),td.getAmount());
                                    orItemSummaries.add(os);
                                }
                            }
                        }
                        teller = new Teller(tellerAccount.getUserName(), employeeInfo.getSignatoryNameFormat(),employeeInfo.getEmployeeAddress(),employeeInfo.getPhone(), searchDateTo.getValue());
                        teller.setId(employeeInfo.getId());
                        teller.setOrItemSummaries(orItemSummaries);
                        return null;
                    }
                };

                task.setOnRunning(wse -> {
                    taskIsRunning = true;
                    dialog.show();
                });

                task.setOnSucceeded(wse -> {
                    dialog.close();
                    try {
                        resultInfo = teller;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("SearchResult", resultInfo);
                        result.put("TransactionHeader", transactionHeader);
                        result.put("SearchDate", searchDateFrom.getValue());
                        result.put("CountAccount", countAccount);
                        this.parentController.receive(result);
                        taskIsRunning = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                task.setOnFailed(wse -> {

                    Throwable throwable = task.getException();
                    throwable.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! \nError found: "+ throwable.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    dialog.close();
                });

                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //return costumer info from CRMQueue
            this.parentController.receive(resultInfo);
        }
    }
}
