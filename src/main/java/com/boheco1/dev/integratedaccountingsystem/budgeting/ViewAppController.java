package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.DeptThresholdDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.APP;
import com.boheco1.dev.integratedaccountingsystem.objects.Department;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

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
        threshCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DeptThreshold, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DeptThreshold, String> event) {
                DeptThreshold dt = event.getRowValue();
                dt.setThreshAmount(Double.parseDouble(event.getNewValue().replace(",","").replace("₱","").replace(" ",",")));
            }
        });

        TableColumn<DeptThreshold, String> apprCol = new TableColumn<>("Appropriations");
        apprCol.setCellValueFactory(new PropertyValueFactory<DeptThreshold, String>("deptAppropriationsStr"));
        apprCol.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn<DeptThreshold, DeptThreshold> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(deptThresholdDeptThresholdCellDataFeatures -> new ReadOnlyObjectWrapper<>(deptThresholdDeptThresholdCellDataFeatures.getValue()));
        actionCol.setStyle("-fx-alignment: CENTER");
        actionCol.setCellFactory(deptThresholdDeptThresholdTableColumn -> new TableCell<>(){
            final FontIcon openIcon =  new FontIcon("mdi2d-door-open");
            private final JFXButton openButton = new JFXButton("", openIcon);

            @Override
            protected void updateItem(DeptThreshold deptThreshold, boolean b) {
                super.updateItem(deptThreshold, b);
                if(deptThreshold!=null) {
                    setGraphic(openButton);
                    openButton.setStyle("-fx-background-color: #009688;");
                    openIcon.setIconSize(14);
                    openIcon.setIconColor(Color.WHITE);
                    openButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            Utility.setSelectedObject(deptThreshold);
                            Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/view_dept_summary.fxml"));
                        }
                    });
                }
            }
        });

        thresholdTable.getColumns().add(deptCol);
        thresholdTable.getColumns().add(threshCol);
        thresholdTable.getColumns().add(apprCol);
        thresholdTable.getColumns().add(actionCol);
        thresholdTable.setEditable(true);

        thresholdTable.setItems(FXCollections.observableArrayList(thresholds));

    }

    public void onSave() {
        try {
            //update app
            app.setYear(budgetYearText.getText());
            app.setBoardRes(boardResoText.getText());
            app.setTotalBudget(Double.parseDouble(cobText.getText().replace("₱","").replace(",","").replace(" ","")));

            //update threshold
            for(DeptThreshold dt: thresholds) {
                if(dt.getThresID()==null) {
                    dt.setAppID(app.getAppId());
                    DeptThresholdDAO.create(dt);
                }else {
                    DeptThresholdDAO.updateAmount(dt);
                }
            }

            AppDAO.update(app);
            AlertDialogBuilder.messgeDialog("APP Saved","The APP has been updated successfully!!!",Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error",ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
