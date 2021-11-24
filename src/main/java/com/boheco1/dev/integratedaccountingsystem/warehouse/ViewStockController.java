package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.StockEntryLog;
import com.boheco1.dev.integratedaccountingsystem.objects.StockType;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ViewStockController implements Initializable {

    @FXML
    private JFXTextField stockName, serialNumber, brand, model, quantity, unit, price, neaCode, threshold;

    @FXML
    private JFXTextArea description, comments;

    @FXML
    private DatePicker manuDate, valDate;

    @FXML
    private JFXComboBox type;

    @FXML
    private TableView stockEntries;

    @FXML
    private JFXToggleButton editMode;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private Label md_lbl, vd_lbl;

    private Stock stock;

    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            this.bindNumbers();
            this.bindStockTypes();
            this.createStockEntriesTable();
            this.initializeStockEntries();
            this.initializeInformation();
            this.setLabels();
            this.toggle();
        });
    }

    public void toggle(){
        this.saveBtn.setVisible(false);
        this.editMode.setOnAction(actionEvent -> saveBtn.setVisible(editMode.isSelected()));
    }

    @FXML
    public void updateStock(){
        String name = this.stockName.getText();
        String brand = this.brand.getText();
        String model = this.model.getText();
        String unit = this.unit.getText();
        StockType stockType = (StockType) this.type.getSelectionModel().getSelectedItem();
        String serialNumber = this.serialNumber.getText();

        int threshold = 0;
        int quantity = 0;
        double price = 0;

        try {
            quantity = Integer.parseInt(this.quantity.getText());
        }catch (Exception e){

        }

        try {
            price = Double.parseDouble(this.price.getText());
        }catch (Exception e){

        }

        try {
            threshold  = Integer.parseInt(this.threshold.getText());
        }catch (Exception e){

        }

        int id = this.stock.getId();
        this.stock = new Stock();

        //Mandatory fields
        this.stock.setId(id);
        this.stock.setStockName(name);
        this.stock.setBrand(brand);
        this.stock.setModel(model);
        this.stock.setPrice(price);

        this.stock.setTrashed(false);
        this.stock.setUserIDCreated(ActiveUser.getUser().getId());
        if (stockType != null) this.stock.setTypeID(stockType.getId());
        this.stock.setUnit(unit);

        //Optional Fields
        this.stock.setDescription(this.description.getText());
        this.stock.setComments(this.comments.getText());
        this.stock.setSerialNumber(serialNumber);
        this.stock.setManufacturingDate(this.manuDate.getValue());
        this.stock.setValidityDate(this.valDate.getValue());
        this.stock.setNeaCode(this.neaCode.getText());
        this.stock.setCritical(threshold);

        if (name.length() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for stock name!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (brand.length() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for brand!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (price == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for price!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (quantity == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (unit.length() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for unit!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (stockType == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please select a valid stock type!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (threshold == 0){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please select enter a valid threshold remaining limit for the stock!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else {
            try {
                this.stock.setQuantity(quantity);
                StockDAO.update(this.stock);
                AlertDialogBuilder.messgeDialog("Update Stock", "Current stock details was successfully updated!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "New stock was not successfully added due to:"+e.getMessage()+".", stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }
    }

    public void setStock(Stock stock){
        try {
            this.stock = StockDAO.get(stock.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createStockEntriesTable(){
        TableColumn<StockEntryLog, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(110);
        column1.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        TableColumn<StockEntryLog, String> column2 = new TableColumn<>("Price");
        column2.setMinWidth(110);
        column2.setCellValueFactory(new PropertyValueFactory<>("Price"));

        TableColumn<StockEntryLog, String> column3 = new TableColumn<>("Source");
        column3.setMinWidth(110);
        column3.setCellValueFactory(new PropertyValueFactory<>("source"));

        TableColumn<StockEntryLog, String> column4 = new TableColumn<>("Date");
        column4.setMinWidth(70);
        column4.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        stockEntries.getColumns().add(column1);
        stockEntries.getColumns().add(column2);
        stockEntries.getColumns().add(column3);
        stockEntries.getColumns().add(column4);
    }

    public void initializeInformation(){
        this.stockName.setText(stock.getStockName());
        this.serialNumber.setText(""+stock.getSerialNumber());
        this.brand.setText(stock.getBrand());
        this.model.setText(stock.getModel());
        this.description.setText(stock.getDescription());
        LocalDate mDate = stock.getManufacturingDate();
        LocalDate vDate = stock.getValidityDate();
        if (mDate != null)
            this.manuDate.setValue(mDate);
        if (vDate != null)
            this.valDate.setValue(vDate);
        this.quantity.setText(""+stock.getQuantity());
        this.unit.setText(stock.getUnit());
        this.price.setText(""+stock.getPrice());
        this.threshold.setText(""+stock.getCritical());
        this.neaCode.setText(stock.getNeaCode());
        this.comments.setText(stock.getComments());
        ObservableList<StockType> stocktypes = this.type.getItems();
        int index = 0;
        for (int i=0;  i < stocktypes.size(); i++){
            if (stocktypes.get(i).getId() == this.stock.getTypeID()) {
                index = i;
                break;
            }
        }
        this.type.getSelectionModel().select(index);
    }
    public void initializeStockEntries(){
        try {
            ObservableList<StockEntryLog> stocks = FXCollections.observableList(StockDAO.getEntryLogs(stock));
            stockEntries.getItems().setAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.quantity);
        InputHelper.restrictNumbersOnly(this.price);
        InputHelper.restrictNumbersOnly(this.threshold);
    }

    public void bindStockTypes(){
        try {
            List<StockType> types = StockDAO.getTypes();
            for (StockType t : types){
                this.type.getItems().add(t);
            }
            this.type.setConverter(new StringConverter<StockType>() {
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
            this.type.setOnAction(actionEvent -> {
                StockType selected = (StockType) this.type.getSelectionModel().getSelectedItem();
                if (selected != null)
                    this.unit.setText(selected.getUnit());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStackPane(StackPane stackpane){
        this.stackPane = stackpane;
    }

    public void setLabels(){
        this.md_lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 11));
        this.vd_lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 11));
    }
}
