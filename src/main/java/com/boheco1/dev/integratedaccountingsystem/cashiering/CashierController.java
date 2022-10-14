package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilderForWareHouse;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;
import com.boheco1.dev.integratedaccountingsystem.tellering.PowerBillsPaymentController;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;


import java.net.URL;
import java.util.ResourceBundle;

public class CashierController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXButton print_btn;

    @FXML
    private Label consumer, address, payable;

    @FXML
    private TableView paymentTable;


    private ConsumerInfo consumerInfo = null;


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
        ModalBuilderForWareHouse.showModalFromXMLNoClose(CashierController.class, "../tellering/tellering_search_consumer.fxml", Utility.getStackPane());
    }

    @Override
    public void receive(Object o) {
        if (o instanceof ConsumerInfo) {
            this.consumerInfo = (ConsumerInfo) o;
            this.consumer.setText(consumerInfo.getConsumerName());
            this.address.setText(consumerInfo.getConsumerAddress());
        }
    }

}