package com.boheco1.dev.integratedaccountingsystem.finance;

import com.boheco1.dev.integratedaccountingsystem.dao.ChartOfAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ChartOfAccount;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;


import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VoucherController extends MenuControllerHandler implements Initializable {

    @FXML
    private TextField search;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea nature;

    @FXML
    private Label total;

    @FXML
    private JFXTextField payee, debit, credit, code, title;

    private ChartOfAccount selectedChartOfAccount;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            date.setValue(Utility.serverDate());
        } catch (Exception e) {
            date.setValue(LocalDate.now());
            throw new RuntimeException(e);
        }
        bindChartOfAccountsAutocomplete(title);
        bindChartOfAccountsAutocomplete(code);

    }


    @FXML
    void onPrint(ActionEvent event) {

    }

    @FXML
    void onSave(ActionEvent event) {

    }

    private void bindChartOfAccountsAutocomplete(JFXTextField textField){
        AutoCompletionBinding<ChartOfAccount> suggestion = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<ChartOfAccount> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() >= 1){
                        try {
                            list = ChartOfAccountDAO.search(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(ChartOfAccount object) {
                        return object.getTitle();
                    }

                    @Override
                    public ChartOfAccount fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        suggestion.setOnAutoCompleted(event -> {
            selectedChartOfAccount = event.getCompletion();
            debit.setText("");
            credit.setText("");
            title.setText(selectedChartOfAccount.getTitle());
            code.setText(selectedChartOfAccount.getAccountCode());

        });
    }


}
