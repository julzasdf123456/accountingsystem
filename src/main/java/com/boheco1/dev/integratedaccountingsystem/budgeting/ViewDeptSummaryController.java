package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.CobDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import jdk.jfr.Event;
import org.kordamp.ikonli.javafx.FontIcon;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewDeptSummaryController extends MenuControllerHandler implements Initializable {

    @FXML
    Label titleLabel;
    @FXML
    TableView summaryTable;

    @FXML Label thresholdLabel;
    @FXML Label appropriationsLabel;
    @FXML Label differenceLabel;

    private DeptThreshold selectedDepartment;
    private List<COB> cobs;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            selectedDepartment = Utility.getSelectedDeptThreshold();
            titleLabel.setText(selectedDepartment.getDepartmentName() + " Budget Summary");

            cobs = CobDAO.getAll(selectedDepartment.getDepartmentID());

            renderTable();
            updateComputations();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error!",ex.getMessage(),Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void renderTable() {
        TableColumn<COB, String> cobIdCol = new TableColumn<>("COB ID");
        cobIdCol.setCellValueFactory(new PropertyValueFactory<COB, String>("cobId"));

        TableColumn<COB, String> activityCol = new TableColumn<>("Major Activity");
        activityCol.setCellValueFactory(new PropertyValueFactory<COB, String>("activity"));
        activityCol.setMinWidth(250.0);

        TableColumn<COB, String> cobTypeCol = new TableColumn<>("COB Type");
        cobTypeCol.setCellValueFactory(new PropertyValueFactory<COB, String>("type"));

        TableColumn<COB, String> appropCol = new TableColumn<>("Appropriation");
        appropCol.setCellValueFactory(new PropertyValueFactory<COB, String>("appropriationStr"));
        appropCol.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<COB, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<COB, String>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");

        TableColumn<COB, EmployeeInfo> prepCol = new TableColumn<>("Prepared by");
        prepCol.setCellValueFactory(new PropertyValueFactory<COB, EmployeeInfo>("prepared"));
        prepCol.setStyle("-fx-alignment: CENTER");

        TableColumn<COB, COB> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(cobcobCellDataFeatures -> new ReadOnlyObjectWrapper<>(cobcobCellDataFeatures.getValue()));
        actionCol.setStyle("-fx-alignment: CENTER");

        actionCol.setCellFactory(cobcobTableColumn -> new TableCell<>(){
            final FontIcon openIcon =  new FontIcon("mdi2d-door-open");
            private final JFXButton openButton = new JFXButton("", openIcon);

            @Override
            protected void updateItem(COB cob, boolean b) {
                super.updateItem(cob, b);
                if(cob!=null) {
                    openButton.setStyle("-fx-background-color: #009688;");
                    openIcon.setIconSize(14);
                    openIcon.setIconColor(Color.WHITE);
                    setGraphic(openButton);

                    openButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            Utility.setSelectedObject(cob);
                            Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/budgeting_cob_approval.fxml"));
                        }
                    });
                }
            }
        });

        summaryTable.getColumns().add(cobIdCol);
        summaryTable.getColumns().add(activityCol);
        summaryTable.getColumns().add(cobTypeCol);
        summaryTable.getColumns().add(appropCol);
        summaryTable.getColumns().add(statusCol);
        summaryTable.getColumns().add(prepCol);
        summaryTable.getColumns().add(actionCol);

        summaryTable.setItems(FXCollections.observableArrayList(cobs));
    }

    private void updateComputations() throws Exception {
        thresholdLabel.setText(selectedDepartment.getThreshAmountStr());
        appropriationsLabel.setText(selectedDepartment.getDeptAppropriationsStr());
        double diff = selectedDepartment.getThreshAmount()-selectedDepartment.getDeptAppropriations();
        differenceLabel.setText(String.format("₱ %,.2f",diff));
        if(diff<0) {
            differenceLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16");
        }else {
            differenceLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16");
        }
    }

    public void onBack() {
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/view_app.fxml"));
    }

}
