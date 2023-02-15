package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;


public class ArPrint {
    @FXML
    Text receivedFrom1, receivedFrom2, total1, total2;

    @FXML
    Label paymentFor1, amount1, paymentFor2, amount2, totalStr1, totalStr2;

    public void setData(String receivedFrom, String amountStr, float total, String[] paymentFor, double[] amount){
        receivedFrom1.setText(receivedFrom);
        receivedFrom2.setText(receivedFrom);
        total1.setText(String.format("%,.2f", total));
        total2.setText(String.format("%,.2f", total));
        totalStr1.setText(Utility.doubleAmountToWords(total));
        totalStr2.setText(Utility.doubleAmountToWords(total));

        StringBuffer paymentForBuff = new StringBuffer();
        StringBuffer amountBuff = new StringBuffer();

        for(int i=0; i<paymentFor.length; i++) {
            paymentForBuff.append(paymentFor[i] + "\n");
            amountBuff.append(String.format("%,.2f",amount[i]) + "\n");
        }

        paymentFor1.setText(paymentForBuff.toString());
        amount1.setText(amountBuff.toString());
        paymentFor2.setText(paymentForBuff.toString());
        amount2.setText(amountBuff.toString());
    }
}
