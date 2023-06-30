package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ReviseItemController implements Initializable {

    @FXML TextField itemField;
    @FXML TextField qtyField;
    @FXML TextField costField;
    @FXML TextField amountField;
    @FXML TextField qtr1Field;
    @FXML TextField qtr2Field;
    @FXML TextField qtr3Field;
    @FXML TextField qtr4Field;
    @FXML CheckBox check1;
    @FXML CheckBox check2;
    @FXML CheckBox check3;
    @FXML CheckBox check4;


    COBItem item;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.item = (COBItem)Utility.getSelectedObject();

        renderForm();
    }

    private void renderForm() {
        itemField.setText(item.getDescription());
        qtyField.setText(String.valueOf(item.getQty()));
        costField.setText(String.format("₱ %,.2f", item.getCost()));
        amountField.setText(String.format("₱ %,.2f", item.getAmount()));
        qtr1Field.setText(String.format("₱ %,.2f", item.getQtr1()));
        qtr2Field.setText(String.format("₱ %,.2f", item.getQtr2()));
        qtr3Field.setText(String.format("₱ %,.2f", item.getQtr3()));
        qtr4Field.setText(String.format("₱ %,.2f", item.getQtr4()));

        check1.setSelected(item.getQtr1()>0);
        check2.setSelected(item.getQtr2()>0);
        check3.setSelected(item.getQtr3()>0);
        check4.setSelected(item.getQtr4()>0);
    }

    public void computeBalances() {
        int checkCount = 0;

        checkCount += check1.isSelected() ? 1 : 0;
        checkCount += check2.isSelected() ? 1 : 0;
        checkCount += check3.isSelected() ? 1 : 0;
        checkCount += check4.isSelected() ? 1 : 0;

        try {
            int qty = Integer.parseInt(qtyField.getText());
            double cost = Double.parseDouble(costField.getText().replace("₱","").replace(",","").replace(" ",""));
            double amount = qty * cost;

            double dist = amount/checkCount;

            amountField.setText(String.format("₱ %,.2f", amount));
            qtr1Field.setText( check1.isSelected() ? String.format("₱ %,.2f", dist) : "0" );
            qtr2Field.setText( check2.isSelected() ? String.format("₱ %,.2f", dist) : "0" );
            qtr3Field.setText( check3.isSelected() ? String.format("₱ %,.2f", dist) : "0" );
            qtr4Field.setText( check4.isSelected() ? String.format("₱ %,.2f", dist) : "0" );

        }catch(NumberFormatException ex) {
            ex.printStackTrace();
        }

    }

    public void onSaveChanges() {
        try {
            item.setQty(Integer.parseInt(qtyField.getText()));
            item.setCost(Double.parseDouble(costField.getText().replace("₱","").replace(",","").replace(" ","")));
            item.setQtr1(Double.parseDouble(qtr1Field.getText().replace("₱","").replace(",","").replace(" ","")));
            item.setQtr2(Double.parseDouble(qtr2Field.getText().replace("₱","").replace(",","").replace(" ","")));
            item.setQtr3(Double.parseDouble(qtr3Field.getText().replace("₱","").replace(",","").replace(" ","")));
            item.setQtr4(Double.parseDouble(qtr4Field.getText().replace("₱","").replace(",","").replace(" ","")));

            CobItemDAO.update(item);

            Utility.getParentController().receive(item);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
