package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

public class TrashStockController extends MenuControllerHandler implements Initializable, SubMenuHelper {

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
        this.bindPages();
        this.setTrashCount();
        this.initializeTrashStocks();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    @FXML
    public void searchStock(){
        selected = new HashMap<>();
        String key = this.query_tf.getText();

        Platform.runLater(() -> {
                try {
                    ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.search(key, 1));
                    this.stocksTable.getItems().setAll(stocks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    public void createTable(){
        TableColumn<SlimStock, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(125);
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));
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
        column5.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column6 = new TableColumn<>("Quantity");
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column6.setMinWidth(75);
        column6.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column7 = new TableColumn<>("Price");
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        column7.setMinWidth(100);
        column7.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, SlimStock> column8 = new TableColumn<>("Action");
        column8.setMinWidth(50);
        column8.setCellValueFactory(stock -> new ReadOnlyObjectWrapper<>(stock.getValue()));
        column8.setCellFactory(stocktable -> new TableCell<>(){
            FontIcon icon =  new FontIcon("mdi2f-file-document-outline");
            private final JFXButton restoreButton = new JFXButton("", icon);

            @Override
            public void updateItem(SlimStock item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    restoreButton.setStyle("-fx-background-color: #00AD8E;");
                    icon.setIconSize(13);
                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    restoreButton.setOnAction(actionEvent -> {
                        JFXDialogLayout dialogLayout = new JFXDialogLayout();
                        dialogLayout.setHeading(new Text("Confirm Trash Stock"));
                        Label label = new Label("Do you want to restore the selected stock from recycle bin?");
                        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
                        label.setWrapText(true);
                        label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
                        dialogLayout.setBody(label);
                        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                        JFXButton trash = new JFXButton("Restore");
                        trash.setStyle("-fx-background-color: "+ColorPalette.INFO);
                        trash.setTextFill(Paint.valueOf("#FFFFFF"));
                        trash.setMinWidth(75);
                        trash.setOnAction(event -> {
                            try {
                                stocksTable.getItems().remove(item);
                                StockDAO.restore(StockDAO.get(item.getId()));
                                AlertDialogBuilder.messgeDialog("System Information", "Stock item were restored from trash!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                                bindPages();
                                setTrashCount();
                                dialog.close();
                            } catch (Exception e) {
                                AlertDialogBuilder.messgeDialog("System Error", "Failed to restore trashed stock due to: "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
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
                    setGraphic(restoreButton);
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
        this.stocksTable.setPlaceholder(new Label("No stock items found in trash!"));
    }

    public void initializeTrashStocks(){
        Platform.runLater(() -> {
            try {
                this.createTable();
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT,0, 1));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void bindPages(){
        Platform.runLater(() -> {
            try {
                COUNT = StockDAO.countTrashed();
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
                            ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT, offset, 1));
                            this.stocksTable.getItems().setAll(stocks);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private void setTrashCount() {
        Platform.runLater(() -> {
            try {
                this.num_stocks_lbl.setText(StockDAO.countTrashed()+" rows");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
