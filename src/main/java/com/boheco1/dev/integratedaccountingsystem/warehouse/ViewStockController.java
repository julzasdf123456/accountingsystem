package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ViewStockController implements Initializable {

    @FXML
    private JFXTextField stockName, serialNumber, brand, model, quantity, unit, price, neaCode, threshold, localCode, accountCode;

    @FXML
    private JFXTextArea description, comments;

    @FXML
    private DatePicker manuDate, valDate;

    @FXML
    private JFXComboBox type;

    @FXML
    private TabPane tabPane;

    private TableView stockEntries = new TableView(), stockReleases = new TableView(), mr = new TableView();

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
        //Gets the main stackpane and assign to current stackpane
        this.stackPane = Utility.getStackPane();
        //Displays the stock details
        Platform.runLater(() -> {
            this.stock = (Stock) Utility.getSelectedObject();
            this.bindNumbers();
            this.bindStockTypes();

            Tab stocks_tab = new Tab();
            stocks_tab.setText("Stock Entries");
            stocks_tab.setContent(this.stockEntries);
            tabPane.getTabs().add(stocks_tab);

            this.createStockEntriesTable();
            this.initializeStockEntries();

            Tab mr_tab = new Tab();
            mr_tab.setText("MR Items");
            mr_tab.setContent(this.mr);
            tabPane.getTabs().add(mr_tab);

            this.createMRItems();
            this.initializeMRedItems();

            Tab releases_tab = new Tab();
            releases_tab.setText("Stock Releases");
            releases_tab.setContent(this.stockReleases);
            tabPane.getTabs().add(releases_tab);

            this.createStockReleasesTable();
            this.initializeStockReleases();

            this.initializeInformation();
            this.setLabels();
            this.toggle();
        });
    }
    /**
     * Shows/hides the edit button
     * @return void
     */
    public void toggle(){
        this.saveBtn.setVisible(false);
        this.editMode.setOnAction(actionEvent -> saveBtn.setVisible(editMode.isSelected()));
    }
    /**
     * Updates the stock details. Quantity, Stock ID/Local ID, and Price are excluded.
     * @return void
     */
    @FXML
    public void updateStock(){
        String name = this.stockName.getText();
        String desc = this.description.getText();
        String brand = this.brand.getText();
        String model = this.model.getText();
        String unit = this.unit.getText();
        StockType stockType = (StockType) this.type.getSelectionModel().getSelectedItem();
        String serialNumber = this.serialNumber.getText();
        String localCode = this.localCode.getText();
        String accountCode = this.accountCode.getText();

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

        String id = this.stock.getId();
        this.stock = new Stock();

        //Mandatory fields
        this.stock.setId(id);
        this.stock.setLocalCode(localCode);
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

        if (localCode.length() == 0 || localCode == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid local code!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (desc.length() == 0 || desc == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid description!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (price == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for price!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (unit == null) {
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
                this.stock.setAcctgCode(accountCode);
                this.stock.setLocalCode(localCode);
                this.stock.setQuantity(quantity);
                StockDAO.update(this.stock);
                AlertDialogBuilder.messgeDialog("Update Stock", "Current stock details was successfully updated!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "New stock was not successfully added due to:"+e.getMessage()+".", stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }
    }
    /**
     * Creates the stock entries table
     * @return void
     */
    public void createStockEntriesTable(){
        TableColumn<StockEntryLog, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(90);
        column1.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<StockEntryLog, String> column2 = new TableColumn<>("Price");
        column2.setMinWidth(110);
        column2.setCellValueFactory(new PropertyValueFactory<>("Price"));

        TableColumn<StockEntryLog, String> column3 = new TableColumn<>("Source");
        column3.setMinWidth(110);
        column3.setCellValueFactory(new PropertyValueFactory<>("source"));

        TableColumn<StockEntryLog, String> column4 = new TableColumn<>("Date");
        column4.setMinWidth(70);
        column4.setCellValueFactory(stockEntryLog -> new SimpleStringProperty(stockEntryLog.getValue().getUpdatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<StockEntryLog, String> column5 = new TableColumn<>("Receiving Report");
        column5.setMinWidth(135);
        column5.setStyle("-fx-alignment: center;");
        column5.setCellValueFactory(new PropertyValueFactory<>("rrNo"));

        stockEntries.getColumns().add(column4);
        stockEntries.getColumns().add(column5);
        stockEntries.getColumns().add(column1);
        stockEntries.getColumns().add(column2);
        stockEntries.getColumns().add(column3);
    }
    /**
     * Creates the MR items table
     * @return void
     */
    public void createMRItems() {

        TableColumn<MrItem, String> column = new TableColumn<>("MR No");
        column.setMinWidth(100);
        column.setCellValueFactory(new PropertyValueFactory<>("mrNo"));
        column.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column2 = new TableColumn<>("Qty");
        column2.setMinWidth(50);
        column2.setCellValueFactory(new PropertyValueFactory<>("qty"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column3 = new TableColumn<>("Unit Price");
        column3.setMinWidth(80);
        column3.setCellValueFactory(item -> {
            try {

                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getPrice());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3.setStyle("-fx-alignment: center-left;");
        TableColumn<MrItem, Double> column3a = new TableColumn<>("Total");
        column3a.setMinWidth(95);
        column3a.setCellValueFactory(item -> {
            try {

                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getPrice()*item.getValue().getQty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3a.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column4 = new TableColumn<>("Date of MR");
        column4.setMinWidth(100);
        column4.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(MrDAO.get(item.getValue().getMrNo()).getDateOfMR().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column4.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column7 = new TableColumn<>("Remarks");
        column7.setMinWidth(155);
        column7.setCellValueFactory(item -> {
            if (item.getValue().getDateReturned() == null){
                return new ReadOnlyObjectWrapper<>(item.getValue().getRemarks());
            }else{
                String status = item.getValue().getRemarks()+" ("+item.getValue().getStatus()+" on "+item.getValue().getDateReturned()+")";
                return new ReadOnlyObjectWrapper<>(status);
            }
        });
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("Employee");
        column8.setMinWidth(132);
        column8.setCellValueFactory(item -> {
            try {
                MR mr = MrDAO.get(item.getValue().getMrNo());
                return new ReadOnlyObjectWrapper<>(mr.getEmployeeInfo().getEmployeeFirstName()+" "+mr.getEmployeeInfo().getEmployeeLastName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column8.setStyle("-fx-alignment: center-left;");

        this.mr.getColumns().add(column);
        this.mr.getColumns().add(column4);
        this.mr.getColumns().add(column2);
        this.mr.getColumns().add(column3);
        this.mr.getColumns().add(column3a);
        this.mr.getColumns().add(column8);
        this.mr.getColumns().add(column7);
    }
    /**
     * Creates the stock releases table
     * @return void
     */
    public void createStockReleasesTable(){
        TableColumn<Releasing, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(90);
        column1.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<Releasing, String> column2 = new TableColumn<>("Price");
        column2.setMinWidth(110);
        column2.setCellValueFactory(new PropertyValueFactory<>("Price"));

        TableColumn<Releasing, String> column3 = new TableColumn<>("Work Order");
        column3.setMinWidth(135);
        column3.setCellValueFactory(new PropertyValueFactory<>("workOrderNo"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<Releasing, String> column4 = new TableColumn<>("Date");
        column4.setMinWidth(70);
        column4.setCellValueFactory(releasedStock -> new SimpleStringProperty(releasedStock.getValue().getUpdatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<StockEntryLog, String> column5 = new TableColumn<>("MCT No");
        column5.setMinWidth(135);
        column5.setStyle("-fx-alignment: center;");
        column5.setCellValueFactory(new PropertyValueFactory<>("mctNo"));

        TableColumn<StockEntryLog, String> column6 = new TableColumn<>("MIRS ID");
        column6.setMinWidth(135);
        column6.setStyle("-fx-alignment: center;");
        column6.setCellValueFactory(new PropertyValueFactory<>("mirsID"));

        stockReleases.getColumns().add(column4);
        stockReleases.getColumns().add(column5);
        stockReleases.getColumns().add(column1);
        stockReleases.getColumns().add(column2);
        stockReleases.getColumns().add(column3);
        stockReleases.getColumns().add(column6);
    }
    /**
     * Sets the stock details
     * @return void
     */
    public void initializeInformation(){
        this.stockName.setText(stock.getStockName());
        this.localCode.setText(stock.getId());
        this.accountCode.setText(stock.getAcctgCode());
        this.neaCode.setText(stock.getNeaCode());
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
        this.comments.setText(stock.getComments());
        ObservableList<StockType> stocktypes = this.type.getItems();
        int index = 0;
        for (int i=0;  i < stocktypes.size(); i++){
            if (stocktypes.get(i).getId().equals(this.stock.getTypeID())) {
                index = i;
                break;
            }
        }
        this.type.getSelectionModel().select(index);
    }
    /**
     * Initializes the stock entries table
     * @return void
     */
    public void initializeStockEntries(){
        try {
            ObservableList<StockEntryLog> stocks = FXCollections.observableList(StockDAO.getEntryLogs(stock));
            stockEntries.getItems().setAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the stock releases table
     * @return void
     */
    public void initializeStockReleases(){
        try {
            ObservableList<Releasing> stocks = FXCollections.observableList(StockDAO.getReleasedStocks(stock, Utility.RELEASED));
            stockReleases.getItems().setAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the MR items table
     * @return void
     */
    public void initializeMRedItems(){
        try {
            ObservableList<MrItem> mrItems = FXCollections.observableList(MrDAO.getMRItems(stock.getId(), null));
            this.mr.getItems().setAll(mrItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Binds the textfields to accept numbers only
     * @return void
     */
    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.quantity);
        InputHelper.restrictNumbersOnly(this.price);
        InputHelper.restrictNumbersOnly(this.threshold);
    }
    /**
     * Populates the stock types in the dropdown box
     * @return void
     */
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
    /**
     * Sets the stackpane
     * @return void
     */
    public void setStackPane(StackPane stackpane){
        this.stackPane = stackpane;
    }
    /**
     * Sets the date labels
     * @return void
     */
    public void setLabels(){
        this.md_lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 11));
        this.vd_lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 11));
    }
}
