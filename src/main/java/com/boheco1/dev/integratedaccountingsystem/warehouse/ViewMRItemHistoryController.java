package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MR;
import com.boheco1.dev.integratedaccountingsystem.objects.MrItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewMRItemHistoryController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField description_tf, stock_id_tf, brand_tf, model_tf;

    @FXML
    private TableView table;


    private MrItem mr_item = null;

    private ObservableList<MrItem> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Retrieves the selected MR object
        mr_item = (MrItem) Utility.getSelectedObject();
        //Displays MR item object
        this.setMRs();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
    /**
     * Initializes the MR item history
     * @return void
     */
    public void initializeTable() {

        TableColumn<MrItem, String> column = new TableColumn<>("MR No");
        column.setMinWidth(100);
        column.setCellValueFactory(new PropertyValueFactory<>("mrNo"));
        column.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column1 = new TableColumn<>("RR No");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<>("rrNo"));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column2 = new TableColumn<>("Quantity");
        column2.setMinWidth(75);
        column2.setCellValueFactory(new PropertyValueFactory<>("qty"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column3 = new TableColumn<>("Unit Price");
        column3.setMinWidth(100);
        column3.setCellValueFactory(item -> {
            try {

                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getPrice());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3.setStyle("-fx-alignment: center-left;");
        TableColumn<MrItem, Double> column3a = new TableColumn<>("Total");
        column3a.setMinWidth(100);
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
        column7.setMinWidth(165);
        column7.setCellValueFactory(item -> {
            if (item.getValue().getDateReturned() == null){
                return new ReadOnlyObjectWrapper<>(item.getValue().getRemarks());
            }else{
                String status = item.getValue().getRemarks()+" ("+item.getValue().getStatus()+" on "+item.getValue().getDateReturned()+")";
                return new ReadOnlyObjectWrapper<>(status);
            }
        });
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("FirstName");
        column8.setMinWidth(140);
        column8.setCellValueFactory(item -> {
            try {
                MR mr = MrDAO.get(item.getValue().getMrNo());
                return new ReadOnlyObjectWrapper<>(mr.getEmployeeInfo().getEmployeeFirstName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column8.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column9 = new TableColumn<>("LastName");
        column9.setMinWidth(140);
        column9.setCellValueFactory(item -> {
            try {
                MR mr = MrDAO.get(item.getValue().getMrNo());
                return new ReadOnlyObjectWrapper<>(mr.getEmployeeInfo().getEmployeeLastName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column9.setStyle("-fx-alignment: center-left;");

        this.mrItems =  FXCollections.observableArrayList();
        this.table.setPlaceholder(new Label("No item added"));
        this.table.getColumns().add(column);
        this.table.getColumns().add(column4);
        this.table.getColumns().add(column1);
        this.table.getColumns().add(column2);
        this.table.getColumns().add(column3);
        this.table.getColumns().add(column3a);
        this.table.getColumns().add(column8);
        this.table.getColumns().add(column9);
        this.table.getColumns().add(column7);
    }
    /**
     * Displays the MR item history
     * @return void
     */
    public void setMRs(){
        try {
            Stock stock = mr_item.getStock();
            this.description_tf.setText(stock.getDescription());
            this.stock_id_tf.setText(stock.getId());
            this.brand_tf.setText(stock.getBrand());
            this.model_tf.setText(stock.getBrand());
            this.initializeTable();
            this.populateTable();
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }

    }
    /**
     * Displays the MR item history in the table
     * @return void
     */
    public void populateTable() {

        try {
            Platform.runLater(() -> {
                try {
                    mrItems = FXCollections.observableList(MrDAO.getMRItems(mr_item.getStockID(), null));
                    this.table.getItems().setAll(mrItems);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
