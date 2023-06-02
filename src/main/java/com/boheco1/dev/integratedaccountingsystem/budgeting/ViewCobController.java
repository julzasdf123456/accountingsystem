package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewCobController extends MenuControllerHandler implements Initializable {

    @FXML
    Label titleLabel;
    @FXML
    TableView cobTable;
    @FXML
    ComboBox statusComboBox;

    private COB cob;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.cob = Utility.getSelectedCOB();
            titleLabel.setText(Utility.getSelectedDeptThreshold().getDepartmentName() + " COB (" + cob.getActivity() + ")");
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error!",ex.getMessage(), Utility.getStackPane(),AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onBack() {
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/view_dept_summary.fxml"));
    }
}
