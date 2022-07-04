package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class MIRSViewAllController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView mirsTable;
    @FXML private JFXTextField search_box;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
        populateMirsTable("");
    }

    private void initializeMirsTable() {
        try {
            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MIRS #");
            mirsIdCol.setStyle("-fx-alignment: center;");
            mirsIdCol.setPrefWidth(200);
            mirsIdCol.setMaxWidth(200);
            mirsIdCol.setMinWidth(200);
            mirsIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<MIRS, String> mirsDateFiled = new TableColumn<>("Date Filed");
            mirsDateFiled.setStyle("-fx-alignment: center;");
            mirsDateFiled.setPrefWidth(110);
            mirsDateFiled.setMaxWidth(110);
            mirsDateFiled.setMinWidth(110);
            mirsDateFiled.setCellValueFactory(new PropertyValueFactory<>("dateFiled"));

            TableColumn<MIRS, String> mirsStatus = new TableColumn<>("Status");
            mirsStatus.setStyle("-fx-alignment: center;");
            mirsStatus.setPrefWidth(100);
            mirsStatus.setMaxWidth(100);
            mirsStatus.setMinWidth(100);
            mirsStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            TableColumn<MIRS, String> purposeCol = new TableColumn<>("Purpose");
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));

            mirsTable.getColumns().removeAll();
            mirsTable.getColumns().add(mirsIdCol);
            mirsTable.getColumns().add(mirsDateFiled);
            mirsTable.getColumns().add(mirsStatus);
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
                ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_mirs_preview.fxml", Utility.getStackPane(), "../warehouse_mirs_view_all.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void searchMIRS(ActionEvent event) {
        populateMirsTable(search_box.getText());
    }

    @FXML
    private void populateMirsTable(String q) {
        try {
            if(q.equals("")) {
                ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getAllMIRS());
                mirsTable.getItems().setAll(mirs);
            }else {
                ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.searchMIRS(q));
                mirsTable.getItems().setAll(mirs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
