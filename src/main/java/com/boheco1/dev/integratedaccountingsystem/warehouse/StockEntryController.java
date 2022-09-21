package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StockEntryController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stockStackPane;

    @FXML
    private JFXTextField stockName, serialNumber, brand, model, quantity, unit, price, neaCode, threshold, localCode, accountCode;

    @FXML
    private JFXTextArea comments, description, local_desc;

    @FXML
    private DatePicker manuDate, valDate;

    @FXML
    private JFXComboBox<StockType> type;

    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXCheckBox individualized_cb;
    private Stock stock = null;

    private boolean isNew = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.bindAutocomplete(stockName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bindStockTypes();
        this.bindNumbers();
        this.stockStackPane = Utility.getStackPane();
    }
    /**
     * Inserts the Stock information
     * @return void
     */
    @FXML
    private void saveBtn()  {
        String name = this.stockName.getText();
        String desc = this.description.getText();
        String ldesc = this.local_desc.getText();
        String brand = this.brand.getText();
        String model = this.model.getText();
        String unit = this.unit.getText();
        StockType stockType = this.type.getSelectionModel().getSelectedItem();
        LocalDate manDate = this.manuDate.getValue();
        LocalDate valDate = this.valDate.getValue();
        String serialNumber = this.serialNumber.getText();
        String localCode = this.localCode.getText();
        String accountCode = this.accountCode.getText();
        String neaCode = this.neaCode.getText();
        String comments = this.comments.getText();
        int threshold = 0;
        double quantity = 0;
        double price = 0;

        try {
            quantity = Double.parseDouble(this.quantity.getText());
        }catch (Exception e){

        }

        try {
            price  = Double.parseDouble(this.price.getText());
        }catch (Exception e){

        }

        try {
            threshold  = Integer.parseInt(this.threshold.getText());
        }catch (Exception e){

        }

        if (this.stock == null) this.stock = new Stock();

        this.stock.setDescription(desc);
        this.stock.setLocalDescription(ldesc);
        this.stock.setBrand(brand);
        this.stock.setModel(model);
        this.stock.setPrice(price);
        this.stock.setTrashed(false);
        this.stock.setUserIDCreated(ActiveUser.getUser().getId());
        if (stockType != null) this.stock.setTypeID(stockType.getId());
        this.stock.setUnit(unit);
        this.stock.setStockName(name);
        this.stock.setComments(comments);
        this.stock.setSerialNumber(serialNumber);
        if (manDate != null) this.stock.setManufacturingDate(manDate);
        if (valDate != null) this.stock.setValidityDate(valDate);
        this.stock.setNeaCode(neaCode);
        this.stock.setLocalCode(localCode);
        this.stock.setCritical(threshold);

        if (accountCode.length() < 4 || accountCode == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid accounting code! Minimum of 4 characters!",
                    stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if ((neaCode.length() < 4 || neaCode == null) && (localCode.length() < 4 || localCode == null)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid NEA/Local code! Minimum of 4 characters!",
                    stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (desc.length() == 0 || desc == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid NEA description!",
                    stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (stockType == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please select a valid stock type!",
                    stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (unit == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for unit!",
                        stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else{
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Stock Entry","This process is final. Confirm Stock Entry?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            double finalQuantity = quantity;
            accept.setOnAction(__ -> {
                if (isNew) {
                    //If new Stock item, set stock quantity to 0 and insert Stock to database
                    this.stock.setQuantity(0);

                    this.stock.setAcctgCode(accountCode);
                    this.stock.setIndividualized(this.individualized_cb.isSelected());
                    try {
                        StockDAO.add(this.stock);
                        //If quantity is set, create stock entry log, otherwise just add record in stocks table
                        if (finalQuantity > 0 ) {
                            //Create StockEntryLog object
                            StockEntryLog stockEntryLog = new StockEntryLog();
                            stockEntryLog.setQuantity(finalQuantity);
                            stockEntryLog.setPrice(this.stock.getPrice());
                            stockEntryLog.setSource("Purchased");

                            //Insert StockEntryLog to database
                            StockDAO.stockEntry(this.stock, stockEntryLog);
                        }
                        AlertDialogBuilder.messgeDialog("Stock Entry", "New stock was successfully added!", stockStackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                    }catch (Exception e){
                        AlertDialogBuilder.messgeDialog("System Error", "New stock was not successfully added due to:"+e.getMessage()+".", stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
                        e.printStackTrace();
                    }
                    this.reset();
                }else{
                    //If existing Stock item, update the stock basic details based on new entry, insert to StockEntryLog and update the Stock quantity.
                    try {
                        StockDAO.updateDetails(this.stock);

                        //Create StockEntryLog object
                        StockEntryLog stockEntryLog = new StockEntryLog();
                        stockEntryLog.setQuantity(finalQuantity);
                        stockEntryLog.setPrice(this.stock.getPrice());
                        stockEntryLog.setSource("Purchased");

                        //Insert StockEntryLog to database
                        StockDAO.stockEntry(this.stock, stockEntryLog);
                        AlertDialogBuilder.messgeDialog("Stock Entry", "New stock was successfully added!", stockStackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                        this.reset();
                    }catch (Exception e){
                        AlertDialogBuilder.messgeDialog("System Error", "New stock was not successfully added due to:"+e.getMessage()+" error.", stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
                    }
                }
                dialog.close();
            });
        }
    }
    /**
     * Binds the textfields to accept numeric inputs
     * @return void
     */
    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.quantity);
        InputHelper.restrictNumbersOnly(this.price);
        InputHelper.restrictNumbersOnly(this.threshold);
    }
    /**
     * Binds list of stock types the dropdownlist
     * @return void
     */
    public void bindStockTypes(){
        try {
            List<StockType> types = StockDAO.getTypes();
            for (StockType t : types){
                type.getItems().add(t);
            }
            type.setConverter(new StringConverter<>() {
                @Override
                public String toString(StockType object) {
                    return object==null? "" : object.getStockType();
                }
                @Override
                public StockType fromString(String string) {
                    try {
                        return StockDAO.getStockType(string);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            type.setOnAction(actionEvent -> {
                StockType selected = type.getSelectionModel().getSelectedItem();
                if (selected != null)
                    unit.setText(selected.getUnit());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Attaches the autocomplete to the textfield
     * @param textField the text input
     * @return void
     */
    public void bindAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SlimStock> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SlimStock> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.search(query, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        isNew = true;
                        stock = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(SlimStock object) {
                        return object.getDescription();
                    }

                    @Override
                    public SlimStock fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(event -> {
            SlimStock result = event.getCompletion();
            try {
                isNew = false;
                stock = StockDAO.get(result.getId());
                stockName.setText(stock.getStockName());
                accountCode.setText(stock.getAcctgCode());
                localCode.setText(stock.getLocalCode());
                //quantity.setText(""+stock.getQuantity());
                price.setText(""+stock.getPrice());
                serialNumber.setText(stock.getSerialNumber());
                brand.setText(stock.getBrand());
                model.setText(stock.getModel());
                unit.setText(stock.getUnit());
                neaCode.setText(stock.getNeaCode());
                comments.setText(stock.getComments());
                description.setText(stock.getDescription());
                local_desc.setText(stock.getLocalDescription());
                manuDate.setValue(stock.getManufacturingDate());
                valDate.setValue(stock.getValidityDate());
                threshold.setText(""+stock.getCritical());
                individualized_cb.setSelected(stock.isIndividualized());
                ObservableList<StockType> stocktypes = type.getItems();
                int index = 0;
                for (int i=0;  i < stocktypes.size(); i++){
                    if (stocktypes.get(i).getId() == stock.getTypeID()) {
                        index = i;
                        break;
                    }
                }
                type.getSelectionModel().select(index);
                stockName.setDisable(true);
                serialNumber.setDisable(true);
                accountCode.setDisable(true);
                localCode.setDisable(true);
                brand.setDisable(true);
                model.setDisable(true);
                unit.setDisable(true);
                neaCode.setDisable(true);
                comments.setDisable(true);
                description.setDisable(true);
                local_desc.setDisable(true);
                manuDate.setDisable(true);
                valDate.setDisable(true);
                threshold.setDisable(true);
                type.setDisable(true);
                individualized_cb.setDisable(true);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", e.getMessage(), this.stockStackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }
    /**
     * Resets every input fields and variables
     * @return void
     */
    public void reset(){
        this.stock = null;
        this.isNew = true;

        this.stockName.setText("");
        this.localCode.setText("");
        this.accountCode.setText("");
        this.brand.setText("");
        this.quantity.setText("");
        this.unit.setText("");
        this.price.setText("");
        this.type.getSelectionModel().select(null);
        this.description.setText("");
        this.local_desc.setText("");
        this.model.setText("");
        this.serialNumber.setText("");
        this.neaCode.setText("");
        this.comments.setText("");
        this.manuDate.setValue(null);
        this.valDate.setValue(null);
        this.threshold.setText("");
        this.stockName.setDisable(false);
        this.localCode.setDisable(false);
        this.accountCode.setDisable(false);
        this.serialNumber.setDisable(false);
        this.brand.setDisable(false);
        this.model.setDisable(false);
        this.unit.setDisable(false);
        this.neaCode.setDisable(false);
        this.comments.setDisable(false);
        this.description.setDisable(false);
        this.local_desc.setDisable(false);
        this.manuDate.setDisable(false);
        this.valDate.setDisable(false);
        this.threshold.setDisable(false);
        this.type.setDisable(false);
        this.individualized_cb.setSelected(false);
        this.individualized_cb.setDisable(false);
    }

    /**
     * Clears everything
     * @return void
     */
    @FXML
    private void clear()  {
        reset();
    }
}
