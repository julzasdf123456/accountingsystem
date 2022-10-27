package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.CRMQueue;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
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
                    this.parentController.receive(resultInfo);
                }
            });
            return row ;
        });
        this.parentController = Utility.getParentController();
        this.searchTf.requestFocus();
        this.toggleSearch.setSelected(Utility.TOGGLE_SEARCH);
        this.searchDate.setValue(LocalDate.now());
        if(this.toggleSearch.isSelected()) {
            this.toggleSearch.setText("Search Consumer");
            this.searchDate.setVisible(false);
            this.searchTf.setPromptText("Reference Number/Last Name/First Name/Address");
        }else {
            this.toggleSearch.setText("Search Teller");
            this.searchDate.setVisible(true);
            this.searchTf.setPromptText("Username/Last Name/First Name");
        }
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
                ObservableList<CRMQueue> result = FXCollections.observableArrayList(ConsumerDAO.getConsumerRecordFromCRM(query));
                this.searchResultTable.setItems(result);
                createTable(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                List<EmployeeInfo> list = EmployeeDAO.getEmployeeInfo(query);

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
            consumerCol.setMinWidth(150);
            consumerCol.setMaxWidth(150);
            consumerCol.setPrefWidth(150);
            consumerCol.setCellValueFactory(new PropertyValueFactory<>("consumerName"));
            consumerCol.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> addressCol = new TableColumn<>("Address");
            addressCol.setMinWidth(200);
            addressCol.setMaxWidth(200);
            addressCol.setPrefWidth(200);
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
        }else {
            this.toggleSearch.setText("Search Teller");
            this.searchDate.setVisible(true);
            this.searchTf.setPromptText("Username/Last Name/First Name");
        }

    }
}
