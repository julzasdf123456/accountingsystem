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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class SearchCashieringConsumerController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField searchTf;

    @FXML
    private JFXToggleButton toggleSearch;

    @FXML
    private DatePicker searchDate;

    @FXML
    private TableView searchResultTable;
    //private ObservableList<CRMQueue> result = null;
    private Object resultInfo = null;
    private ObjectTransaction parentController = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       // this.createTable();


        this.searchResultTable.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    resultInfo = row.getItem();
                    if(resultInfo instanceof EmployeeInfo){
                        EmployeeInfo employeeInfo = (EmployeeInfo) resultInfo;
                        try {
                            JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
                            Task<Void> task = new Task<>() {
                                @Override
                                protected Void call() throws Exception {
                                    int month = searchDate.getValue().getMonthValue();
                                    int day = searchDate.getValue().getDayOfMonth();
                                    int year = searchDate.getValue().getYear();
                                    String username = UserDAO.get(employeeInfo.getId()).getUserName();
                                    HashMap<String, List<ItemSummary>> DCRBreakDown = BillDAO.getDCRBreakDown(year,month,day, username);
                                    Teller teller = new Teller(username, employeeInfo.getSignatoryNameFormat(),employeeInfo.getEmployeeAddress(),employeeInfo.getPhone(), searchDate.getValue(), DCRBreakDown);
                                    resultInfo = teller;
                                    return null;
                                }
                            };

                            task.setOnRunning(wse -> {
                                dialog.show();
                            });

                            task.setOnSucceeded(wse -> {
                                dialog.close();
                                try {
                                    TransactionHeader transactionHeader = TransactionHeaderDAO.get(UserDAO.get(employeeInfo.getId()).getUserName(), searchDate.getValue());
                                    if(transactionHeader == null){
                                        AlertDialogBuilder.messgeDialog("System Message", "Task successful." ,
                                                Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                                    }else {
                                        AlertDialogBuilder.messgeDialog("System Message", "OR was already issued on this transaction." ,
                                                Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);

                                    }
                                    HashMap<String, Object> result = new HashMap<>();
                                    result.put("SearchResult", resultInfo);
                                    result.put("TransactionHeader", transactionHeader);
                                    result.put("SearchDate", searchDate.getValue());
                                    this.parentController.receive(result);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            task.setOnFailed(wse -> {
                                dialog.close();
                                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
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
            });
            return row ;
        });

        this.parentController = Utility.getParentController();
        this.searchTf.requestFocus();
        this.toggleSearch.setSelected(Utility.TOGGLE_SEARCH);
        try {
            this.searchDate.setValue(Utility.serverDate());
        } catch (Exception e) {
            this.searchDate.setValue(LocalDate.now());
            throw new RuntimeException(e);
        }

        if(this.toggleSearch.isSelected()) {
            this.toggleSearch.setText("Search Consumer");
            this.searchDate.setVisible(false);
            this.searchTf.setPromptText("Reference Number/Last Name/First Name/Address");
        }else {
            this.toggleSearch.setText("Search Teller");
            this.searchDate.setVisible(true);
            this.searchTf.setPromptText("Username/Last Name/First Name");
        }

        searchDate.getEditor().setOnKeyReleased(e ->{
            if (e.getCode() == KeyCode.ENTER)
                search();
        });

    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void search(){
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
            consumerCol.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));
            addressCol.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> purposeCol = new TableColumn<>("Purpose");
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("transactionPurpose"));
            purposeCol.setStyle("-fx-alignment: center-left;");

            this.searchResultTable.getColumns().add(consumerCol);
            this.searchResultTable.getColumns().add(addressCol);
            this.searchResultTable.getColumns().add(purposeCol);
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
            this.searchDate.setVisible(false);
            this.searchTf.setPromptText("Reference Number/Last Name/First Name/Address");
            search();
        }else {
            this.toggleSearch.setText("Search Teller");
            this.searchDate.setVisible(true);
            this.searchTf.setPromptText("Username/Last Name/First Name");
            searchResultTable.getItems().clear();
        }

    }
}
