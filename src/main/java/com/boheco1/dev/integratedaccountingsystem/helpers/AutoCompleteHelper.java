package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javax.swing.*;
import java.net.URL;
import java.util.*;

public class AutoCompleteHelper {

    private static Stock result = null;

    public static void bindAutocomplete(JFXTextField textField, List<Stock> data){
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
                result = event.getCompletion();

                //Temporary display to get the information on selected stock. Replace this with the codes to set the text of the respected textfields
                //String information = "ID: " + result.getId() + "\n";
                //information += "Stock: " + result.getStockName() + "\n";
                //information += "Quantity: " + result.getQuantity() + "\n";
                //information += "Price: " + result.getPrice() + "\n";
                //JOptionPane.showMessageDialog(null, information);
                //return result;
            }
        });
        //return result;
    }

    public static Stock getResult(){
        return result;
    }
}
