package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.dao.CRMDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.tellering.PowerBillsPaymentController;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


import java.net.URL;
import java.util.ResourceBundle;

public class CashierController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXButton print_btn;

    @FXML
    private Label consumer, address, purpose, payable;

    @FXML
    private TableView paymentTable;


    private CRMQueue consumerInfo = null;
    private EmployeeInfo employeeInfo = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setParentController(this);
    }

    @FXML
    private void print(ActionEvent event) {
        System.out.print("Print");
    }

    @FXML
    private void search(ActionEvent event) {
        ModalBuilderForWareHouse.showModalFromXMLNoClose(CashierController.class, "../cashiering/cashiering_search_consumer.fxml", Utility.getStackPane());
    }

    @Override
    public void receive(Object o) {
        this.paymentTable.getColumns().clear();
        payable.setText("Total: 0");
        if (o instanceof CRMQueue) {
            this.consumerInfo = (CRMQueue) o;
            this.consumer.setText(consumerInfo.getConsumerName());
            this.address.setText(consumerInfo.getConsumerAddress());
            this.purpose.setText(consumerInfo.getTransactionPurpose());
            setUpPaymentTable(consumerInfo);
        }else if (o instanceof EmployeeInfo) {
            this.employeeInfo = (EmployeeInfo) o;
            this.consumer.setText(employeeInfo.getSignatoryNameFormat());
            this.address.setText(employeeInfo.getEmployeeAddress());
            this.purpose.setText(employeeInfo.getDesignation());
        }
    }

    private void setUpPaymentTable(Object o) {
        if(o instanceof CRMQueue){
            CRMQueue crmQueue = (CRMQueue) o;
            TableColumn<CRMDetails, String> column1 = new TableColumn<>("Reference No");
            column1.setMinWidth(150);
            column1.setMaxWidth(150);
            column1.setPrefWidth(150);
            column1.setCellValueFactory(new PropertyValueFactory<>("referenceNo"));
            column1.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMDetails, String> column2 = new TableColumn<>("Particulars");
            column2.setCellValueFactory(new PropertyValueFactory<>("particulars"));
            column2.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMDetails, String> column3 = new TableColumn<>("Amount");
            column3.setMinWidth(100);
            column3.setMaxWidth(100);
            column3.setPrefWidth(100);
            //column3.setCellValueFactory(new PropertyValueFactory<>("total"));
            Callback<TableColumn<CRMDetails, String>, TableCell<CRMDetails, String>> qtycellFactory
                    = //
                    new Callback<TableColumn<CRMDetails, String>, TableCell<CRMDetails, String>>() {
                        @Override
                        public TableCell call(final TableColumn<CRMDetails, String> param) {
                            final TableCell<CRMDetails, String> cell = new TableCell<CRMDetails, String>() {
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        CRMDetails crmDetails = getTableView().getItems().get(getIndex());
                                        setGraphic(null);
                                        setText(Utility.formatDecimal(crmDetails.getTotal()));
                                    }
                                }
                            };
                            return cell;
                        }
                    };

            column3.setCellFactory(qtycellFactory);
            column3.setStyle("-fx-alignment: center-right;");

            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
            this.paymentTable.getColumns().add(column3);

            ObservableList<CRMDetails> result = null;

            try {
                result = FXCollections.observableArrayList(CRMDAO.getConsumerTransaction(crmQueue.getId()));
                this.paymentTable.setItems(result);
                double total = 0;
                for (CRMDetails c : result){
                    total+=c.getTotal();
                }
                payable.setText("Total: "+String.format("%,.2f",total));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{

        }

        this.paymentTable.setPlaceholder(new Label("No consumer records was searched."));
    }

}