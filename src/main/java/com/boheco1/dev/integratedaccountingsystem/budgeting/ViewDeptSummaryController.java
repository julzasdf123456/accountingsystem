package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;


import java.net.URL;
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            selectedDepartment = (DeptThreshold) Utility.getSelectedObject();
            titleLabel.setText(selectedDepartment.getDepartmentName() + " Budget Summary");
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error!",ex.getMessage(),Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onBack() {
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/view_app.fxml"));
    }

}
