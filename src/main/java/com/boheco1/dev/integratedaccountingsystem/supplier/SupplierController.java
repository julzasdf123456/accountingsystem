package com.boheco1.dev.integratedaccountingsystem.supplier;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.SupplierInfo;
import com.boheco1.dev.integratedaccountingsystem.warehouse.WarehouseDashboardController;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SupplierController extends MenuControllerHandler implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField query_tf;

    @FXML
    private TableView<SupplierInfo> suppliersTable;

    @FXML
    private JFXComboBox<String> page_cb;

    private JFXDialog dialog;

    @FXML
    void search(ActionEvent event) {
        String key = this.query_tf.getText();

        Platform.runLater(() -> {
            try {
                ObservableList<SupplierInfo> stocks = FXCollections.observableList(SupplierDAO.search(key, page_cb.getSelectionModel().getSelectedItem()));
                if (this.suppliersTable.getItems() != null){
                    this.suppliersTable.getItems().removeAll();
                }
                this.suppliersTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();

        this.page_cb.getItems().add("Active");
        this.page_cb.getItems().add("Trashed");

        this.page_cb.getSelectionModel().select(0);

        this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            Platform.runLater(() -> {
                try {
                    if (!page_cb.getSelectionModel().isEmpty()) {
                        ObservableList<SupplierInfo> suppliers = FXCollections.observableList(SupplierDAO.search(this.query_tf.getText(), newValue));
                        this.suppliersTable.getItems().setAll(suppliers);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * Sets the supplier table
     * @return void
     */
    public void createTable(){
        TableColumn<SupplierInfo, String> column1 = new TableColumn<>("Company Name");
        column1.setMinWidth(275);
        column1.setMaxWidth(275);
        column1.setPrefWidth(275);
        column1.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<SupplierInfo, String> column2 = new TableColumn<>("Company Address");
        column2.setMinWidth(375);
        column2.setMaxWidth(375);
        column2.setPrefWidth(375);
        column2.setCellValueFactory(new PropertyValueFactory<>("companyAddress"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<SupplierInfo, String> column3 = new TableColumn<>("Supplier Nature");
        column3.setMinWidth(275);
        column3.setMaxWidth(275);
        column3.setPrefWidth(275);
        column3.setCellValueFactory(new PropertyValueFactory<>("supplierNature"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<SupplierInfo, String> column4 = new TableColumn<>("Tax Type");
        column4.setMinWidth(75);
        column4.setMaxWidth(75);
        column4.setPrefWidth(75);
        column4.setCellValueFactory(new PropertyValueFactory<>("taxType"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<SupplierInfo, SupplierInfo> column8 = new TableColumn<>("Action");
        column8.setMinWidth(100);
        column8.setMaxWidth(100);
        column8.setPrefWidth(100);
        column8.setCellValueFactory(supplier -> new ReadOnlyObjectWrapper<>(supplier.getValue()));
        column8.setCellFactory(stocktable -> new TableCell<>(){
            FontIcon icon =  new FontIcon("mdi2p-pencil");
            private final JFXButton viewButton = new JFXButton("", icon);

            FontIcon trashIcon =  new FontIcon("mdi2t-trash-can-outline");
            private final JFXButton trashButton = new JFXButton("", trashIcon);

            @Override
            public void updateItem(SupplierInfo item, boolean empty) {
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
                            SupplierInfo supplier = SupplierDAO.get(item.getSupplierID());
                            viewButton.setOnAction(actionEvent -> {
                                Utility.setSelectedObject(supplier);
                                ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../supplier/update_record_layout.fxml", Utility.getStackPane());
                            });
                        } catch (Exception e) {

                        }
                        trashButton.setOnAction(trashEvent -> {
                            JFXDialogLayout dialogLayout = new JFXDialogLayout();
                            String title = "Confirm Trash Record";
                            String msg = "Do you want to put the selected supplier record to recycle bin?";
                            String btnlabel = "Trash";
                            String success = "Process was successfully completed!";
                            if (item.getStatus().equals("Trashed")) {
                                title = "Confirm Recover Record";
                                msg = "Do you want to activate the selected supplier record?";
                                btnlabel = "Recover";
                            }

                            dialogLayout.setHeading(new Text(title));
                            Label label = new Label(msg);
                            label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
                            label.setWrapText(true);
                            label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
                            dialogLayout.setBody(label);
                            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                            JFXButton trash = new JFXButton(btnlabel);
                            trash.setStyle("-fx-background-color: " + ColorPalette.DANGER);
                            trash.setTextFill(Paint.valueOf("#FFFFFF"));
                            trash.setMinWidth(75);
                            trash.setOnAction(event -> {
                                try {
                                    suppliersTable.getItems().remove(item);
                                    if (item.getStatus().equals("Trashed")) {
                                        SupplierDAO.recover(SupplierDAO.get(item.getSupplierID()));
                                    }else{
                                        SupplierDAO.trash(SupplierDAO.get(item.getSupplierID()));
                                    }
                                    AlertDialogBuilder.messgeDialog("System Information", success, stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                                    dialog.close();
                                } catch (Exception e) {
                                    AlertDialogBuilder.messgeDialog("System Error", "Process Failed due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
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
                        if (!item.getStatus().equals("Trashed")) {
                            hBox.getChildren().add(viewButton);
                            hBox.getChildren().add(filler);
                        }
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

        suppliersTable.getColumns().removeAll();

        suppliersTable.getColumns().add(column1);
        suppliersTable.getColumns().add(column2);
        suppliersTable.getColumns().add(column3);
        suppliersTable.getColumns().add(column4);
        suppliersTable.getColumns().add(column8);
        this.suppliersTable.setPlaceholder(new Label("No queries selected!"));
    }

    @FXML
    void addRecord(ActionEvent event) {
        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../supplier/record_layout.fxml", Utility.getStackPane());
    }

}
