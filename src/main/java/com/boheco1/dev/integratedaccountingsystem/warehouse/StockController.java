package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class StockController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView<SlimStock> stocksTable;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private JFXComboBox<Integer> page_cb;

    private HashMap<Integer, SlimStock> selected = new HashMap<>();

    private JFXDialog dialog;

    private int LIMIT = 3;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.initializeStocks();
        this.bindPages();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    @FXML
    private void newStock() {
        Node node = ContentHandler.getNodeFromFxml(getClass(),"../warehouse_stock_entry.fxml");
        Utility.getContentPane().getChildren().setAll(node);
    }

    @FXML
    public void searchStock(){
        selected = new HashMap<>();
        String key = this.query_tf.getText();
        if (key.length() == 0) {
            this.initializeStocks();
        }else {
            try {
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.search(key, 0));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void trashStock(){
        if (selected.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Warning", "Select item(s) before proceeding!", stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else {

            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setHeading(new Text("Confirm Trash Stock(s)"));
            Label label = new Label("Do you want to put the selected stock(s) to recycle bin?");
            label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
            label.setWrapText(true);
            label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
            dialogLayout.setBody(label);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
            JFXButton trash = new JFXButton("Trash");
            trash.setStyle("-fx-background-color: "+ColorPalette.DANGER);
            trash.setTextFill(Paint.valueOf("#FFFFFF"));
            trash.setMinWidth(75);
            trash.setOnAction(event -> {
                Collection<SlimStock> sel = selected.values();
                Object selected_items[] = sel.toArray();
                boolean ok = true;
                for (int i = 0; i < selected.size(); i++) {
                    SlimStock stock = (SlimStock) selected_items[i];
                    try {
                        stocksTable.getItems().remove(stock);
                        StockDAO.trash(StockDAO.get(stock.getId()));
                    } catch (Exception e) {
                        AlertDialogBuilder.messgeDialog("System Error", "Failed to put item to trash due to: "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                        ok = false;
                        break;
                    }
                }
                if (ok){
                    AlertDialogBuilder.messgeDialog("System Information", "Stock item(s) were put to trash!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                    bindPages();
                    dialog.close();
                }
            });
            JFXButton cancel = new JFXButton("Cancel");
            cancel.setDefaultButton(true);
            cancel.setMinWidth(75);
            cancel.setOnAction(event -> dialog.close());
            List<JFXButton> actions = new ArrayList<>();
            actions.add(cancel);
            actions.add(trash);
            dialogLayout.setActions(actions);
            dialog.show();
        }
    }

    @FXML
    public void viewStock(){
        if (selected.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Warning", "Select an item before proceeding!", stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../warehouse_stock_update.fxml"));
                Parent parent = loader.load();

                Collection<SlimStock> sel = selected.values();
                Object selected_items[] = sel.toArray();
                SlimStock slim_stock = (SlimStock) selected_items[0];
                Stock stock = StockDAO.get(slim_stock.getId());

                JFXTextField stockName_tf = (JFXTextField) loader.getNamespace().get("stockName");
                JFXTextField serialNumber_tf = (JFXTextField) loader.getNamespace().get("serialNumber");
                JFXTextField brand_tf = (JFXTextField) loader.getNamespace().get("brand");
                JFXTextField model_tf = (JFXTextField) loader.getNamespace().get("model");
                JFXTextArea desc_tf = (JFXTextArea) loader.getNamespace().get("description");
                DatePicker manuDate_dp = (DatePicker) loader.getNamespace().get("manuDate");
                DatePicker valDate_dp = (DatePicker) loader.getNamespace().get("valDate");
                JFXComboBox type_cb = (JFXComboBox) loader.getNamespace().get("type");
                JFXComboBox source_cb = (JFXComboBox) loader.getNamespace().get("source");
                JFXTextField quantity_tf = (JFXTextField) loader.getNamespace().get("quantity");
                JFXTextField unit_tf = (JFXTextField) loader.getNamespace().get("unit");
                JFXTextField price_tf = (JFXTextField) loader.getNamespace().get("price");
                JFXTextField neaCode_tf = (JFXTextField) loader.getNamespace().get("neaCode");
                JFXTextArea comments_tf = (JFXTextArea) loader.getNamespace().get("comments");
                Label manuDate_lbl = (Label) loader.getNamespace().get("manuDate_lbl");
                manuDate_lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 11));
                manuDate_lbl.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
                Label valDate_lbl = (Label) loader.getNamespace().get("valDate_lbl");
                valDate_lbl.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 11));
                valDate_lbl.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

                this.bindNumbers(serialNumber_tf);
                this.bindNumbers(quantity_tf);
                this.bindNumbers(price_tf);
                this.bindStockTypes(type_cb);

                JFXButton updateBtn = (JFXButton) loader.getNamespace().get("updateBtn");
                updateBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        String name = stockName_tf.getText();
                        String brand = brand_tf.getText();
                        String model = model_tf.getText();
                        String unit = unit_tf.getText();
                        StockType stockType = (StockType) type_cb.getSelectionModel().getSelectedItem();
                        LocalDate manDate = manuDate_dp.getValue();
                        LocalDate valDate = valDate_dp.getValue();
                        String source = (String) source_cb.getSelectionModel().getSelectedItem();

                        int serialNumber = 0;
                        int quantity = 0;
                        double price = 0;

                        try {
                            quantity = Integer.parseInt(quantity_tf.getText());
                        }catch (Exception e){

                        }

                        try {
                            price = Double.parseDouble(price_tf.getText());
                        }catch (Exception e){

                        }

                        try {
                            serialNumber = Integer.parseInt(serialNumber_tf.getText());
                        }catch (Exception e){

                        }

                        Stock updated_stock = new Stock();

                        //Mandatory fields
                        updated_stock.setId(stock.getId());
                        updated_stock.setStockName(name);
                        updated_stock.setBrand(brand);
                        updated_stock.setModel(model);
                        updated_stock.setPrice(price);

                        updated_stock.setTrashed(false);
                        updated_stock.setUserIDCreated(ActiveUser.getUser().getId());
                        if (stockType != null) updated_stock.setTypeID(stockType.getId());
                        updated_stock.setUnit(unit);

                        //Optional Fields
                        updated_stock.setDescription(desc_tf.getText());
                        updated_stock.setComments(comments_tf.getText());
                        if (serialNumber > 0) updated_stock.setSerialNumber(serialNumber);
                        if (manDate != null) updated_stock.setManufacturingDate(manDate);
                        if (valDate != null) updated_stock.setValidityDate(valDate);
                        updated_stock.setNeaCode(neaCode_tf.getText());

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
                        }else if (source == null){
                            AlertDialogBuilder.messgeDialog("Invalid Input", "Please select a valid source type!",
                                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
                        }else {

                        }
                    }
                });

                TableView stockEntries_table = (TableView) loader.getNamespace().get("stockEntries_table");
                this.createStockEntriesTable(stockEntries_table);
                this.initializeStockEntries(stockEntries_table, stock);
                stockName_tf.setText(stock.getStockName());
                serialNumber_tf.setText(""+stock.getSerialNumber());
                brand_tf.setText(stock.getBrand());
                model_tf.setText(stock.getModel());
                desc_tf.setText(stock.getDescription());
                LocalDate mDate = stock.getManufacturingDate();
                LocalDate vDate = stock.getValidityDate();
                if (mDate != null)
                    manuDate_dp.setValue(mDate);
                if (vDate != null)
                    valDate_dp.setValue(vDate);
                quantity_tf.setText(""+stock.getQuantity());
                unit_tf.setText(stock.getUnit());
                price_tf.setText(""+stock.getPrice());
                neaCode_tf.setText(stock.getNeaCode());
                comments_tf.setText(stock.getComments());
                ObservableList<StockType> stocktypes = type_cb.getItems();
                int index = 0;
                for (int i=0;  i < stocktypes.size(); i++){
                    if (stocktypes.get(i).getId() == stock.getTypeID()) {
                        index = i;
                        break;
                    }
                }
                type_cb.getSelectionModel().select(index);
                JFXDialogLayout dialogLayout = new JFXDialogLayout();
                Label label = new Label("View Stock");
                label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 18));
                label.setWrapText(true);
                label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
                dialogLayout.setHeading(label);
                dialogLayout.setBody(parent);
                JFXButton cancel = new JFXButton("Close");
                cancel.setDefaultButton(true);
                cancel.setMinWidth(75);
                cancel.setOnAction(event -> dialog.close());
                dialogLayout.setActions(cancel);
                dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                dialog.show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable(){
        TableColumn select = new TableColumn("#");
        select.setMinWidth(5);
        select.setCellValueFactory((Callback<TableColumn.CellDataFeatures<SlimStock, CheckBox>, ObservableValue<CheckBox>>) arg0 -> {
            SlimStock stock = arg0.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (new_val == true) {
                    selected.put(stock.getId(), stock);
                }else{
                    selected.remove(stock.getId(), stock);
                }
            });
            return new SimpleObjectProperty<>(checkBox);
        });

        TableColumn<SlimStock, String> column1 = new TableColumn<>("Stock Name");
        column1.setMinWidth(150);
        column1.setCellValueFactory(new PropertyValueFactory<>("stockName"));

        TableColumn<SlimStock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(300);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<SlimStock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<SlimStock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<SlimStock, String> column5 = new TableColumn<>("Unit");
        column5.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<SlimStock, String> column6 = new TableColumn<>("Quantity");
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SlimStock, String> column7 = new TableColumn<>("Price");
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));

        stocksTable.getColumns().add(select);
        stocksTable.getColumns().add(column1);
        stocksTable.getColumns().add(column2);
        stocksTable.getColumns().add(column3);
        stocksTable.getColumns().add(column4);
        stocksTable.getColumns().add(column5);
        stocksTable.getColumns().add(column6);
        stocksTable.getColumns().add(column7);
    }

    public void createStockEntriesTable(TableView table){
        TableColumn<StockEntryLog, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(110);
        column1.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        TableColumn<StockEntryLog, String> column2 = new TableColumn<>("Price");
        column2.setMinWidth(110);
        column2.setCellValueFactory(new PropertyValueFactory<>("Price"));

        TableColumn<StockEntryLog, String> column3 = new TableColumn<>("Source");
        column3.setMinWidth(110);
        column3.setCellValueFactory(new PropertyValueFactory<>("source"));

        TableColumn<StockEntryLog, String> column4 = new TableColumn<>("Date Added");
        column4.setMinWidth(70);
        column4.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        table.getColumns().add(column1);
        table.getColumns().add(column2);
        table.getColumns().add(column3);
        table.getColumns().add(column4);
    }

    public void initializeStocks(){
        try {
            ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT,0, 0));
            this.stocksTable.getItems().setAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeStockEntries(TableView table, Stock stock){
        try {
            ObservableList<StockEntryLog> stocks = FXCollections.observableList(StockDAO.getEntryLogs(stock));
            table.getItems().setAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindPages(){
        try {
            COUNT = StockDAO.countStocks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double div = COUNT/LIMIT;
        double pages = Math.ceil(div);

        if (COUNT % LIMIT >0 )
            pages++;

        this.page_cb.getItems().clear();
        for (int i = 1; i <= pages; i++){
            this.page_cb.getItems().add(i);
        }

        this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            try {
                int offset = (page_cb.getSelectionModel().getSelectedItem()-1)*LIMIT;
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT, offset, 0));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void bindNumbers(JFXTextField tf){
        this.restrictNumbersOnly(tf);
    }

    public void restrictNumbersOnly(JFXTextField tf){
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+")){
                tf.setText(oldValue);
            }
        });
    }

    public void bindStockTypes(JFXComboBox type){
        try {
            List<StockType> types = StockDAO.getTypes();
            for (StockType t : types){
                type.getItems().add(t);
            }
            type.setConverter(new StringConverter<StockType>() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindSources(JFXComboBox source){
        source.getItems().add("Purchased");
        source.getItems().add("Returned");
    }

}
