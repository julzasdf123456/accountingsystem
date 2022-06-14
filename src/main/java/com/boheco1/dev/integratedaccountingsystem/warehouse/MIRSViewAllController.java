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
    @FXML private JFXComboBox<Integer> month;
    @FXML private JFXTextField year;
    @FXML private DatePicker dateSummary;
    @FXML private JFXButton daily_summary_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
        populateMirsTable("");
        Calendar cal = Calendar.getInstance();
        year.setText(""+cal.get(Calendar.YEAR));

        for(int i = 1; i <=12;i++){
            month.getItems().add(i);
        }
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

            TableColumn<MIRS, String> mirsStatus = new TableColumn<>("Status");
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
    private void monthly_summary_btn(ActionEvent event) {
        try {
            ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getByMonthYear(year.getText() +"-"+ month.getSelectionModel().getSelectedItem()));
            if (mirs.size() == 0) {
                AlertDialogBuilder.messgeDialog("System Information", "No record on selected month and year.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                return;
            }
            new PrintMIRSMonthlyChargeSummary(mirs);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void daily_summary_btn(ActionEvent event) {
        try {
            ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getByDateFiled(dateSummary.getValue()));
            // allMirsTable.getItems().clear();
            //allMirsTable.getItems().setAll(mirs);
            if (mirs.size() == 0) {
                AlertDialogBuilder.messgeDialog("System Information", "No record on selected date.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                return;
            }
            new PrintMIRSMonthlyChargeSummary(mirs);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

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
