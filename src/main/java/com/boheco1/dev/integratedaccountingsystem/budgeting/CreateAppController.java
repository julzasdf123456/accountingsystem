package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.APP;
import com.boheco1.dev.integratedaccountingsystem.objects.Department;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateAppController extends MenuControllerHandler implements Initializable {

    @FXML
    TextField budgetYearText;
    @FXML TextField boardResoText;
    @FXML TextField cobText;
    @FXML TableView thresholdTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            List<Department> depts = DepartmentDAO.getAll(DB.getConnection());
            List<DeptThreshold> thresholds = new ArrayList<>();
            for(Department d: depts) {
                thresholds.add(
                        new DeptThreshold("",0, d.getDepartmentID(), null)
                );
            }

            renderTable(thresholds);

        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error", ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void renderTable(List<DeptThreshold> thresholds) {
        TableColumn<DeptThreshold, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<DeptThreshold, String>("departmentName"));


        TableColumn<DeptThreshold, Double> threshCol = new TableColumn<>("Threshold");
        threshCol.setCellValueFactory(new PropertyValueFactory<DeptThreshold, Double>("threshAmount"));
        threshCol.setStyle("-fx-alignment: CENTER_RIGHT");
        threshCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        threshCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DeptThreshold, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DeptThreshold, Double> event) {
                DeptThreshold dt = event.getRowValue();
                dt.setThreshAmount(event.getNewValue());
            }
        });

        thresholdTable.setEditable(true);
        thresholdTable.getColumns().add(deptCol);
        thresholdTable.getColumns().add(threshCol);

        thresholdTable.setItems(FXCollections.observableArrayList(thresholds));
    }

    public void onSave() {
        String msg = validate();
        if(!msg.isEmpty()) {
            AlertDialogBuilder.messgeDialog("Data Entry Error", msg, Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        APP app = new APP();
        app.setYear(budgetYearText.getText());
        app.setBoardRes(boardResoText.getText());
        app.setTotalBudget(Double.parseDouble(cobText.getText().replace(",","")));


    }

    private String validate() {
        if(budgetYearText.getText().isEmpty() || boardResoText.getText().isEmpty() || cobText.getText().isEmpty())
            return "Please do not leave any of the fields blank.";

        try {
            int year = Integer.parseInt(budgetYearText.getText());
        }catch(NumberFormatException ex) {
            return "Please enter a valid number for Budget Year";
        }

        try {
            Double amount = Double.parseDouble(cobText.getText().replace(",",""));
        }catch(NumberFormatException ex) {
            return "Please enter a valid amount for the COB";
        }

        return "";
    }

}
