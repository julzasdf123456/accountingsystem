package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ArPrint {
    @FXML
    Text receivedFrom1, receivedFrom2, total1, total2, date1, date2, user1, user2, transNumber1, transNumber2;

    @FXML
    Label paymentFor1, amount1, paymentFor2, amount2, totalStr1, totalStr2;

    public void setData(String trno, String receivedFrom, String amountStr, float total, String[] paymentFor, double[] amount, LocalDate trDate){
        transNumber1.setText(trno);
        transNumber2.setText(trno);
        receivedFrom1.setText(receivedFrom);
        receivedFrom2.setText(receivedFrom);
        total1.setText(String.format("%,.2f", total));
        total2.setText(String.format("%,.2f", total));
        totalStr1.setText(Utility.doubleAmountToWords(total));
        totalStr2.setText(Utility.doubleAmountToWords(total));
        String dateStr = trDate.format(DateTimeFormatter.ofPattern("MM/dd/YYYY"));
        String username = ActiveUser.getUser().getUserName();
        date1.setText(dateStr);
        date2.setText(dateStr);
        user1.setText(username);
        user2.setText(username);

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
