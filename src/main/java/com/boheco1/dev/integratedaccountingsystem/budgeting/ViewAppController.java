package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.DeptThresholdDAO;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewAppController extends MenuControllerHandler implements Initializable {

    @FXML
    TextField budgetYearText;
    @FXML TextField boardResoText;
    @FXML TextField cobText;
    @FXML
    Label appropriationsLabel;
    @FXML Label remainingLabel;
    @FXML
    TableView thresholdTable;

    private APP app;

    private List<DeptThreshold> thresholds;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.app = Utility.getSelectedAPP();

        budgetYearText.setText(app.getYear());
        boardResoText.setText(app.getBoardRes());
        cobText.setText(app.getFormattedTotalBudget());

        try {
            List<Department> depts = DepartmentDAO.getAll(DB.getConnection());
            thresholds = new ArrayList<>();

            for(Department d: depts) {
                DeptThreshold dt = DeptThresholdDAO.find(app.getAppId(), d.getDepartmentID());
                System.out.println("Find " + app.getAppId() + " : " + d.getDepartmentID() + " " + dt);
                if(dt==null) {
                    dt = new DeptThreshold();
                    dt.setDepartmentID(d.getDepartmentID());
                    dt.setThreshAmount(0);
                }
                thresholds.add(dt);
            }

            renderTable();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error",ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void renderTable() {
        TableColumn<DeptThreshold, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<DeptThreshold, String>("departmentName"));

        TableColumn<DeptThreshold, String> threshCol = new TableColumn<>("Threshold");
        threshCol.setCellValueFactory(new PropertyValueFactory<DeptThreshold, String>("threshAmountStr"));
        threshCol.setCellFactory(TextFieldTableCell.forTableColumn());
        threshCol.setStyle("-fx-alignment: CENTER_RIGHT");
        threshCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DeptThreshold, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DeptThreshold, String> deptThresholdStringCellEditEvent) {

            }
        });

        TableColumn<DeptThreshold, String> apprCol = new TableColumn<>("Appropriations");
        apprCol.setCellValueFactory(new PropertyValueFactory<DeptThreshold, String>("deptAppropriationsStr"));
        apprCol.setStyle("-fx-alignment: CENTER_RIGHT");

        thresholdTable.getColumns().add(deptCol);
        thresholdTable.getColumns().add(threshCol);
        thresholdTable.getColumns().add(apprCol);
        thresholdTable.setEditable(true);

        thresholdTable.setItems(FXCollections.observableArrayList(thresholds));

    }

    public void onSave() {

    }
}
