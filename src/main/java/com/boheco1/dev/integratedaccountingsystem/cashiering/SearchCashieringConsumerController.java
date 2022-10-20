package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.CRMQueue;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchCashieringConsumerController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private JFXToggleButton toggleSearch;

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
        this.query_tf.requestFocus();
        this.toggleSearch.setSelected(Utility.TOGGLE_SEARCH);

        if(this.toggleSearch.isSelected())
            this.toggleSearch.setText("Search Consumer");
        else
            this.toggleSearch.setText("Search Teller");
    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void search(){
        String query = this.query_tf.getText();
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
            TableColumn<CRMQueue, String> column1 = new TableColumn<>("Consumer");
            column1.setMinWidth(200);
            column1.setMaxWidth(200);
            column1.setPrefWidth(200);
            column1.setCellValueFactory(new PropertyValueFactory<>("consumerName"));
            column1.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> column2 = new TableColumn<>("Address");
            column2.setMinWidth(200);
            column2.setMaxWidth(200);
            column2.setPrefWidth(200);
            column2.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));
            column2.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMQueue, String> column3 = new TableColumn<>("Purpose");
            column3.setCellValueFactory(new PropertyValueFactory<>("transactionPurpose"));
            column3.setStyle("-fx-alignment: center-left;");

            this.searchResultTable.getColumns().add(column1);
            this.searchResultTable.getColumns().add(column2);
            this.searchResultTable.getColumns().add(column3);
        } else if (result.get(0) instanceof EmployeeInfo) {
            TableColumn<EmployeeInfo, String> column1 = new TableColumn<>("Teller");
            column1.setCellValueFactory(new PropertyValueFactory<>("signatoryNameFormat"));
            column1.setStyle("-fx-alignment: center-left;");
            this.searchResultTable.getColumns().add(column1);
        }
        this.searchResultTable.setPlaceholder(new Label("No consumer records was searched."));
    }

    @FXML
    private  void toggleSearch(ActionEvent event) {
        searchResultTable.getColumns().clear();
        Utility.TOGGLE_SEARCH = toggleSearch.isSelected();
        if(toggleSearch.isSelected())
            toggleSearch.setText("Search Consumer");
        else
            toggleSearch.setText("Search Teller");

    }
}
