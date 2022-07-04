package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class UserTaskApprovalController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView mirsTable;
    @FXML private JFXTextField remarks;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
        ObservableList<MIRSSignatory> mirs = null;
        try {
            mirs = FXCollections.observableList(MIRSSignatoryDAO.getAllMIRS(ActiveUser.getUser()));
            //mirs = FXCollections.observableList(MirsDAO.getAllMIRS());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mirsTable.getItems().setAll(mirs);

    }

    private void initializeMirsTable() {
        try {
            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MIRS #");
            mirsIdCol.setStyle("-fx-alignment: center;");
            mirsIdCol.setPrefWidth(200);
            mirsIdCol.setMaxWidth(200);
            mirsIdCol.setMinWidth(200);
            mirsIdCol.setCellValueFactory(new PropertyValueFactory<>("mirsID"));

            TableColumn<MIRS, String> mirsDateFiled = new TableColumn<>("Date Filed");
            mirsDateFiled.setStyle("-fx-alignment: center;");
            mirsDateFiled.setPrefWidth(150);
            mirsDateFiled.setMaxWidth(150);
            mirsDateFiled.setMinWidth(150);
            mirsDateFiled.setCellValueFactory(new PropertyValueFactory<>("mirsdateFiled"));

            TableColumn<MIRS, String> purposeCol = new TableColumn<>("Purpose");
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));

            mirsTable.getColumns().removeAll();
            mirsTable.getColumns().add(mirsIdCol);
            mirsTable.getColumns().add(mirsDateFiled);
            mirsTable.getColumns().add(purposeCol);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error initializing table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void mirsTableClicked(MouseEvent event) throws Exception {
        if(event.getClickCount() == 2){
            MIRSSignatory temp = (MIRSSignatory) mirsTable.getSelectionModel().getSelectedItem();
            MIRS selected = MirsDAO.getMIRS(temp.getMirsID());
            if (selected == null) {
                AlertDialogBuilder.messgeDialog("System Information", "Select item(s) before proceeding!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                return;
            }
            try {
                Utility.setActiveMIRS(selected);
                ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../user_mirs_preview.fxml", Utility.getStackPane(), "../user_task_approval_mirs.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
