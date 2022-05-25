package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class StockController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView<SlimStock> stocksTable;

    @FXML
    private Label num_stocks_lbl;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private JFXComboBox<Integer> page_cb;

    private HashMap<String, SlimStock> selected = new HashMap<>();

    private JFXDialog dialog;

    private int LIMIT = HomeController.ROW_PER_PAGE;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bindPages();
        this.setStocksCount();
        this.initializeStocks();
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
            AlertDialogBuilder.messgeDialog("System Information", "Please enter search string before proceeding!", stackPane, AlertDialogBuilder.INFO_DIALOG);
        }else {
            Platform.runLater(() -> {
                try {
                    ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.search(key, 0));
                    if (this.stocksTable.getItems() != null){
                        this.stocksTable.getItems().removeAll();
                    }
                    this.stocksTable.getItems().setAll(stocks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void updatePrices(){
        ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_update_prices.fxml", Utility.getStackPane(),  "../warehouse_stock.fxml");
    }

    @FXML
    public void downloadCSV(){
        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("StockPricesTemplate.csv");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            Platform.runLater(() -> {
                try {
                    CSVWriter writer = new CSVWriter(new FileWriter(selectedFile));
                    String[] header = "id,StockName,Description,Current Price,Updated Price".split(",");
                    writer.writeNext(header);
                    List<Stock> stocks = StockDAO.getInventory();
                    for (Stock stock : stocks) {
                        writer.writeNext(new String[]{stock.getId(), stock.getStockName(), stock.getDescription(), stock.getPrice() + "", stock.getPrice() + ""});
                    }
                    writer.close();
                    if (selectedFile.exists()) {
                        String path = selectedFile.getAbsolutePath();
                        Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
                        p.waitFor();
                    } else {
                        AlertDialogBuilder.messgeDialog("System Error", "The CSV file does not exists!", stackPane, AlertDialogBuilder.DANGER_DIALOG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the CSV due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        }
    }

    @FXML
    public void trashStock(){
        if (selected.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Information", "Select item(s) before proceeding!", stackPane, AlertDialogBuilder.INFO_DIALOG);
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
                    setStocksCount();
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
            AlertDialogBuilder.messgeDialog("System Information", "Select an item before proceeding!", stackPane, AlertDialogBuilder.INFO_DIALOG);
        }else {
            try {
                Collection<SlimStock> sel = selected.values();
                Object selected_items[] = sel.toArray();
                SlimStock slim_stock = (SlimStock) selected_items[0];
                Stock stock = StockDAO.get(slim_stock.getId());
                Utility.setSelectedStock(stock);
                ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_stock_update.fxml", Utility.getStackPane(),  "../warehouse_stock.fxml");
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
        select.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column1 = new TableColumn<>("Stock Name");
        column1.setMinWidth(150);
        column1.setCellValueFactory(new PropertyValueFactory<>("stockName"));

        TableColumn<SlimStock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(300);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<SlimStock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(175);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<SlimStock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(175);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<SlimStock, String> column5 = new TableColumn<>("Unit");
        column5.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column5.setMinWidth(75);
        column5.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column6 = new TableColumn<>("Quantity");
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column6.setMinWidth(75);
        column6.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column7 = new TableColumn<>("Price");
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        column7.setMinWidth(75);

        stocksTable.getColumns().removeAll();

        stocksTable.getColumns().add(select);
        stocksTable.getColumns().add(column1);
        stocksTable.getColumns().add(column2);
        stocksTable.getColumns().add(column3);
        stocksTable.getColumns().add(column4);
        stocksTable.getColumns().add(column5);
        stocksTable.getColumns().add(column6);
        stocksTable.getColumns().add(column7);
    }

    public void initializeStocks(){
        Platform.runLater(() -> {
            try {
                this.createTable();
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT, 0, 0));
                if (this.stocksTable.getItems() != null){
                    this.stocksTable.getItems().removeAll();
                }
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void bindPages(){
        Platform.runLater(() -> {
            try {
                COUNT = StockDAO.countStocks();
            } catch (Exception e) {
                e.printStackTrace();
            }

            double div = COUNT / LIMIT;
            double pages = Math.ceil(div);

            if (COUNT % LIMIT > 0)
                pages++;

            this.page_cb.getItems().clear();
            for (int i = 1; i <= pages; i++) {
                this.page_cb.getItems().add(i);
            }

            this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    try {
                        if (!page_cb.getSelectionModel().isEmpty()) {
                            int offset = (page_cb.getSelectionModel().getSelectedItem() - 1) * LIMIT;
                            ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT, offset, 0));
                            this.stocksTable.getItems().setAll(stocks);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private void setStocksCount() {
        Platform.runLater(() -> {
            try {
                this.num_stocks_lbl.setText(StockDAO.countStocks() + " rows");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
