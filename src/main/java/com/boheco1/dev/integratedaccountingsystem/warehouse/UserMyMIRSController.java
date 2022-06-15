package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMyMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView mirsTable;
    @FXML private JFXComboBox<String> myMIRSStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
      //  populateMirsTable();

        myMIRSStatus.getItems().add(Utility.PENDING.toUpperCase());
        myMIRSStatus.getItems().add(Utility.RELEASING.toUpperCase());
        myMIRSStatus.getItems().add(Utility.CLOSED.toUpperCase());
    }

    private void initializeMirsTable() {
        try {
            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MIRS #");
            mirsIdCol.setPrefWidth(100);
            mirsIdCol.setMaxWidth(100);
            mirsIdCol.setMinWidth(100);
            mirsIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<MIRS, String> mirsDateFiled = new TableColumn<>("Date Filed");
            mirsDateFiled.setPrefWidth(100);
            mirsDateFiled.setMaxWidth(100);
            mirsDateFiled.setMinWidth(100);
            mirsDateFiled.setCellValueFactory(new PropertyValueFactory<>("dateFiled"));

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
    private void mirsTableClicked(MouseEvent event) {
        if(event.getClickCount() == 2){
            MIRS selected = (MIRS) mirsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertDialogBuilder.messgeDialog("System Information", "Select item(s) before proceeding!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                return;
            }
            try {
                Utility.setActiveMIRS(selected);
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, "user_my_mirs_application.fxml", Utility.getContentPane(), Utility.getSubToolbar(), new Label("My MIRS Application: ")));
                //ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../user_my_mirs_application.fxml", Utility.getStackPane(), "../user_my_mirs.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void populateMirsTable() {
        try {
            ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getMyMIRS(ActiveUser.getUser().getId(), Utility.PENDING));
            mirsTable.getItems().setAll(mirs);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void viewMyMIRSByStatus(ActionEvent event) {
        try {
            ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getMyMIRS(ActiveUser.getUser().getId(), myMIRSStatus.getValue()));
            mirsTable.getItems().setAll(mirs);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
