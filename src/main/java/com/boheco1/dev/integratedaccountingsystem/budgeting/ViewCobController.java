package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.cashiering.ArSearchPayeeController;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewCobController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    Label titleLabel;
    @FXML
    TableView cobTable;
    @FXML
    ComboBox statusComboBox;
    @FXML Label totalLabel;
    JFXDialog modal;
    private COB cob;
    private List<COBItem> items;

//    private COBItem selectedItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.cob = Utility.getSelectedCOB();
            titleLabel.setText(Utility.getSelectedDeptThreshold().getDepartmentName() + " COB (" + cob.getActivity() + ")");
            this.items = cob.getItems();

            renderTable();

            computeTotals();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error!",ex.getMessage(), Utility.getStackPane(),AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void computeTotals() {
        try {
            double total = cob.resetAmount();

            totalLabel.setText(String.format("TOTAL ₱ %,.2f", total));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void renderTable() {
        TableColumn<COBItem, String> titleCol = new TableColumn<>("Account Title/Specific Activity");
        titleCol.setCellValueFactory(new PropertyValueFactory<COBItem, String>("description"));
        titleCol.setMinWidth(300);

        TableColumn<COBItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<COBItem, Integer>("qty"));
        qtyCol.setStyle("-fx-alignment: CENTER");

        TableColumn<COBItem, Double> costCol = new TableColumn<>("Unit Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<COBItem, Double>("cost"));
        costCol.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COBItem, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<COBItem, String>("amountStr"));
        amountCol.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COBItem, String> qtr1Col = new TableColumn<>("1st");
        qtr1Col.setCellValueFactory(new PropertyValueFactory<COBItem, String>("qtr1Str"));
        qtr1Col.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COBItem, String> qtr2Col = new TableColumn<>("2nd");
        qtr2Col.setCellValueFactory(new PropertyValueFactory<COBItem, String>("qtr2Str"));
        qtr2Col.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COBItem, String> qtr3Col = new TableColumn<>("3rd");
        qtr3Col.setCellValueFactory(new PropertyValueFactory<COBItem, String>("qtr3Str"));
        qtr3Col.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COBItem, String> qtr4Col = new TableColumn<>("4th");
        qtr4Col.setCellValueFactory(new PropertyValueFactory<COBItem, String>("qtr4Str"));
        qtr4Col.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COBItem, COBItem> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(cobItemCOBItemCellDataFeatures -> new ReadOnlyObjectWrapper<>(cobItemCOBItemCellDataFeatures.getValue()));
        actionCol.setStyle("-fx-alignment: CENTER");
        actionCol.setCellFactory(cobItemCOBItemTableColumn -> new TableCell<>(){
            final FontIcon openIcon =  new FontIcon("mdi2d-door-open");
            private final JFXButton openButton = new JFXButton("", openIcon);

            @Override
            protected void updateItem(COBItem cobItem, boolean b) {
                super.updateItem(cobItem, b);
                if (cobItem != null) {
                    setGraphic(openButton);
                    openButton.setStyle("-fx-background-color: #009688;");
                    openIcon.setIconSize(14);
                    openIcon.setIconColor(Color.WHITE);

                    openButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            showReviseItem(cobItem);
                        }
                    });
                }
            }
        });

        cobTable.getColumns().add(titleCol);
        cobTable.getColumns().add(qtyCol);
        cobTable.getColumns().add(costCol);
        cobTable.getColumns().add(amountCol);
        cobTable.getColumns().add(qtr1Col);
        cobTable.getColumns().add(qtr2Col);
        cobTable.getColumns().add(qtr3Col);
        cobTable.getColumns().add(qtr4Col);
        cobTable.getColumns().add(actionCol);

        cobTable.setItems(FXCollections.observableArrayList(this.items));
    }

    private void showReviseItem(COBItem cobItem) {
        Utility.setParentController(this);
        Utility.setSelectedObject(cobItem);
        modal = ModalBuilder.showModalFromXML(ArSearchPayeeController.class, "../budgeting/revise_item.fxml", Utility.getStackPane());
    }

    public void onBack() {
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/view_dept_summary.fxml"));
    }

    @Override
    public void receive(Object o) {
        try {
            this.items = cob.getItems();
            cobTable.setItems(FXCollections.observableArrayList(this.items));
            computeTotals();
            modal.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
