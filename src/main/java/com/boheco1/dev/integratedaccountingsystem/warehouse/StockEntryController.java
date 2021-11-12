package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.AutoCompleteHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.ParseHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.StockType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javax.swing.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class StockEntryController implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stockStackPane;

    @FXML
    private JFXTextField stockName, serialNumber, brand, model, quantity, unit, price, neaCode;

    @FXML
    private JFXTextArea comments, description;

    @FXML
    private DatePicker manuDate, valDate;

    @FXML
    private JFXComboBox<String> source;

    @FXML JFXComboBox<StockType> type;

    @FXML
    private JFXButton saveBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            bindAutocomplete(stockName,StockDAO.getList(10,0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveBtn(ActionEvent event) throws Exception {
        //int id, String stockName, String description, int serialNumber,
        // String brand, String model, LocalDate manufacturingDate,
        // LocalDate validityDate, int typeID, String unit, int quantity,
        // double price, String neaCode, boolean isTrashed, String comments,
        // LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime trashedAt,
        // int userIDCreated, int userIDUpdated, int userIDTrashed
        Stock stock = new Stock(
                -1, stockName.getText(),
                description.getText(),
                Integer.parseInt(serialNumber.getText()),
                brand.getText(),
                model.getText(),
                manuDate.getValue(),
                valDate.getValue(),
                type.getSelectionModel().getSelectedItem().getId(),
                unit.getText(),
                ParseHelper.intifyOrZero(quantity.getText()),
                ParseHelper.doublifyOrZero(price.getText()),
                neaCode.getText(),
                false,
                comments.getText(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                ActiveUser.getUser().getId(),
                ActiveUser.getUser().getId(),
                -1
        );
        StockDAO.add(stock);
        AlertDialogBuilder.messgeDialog("Stock Entry","New stock added",stockStackPane,AlertDialogBuilder.INFO_DIALOG);
    }

    public void bindAutocomplete(JFXTextField textField, List<Stock> data){
        AutoCompletionBinding<Stock> stockSuggest = TextFields.bindAutoCompletion(textField,
                new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Stock>>() {
                    @Override
                    public Collection<Stock> call(AutoCompletionBinding.ISuggestionRequest param) {

                        //Value typed in the textfield
                        String query = param.getUserText();

                        //Initialize list of stocks
                        List<Stock> list = new ArrayList<>();

                        //Perform DB query when length of search string is 4 or above
                        if (query.length() > 3){
                            /*try {
                                StockDAO here...
                            } catch (IOException ex) {
                                Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                            }*/

                            //Linear search to test search stocks on keypress
                            //Remove this code when the StockDAO returns the search results
                            for (Stock s : data){
                                if (s.getStockName().toLowerCase().contains(query.toLowerCase())){
                                    list.add(s);
                                }
                            }
                        }
                        return list;
                    }
                }, new StringConverter<Stock>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(Stock object) {
                        return object.getStockName();
                    }

                    @Override
                    public Stock fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Stock>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Stock> event) {
                Stock result = event.getCompletion();
                stockName.setText(result.getStockName());
                quantity.setText(""+result.getQuantity());
                price.setText(""+result.getPrice());
            }
        });
    }
}
