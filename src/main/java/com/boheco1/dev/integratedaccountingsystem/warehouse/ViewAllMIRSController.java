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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ViewAllMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView allMirsTable;
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

            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MIRS Number");
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
            //purposeCol.setMinWidth(500);
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));

            /*TableColumn<MIRS, MIRS> actionCol = new TableColumn<>("Action");
            actionCol.setStyle("-fx-alignment: center;");
            // ADD ACTION BUTTONS
            actionCol.setCellValueFactory(mirsmirsCellDataFeatures -> new ReadOnlyObjectWrapper<>(mirsmirsCellDataFeatures.getValue()));
            actionCol.setCellFactory(mirsmirsTableColumn -> new TableCell<>(){
                FontIcon viewIcon =  new FontIcon("mdi2e-eye");
                private final JFXButton viewButton = new JFXButton("", viewIcon);

                FontIcon printIcon =  new FontIcon("mdi2p-printer");
                private final JFXButton printButton = new JFXButton("", printIcon);

                @Override
                protected void updateItem(MIRS mirs, boolean b) {
                    super.updateItem(mirs, b);

                    if (mirs != null) {
                        viewButton.setStyle("-fx-background-color: #2196f3;");
                        viewIcon.setIconSize(13);
                        viewIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        printButton.setStyle("-fx-background-color: #ff9800;");
                        printIcon.setIconSize(13);
                        printIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        viewButton.setOnAction(actionEvent -> {
                            try {
                                Utility.setActiveMIRS(mirs);
                                ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_mirs_preview.fxml", Utility.getStackPane(), "../view_all_mirs_controller.fxml");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        printButton.setOnAction(actionEvent -> {
                            try {
                                new PrintMIRS(mirs);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });


                        HBox hBox = new HBox();
                        hBox.setSpacing(2);
                        hBox.getChildren().add(viewButton);
                        hBox.getChildren().add(printButton);

                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                        return;
                    }
                }
            });*/

            allMirsTable.getColumns().removeAll();
            allMirsTable.getColumns().add(mirsIdCol);
            allMirsTable.getColumns().add(mirsDateFiled);
            allMirsTable.getColumns().add(mirsStatus);
            allMirsTable.getColumns().add(purposeCol);
            //allMirsTable.getColumns().add(actionCol);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error initializing table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }


    @FXML
    private void searchMIRS(ActionEvent event) {
        populateMirsTable(search_box.getText());
    }

    @FXML
    private void viewMIRS(ActionEvent event) {
        MIRS selected = (MIRS) allMirsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertDialogBuilder.messgeDialog("System Information", "Select item(s) before proceeding!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
            return;
        }
            try {
            Utility.setActiveMIRS(selected);
            ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_mirs_preview.fxml", Utility.getStackPane(), "../view_all_mirs_controller.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void monthly_summary_btn(ActionEvent event) {
        try {
            ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getByMonthYear(year.getText() +"-"+ month.getSelectionModel().getSelectedItem()));
           // allMirsTable.getItems().clear();
            //allMirsTable.getItems().setAll(mirs);
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
                allMirsTable.getItems().clear();
                allMirsTable.getItems().setAll(mirs);
            }else {
                allMirsTable.getItems().clear();
                allMirsTable.getItems().add(MirsDAO.getMIRS(q));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
