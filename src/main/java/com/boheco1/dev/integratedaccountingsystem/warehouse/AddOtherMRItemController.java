package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MrItem;
import com.boheco1.dev.integratedaccountingsystem.objects.ReceivingItem;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AddOtherMRItemController extends MenuControllerHandler implements Initializable {


    private StackPane stackPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField stock_tf, qty_to_mr_tf, remarks_tf, item_name_tf, description_tf, property_no_tf, rrno_tf, cost_tf;

    @FXML
    private TableView stockTable;

    private MrItem currentMRItem = null;

    private ObservableList<MrItem> stockItems = null;

    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initializes the stocks table
        this.createTable();
        //Enables the textfield to accept numbers only
        this.bindNumbers();
        //Initializes the variables
        this.reset();
        //Retrieves the parent controller so that the MR Item object can be passed to it
        this.parentController = Utility.getParentController();
        //Sets the stackpane for the dialogs/modals to display
        this.stackPane = Utility.getStackPane();
        //Add table listener for click MR items after searching
        stockTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                MrItem selectedRow = (MrItem) stockTable.getSelectionModel().getSelectedItem();
                this.item_name_tf.setText(selectedRow.getItemName());
                this.description_tf.setText(selectedRow.getDescription());
                this.cost_tf.setText(selectedRow.getPrice()+"");
                this.qty_to_mr_tf.setText(selectedRow.getQty()+"");
                if (selectedRow.getPropertyNo() != null)
                    this.property_no_tf.setText(selectedRow.getPropertyNo());
                if (selectedRow.getRrNo() != null)
                    this.property_no_tf.setText(selectedRow.getRrNo());

                String from = "";
                if (selectedRow.getDateReturned() != null) {
                    try {
                        from = " (ref MR No: "+selectedRow.getMrNo()+")";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                this.remarks_tf.setText(selectedRow.getRemarks()+from);
            }
        });
    }
    /**
     * Creates the MR item based on the field values and adds it to the parent controller table
     * @return void
     */
    @FXML
    public void addMRItem(){
        String itemName = this.item_name_tf.getText();
        String description = this.description_tf.getText();
        String cost_str = this.cost_tf.getText();
        String remarks = this.remarks_tf.getText();
        String property_no = this.property_no_tf.getText();
        String rrno = this.rrno_tf.getText();

        int qty_to_add = 0;
        double cost = 0;
        try {
            qty_to_add = Integer.parseInt(this.qty_to_mr_tf.getText());
            cost = Double.parseDouble(cost_str);
        }catch (Exception e){

        }

        Object selectedItem = this.stockTable.getSelectionModel().getSelectedItem();

        if (selectedItem instanceof MrItem)
            this.currentMRItem = (MrItem) selectedItem;

        if (qty_to_add <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for MR quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (cost <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for item cost!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (itemName.length() == 0 || itemName == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid item name!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (description.length() == 0 || description == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid description!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (remarks.length() == 0 || remarks == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid remarks!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (selectedItem != null
                && (!itemName.equals(this.currentMRItem.getItemName())
                || !description.equals(this.currentMRItem.getDescription())
                || cost != this.currentMRItem.getPrice()
                || (this.currentMRItem.getPropertyNo() != null && !property_no.equals(this.currentMRItem.getPropertyNo()))
                || (this.currentMRItem.getRrNo() != null && !rrno.equals(this.currentMRItem.getRrNo()))
                || qty_to_add != this.currentMRItem.getQty())
        ){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please the item details entered does not match the details of the existing MR Item!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else {
            try {
                MrItem item = new MrItem();
                item.setItemName(itemName);
                item.setDescription(description);
                item.setPrice(cost);
                item.setQty(qty_to_add);
                item.setRemarks(remarks);
                item.setPropertyNo(property_no);
                if (rrno.length() != 0) item.setRrNo(rrno);
                this.parentController.receive(item);
                this.reset();
            } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "A system error occurred due to: " + e.getMessage(),
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }
    }
    /**
     * Initializes the stocks table
     * @return void
     */
    public void createTable(){
        TableColumn<MrItem, String> column0 = new TableColumn<>("Property No");
        column0.setMinWidth(100);
        column0.setCellValueFactory(new PropertyValueFactory<>("propertyNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column1 = new TableColumn<>("Item Name");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(345);
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("Qty");
        column8.setMinWidth(25);
        column8.setCellValueFactory(new PropertyValueFactory<>("qty"));
        column8.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column4 = new TableColumn<>("Price");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("Price"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column5 = new TableColumn<>("Total");
        column5.setMinWidth(100);
        column5.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrice()*item.getValue().getQty()+""));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column6 = new TableColumn<>("Remarks");
        column6.setMinWidth(174);
        column6.setCellValueFactory(item -> {
            String remarks = item.getValue().getRemarks();
            try {
                if (remarks.equals(Utility.MR_RETURNED))
                    remarks += " (returned by: "+ UserDAO.get(MrDAO.get(item.getValue().getMrNo()).getEmployeeInfo().getFullName())+" on "+item.getValue().getDateReturned()+")";
                return new ReadOnlyObjectWrapper<>(remarks);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column6.setStyle("-fx-alignment: center;");

        this.stockItems =  FXCollections.observableArrayList();
        this.stockTable.setPlaceholder(new Label("No stock added"));

        this.stockTable.getColumns().add(column0);
        this.stockTable.getColumns().add(column1);
        this.stockTable.getColumns().add(column3);
        this.stockTable.getColumns().add(column8);
        this.stockTable.getColumns().add(column4);
        this.stockTable.getColumns().add(column5);
        this.stockTable.getColumns().add(column6);
    }
    /**
     * Sets the textfields to accepts number inputs only
     * @return void
     */
    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.qty_to_mr_tf);
        InputHelper.restrictNumbersOnly(this.cost_tf);
    }
    /**
     * Search for stocks based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void search(){
        String key = this.stock_tf.getText();
        try {
            this.stockItems = FXCollections.observableArrayList(MrDAO.searchAvailableMRItem(key, Utility.MR_RETURNED));
            this.stockTable.setItems(this.stockItems);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Resets all the field values
     * @return void
     */
    @FXML
    public void reset(){
        this.stockItems =  FXCollections.observableArrayList();
        this.stockTable.setItems(this.stockItems);
        this.stockTable.setPlaceholder(new Label("No item added!"));
        this.stock_tf.setText("");
        this.qty_to_mr_tf.setText("");
        this.remarks_tf.setText("");
        this.cost_tf.setText("");
        this.item_name_tf.setText("");
        this.description_tf.setText("");
        this.property_no_tf.setText("");
        this.rrno_tf.setText("");
        this.currentMRItem = null;
    }
}
