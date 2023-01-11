package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.Check;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentAddCheckController extends MenuControllerHandler implements Initializable {


    @FXML
    private AnchorPane contentPane;

    @FXML
    private TextField bank_tf;

    @FXML
    private TextField check_no_tf;

    @FXML
    private TextField amount_tf;

    @FXML
    private JFXButton add_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputHelper.restrictNumbersOnly(amount_tf);
    }

    public JFXButton getAdd_btn() {
        return add_btn;
    }

    public TextField getAmount_tf(){return amount_tf;}

    public TextField getBank_tf(){return bank_tf;}

    public Check getCheck(){
        Check check = null;
        String bank = this.bank_tf.getText();
        String checkNo = this.check_no_tf.getText();
        double amount = 0;
        try{
            amount = Double.parseDouble(this.amount_tf.getText());
        }catch(Exception e){
            e.printStackTrace();
        }

        if (!bank.isEmpty() && !checkNo.isEmpty() && amount != 0){
            check = new Check(bank, checkNo, amount);
            this.bank_tf.setText("");
            this.check_no_tf.setText("");
            this.amount_tf.setText("");
        }
        return check;
    }
}
