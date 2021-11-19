package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

public class TrashStockController extends MenuControllerHandler implements Initializable, SubMenuHelper {

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
        this.initializeTrashStocks();
        this.bindPages();
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
        if (key.length() == 0) {
            this.initializeTrashStocks();
        }else {
            try {
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.search(key, 1));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void restoreTrash(){
        if (selected.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Warning", "Select item(s) before proceeding!", stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else {

            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setHeading(new Text("Confirm Trash Stock(s)"));
            Label label = new Label("Do you want to restore the selected stock(s) from recycle bin?");
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
                Collection<SlimStock> sel = selected.values();
                Object selected_items[] = sel.toArray();
                boolean ok = true;
                for (int i = 0; i < selected.size(); i++) {
                    SlimStock stock = (SlimStock) selected_items[i];
                    try {
                        stocksTable.getItems().remove(stock);
                        StockDAO.restore(StockDAO.get(stock.getId()));
                    } catch (Exception e) {
                        AlertDialogBuilder.messgeDialog("System Error", "Failed to restore trashed stock(s) due to: "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                        ok = false;
                        break;
                    }
                }
                if (ok){
                    AlertDialogBuilder.messgeDialog("System Information", "Stock item(s) were restored from trash!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
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

    public void createTable(){
        TableColumn select = new TableColumn("#");
        select.setMinWidth(5);
        select.setCellValueFactory((Callback<TableColumn.CellDataFeatures<SlimStock, CheckBox>, ObservableValue<CheckBox>>) arg0 -> {
            SlimStock stock = arg0.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (new_val) {
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

    public void initializeTrashStocks(){
        try {
            ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT,0, 1));
            this.stocksTable.getItems().setAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindPages(){

        try {
            COUNT = StockDAO.countTrashed();
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
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT, offset, 1));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
