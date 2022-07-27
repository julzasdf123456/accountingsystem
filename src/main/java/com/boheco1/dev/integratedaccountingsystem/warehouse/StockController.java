package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileWriter;
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

    private int LIMIT = Utility.ROW_PER_PAGE;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Binds the dropdown box for pagination
        this.bindPages();
        //Sets the stocks count
        this.setStocksCount();
        //Initializes the stocks table
        this.initializeStocks();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
    /**
     * Opens the Stock Entry form
     * @return void
     */
    @FXML
    private void newStock() {
        Node node = ContentHandler.getNodeFromFxml(getClass(),"../warehouse_stock_entry.fxml");
        Utility.getContentPane().getChildren().setAll(node);
    }
    /**
     * Populates the stocks table based on search query
     * @return void
     */
    @FXML
    public void searchStock(){
        selected = new HashMap<>();
        String key = this.query_tf.getText();

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
    /**
     * Displays the View/update Stock form
     * @return void
     */
    @FXML
    public void updatePrices(){
        ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_update_prices.fxml", Utility.getStackPane(),  "../warehouse_stock.fxml");
    }
    /**
     * Generates and saves the CSV of stocks for bulk price update
     * @return void
     */
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
                    String[] header = "id,Code,Description,Model,Brand,Current Price,Updated Price".split(",");
                    writer.writeNext(header);
                    List<Stock> stocks = StockDAO.getInventory();
                    for (Stock stock : stocks) {
                        String code = "";
                        String neaCode = stock.getNeaCode();
                        if (neaCode != null && neaCode.length() != 0) {
                            code = neaCode;
                        }else{
                            code = stock.getLocalCode();
                        }
                        writer.writeNext(new String[]{stock.getId(), code, stock.getDescription(), stock.getModel(), stock.getBrand(), stock.getPrice() + "", stock.getPrice() + ""});
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

    /**
     * Sets the stocks table
     * @return void
     */
    public void createTable(){
        TableColumn<SlimStock, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(125);
        column1.setCellValueFactory(new PropertyValueFactory<>("Code"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(400);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(175);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(175);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));
        column4.setStyle("-fx-alignment: center-left;");

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
        column7.setMinWidth(100);
        column7.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, SlimStock> column8 = new TableColumn<>("Action");
        column8.setMinWidth(40);
        column8.setCellValueFactory(stock -> new ReadOnlyObjectWrapper<>(stock.getValue()));
        column8.setCellFactory(stocktable -> new TableCell<>(){
            FontIcon icon =  new FontIcon("mdi2p-pencil");
            private final JFXButton viewButton = new JFXButton("", icon);

            FontIcon trashIcon =  new FontIcon("mdi2t-trash-can-outline");
            private final JFXButton trashButton = new JFXButton("", trashIcon);

            @Override
            public void updateItem(SlimStock item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    icon.setIconSize(24);
                    icon.setIconColor(Paint.valueOf(ColorPalette.INFO));

                    trashIcon.setIconSize(24);
                    trashIcon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        try {
                            Stock stock = StockDAO.get(item.getId());
                            viewButton.setOnAction(actionEvent -> {
                                Utility.setSelectedObject(stock);
                                ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_stock_update.fxml", Utility.getStackPane());
                            });
                        } catch (Exception e) {

                        }
                        trashButton.setOnAction(trashEvent -> {
                            JFXDialogLayout dialogLayout = new JFXDialogLayout();
                            dialogLayout.setHeading(new Text("Confirm Trash Stock"));
                            Label label = new Label("Do you want to put the selected stock to recycle bin?");
                            label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
                            label.setWrapText(true);
                            label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
                            dialogLayout.setBody(label);
                            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                            JFXButton trash = new JFXButton("Trash");
                            trash.setStyle("-fx-background-color: " + ColorPalette.DANGER);
                            trash.setTextFill(Paint.valueOf("#FFFFFF"));
                            trash.setMinWidth(75);
                            trash.setOnAction(event -> {
                                try {
                                    stocksTable.getItems().remove(item);
                                    StockDAO.trash(StockDAO.get(item.getId()));
                                    AlertDialogBuilder.messgeDialog("System Information", "Stock item was put to trash!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                                    bindPages();
                                    setStocksCount();
                                    dialog.close();
                                } catch (Exception e) {
                                    AlertDialogBuilder.messgeDialog("System Error", "Failed to put item to trash due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
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
                        });

                        HBox hBox = new HBox();
                        HBox filler = new HBox();
                        hBox.setHgrow(filler, Priority.ALWAYS);
                        hBox.setSpacing(1);
                        hBox.getChildren().add(viewButton);
                        hBox.getChildren().add(filler);
                        hBox.getChildren().add(trashButton);

                        setGraphic(hBox);
                    }
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column8.setStyle("-fx-alignment: center;");

        stocksTable.getColumns().removeAll();


        stocksTable.getColumns().add(column1);
        stocksTable.getColumns().add(column2);
        stocksTable.getColumns().add(column3);
        stocksTable.getColumns().add(column4);
        stocksTable.getColumns().add(column5);
        stocksTable.getColumns().add(column6);
        stocksTable.getColumns().add(column7);
        stocksTable.getColumns().add(column8);
        this.stocksTable.setPlaceholder(new Label("No queries selected!"));
    }
    /**
     * Initializes the stocks table
     * @return void
     */
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
    /**
     * Binds the dropdown box for pagination of results
     * @return void
     */
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
    /**
     * Sets the stocks count label
     * @return void
     */
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
