package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewAllMIRSController  extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView allMirsTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();

        populateMirsTable();
    }

    public void initializeMirsTable() {
        try {
            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MIRS Number");
            mirsIdCol.setMinWidth(150);
            mirsIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<MIRS, String> mirsDateFiled = new TableColumn<>("Date Filed");
            mirsDateFiled.setMinWidth(150);
            mirsDateFiled.setCellValueFactory(new PropertyValueFactory<>("dateFiled"));

            TableColumn<MIRS, String> purposeCol = new TableColumn<>("Purpose");
            purposeCol.setMinWidth(150);
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));

            TableColumn<MIRS, MIRS> actionCol = new TableColumn<>("Action");
            actionCol.setMinWidth(150);

            // ADD ACTION BUTTONS
            actionCol.setCellValueFactory(mirsmirsCellDataFeatures -> new ReadOnlyObjectWrapper<>(mirsmirsCellDataFeatures.getValue()));
            actionCol.setCellFactory(mirsmirsTableColumn -> new TableCell<>(){
                FontIcon viewIcon =  new FontIcon("mdi2e-eye");
                private final JFXButton viewButton = new JFXButton("", viewIcon);

                FontIcon deleteIcon =  new FontIcon("mdi2t-trash-can");
                private final JFXButton deleteButton = new JFXButton("", deleteIcon);

                @Override
                protected void updateItem(MIRS mirs, boolean b) {
                    super.updateItem(mirs, b);

                    if (mirs != null) {
                        viewButton.setStyle("-fx-background-color: #2196f3;");
                        viewIcon.setIconSize(13);
                        viewIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        deleteButton.setStyle("-fx-background-color: #f44336;");
                        deleteIcon.setIconSize(13);
                        deleteIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        viewButton.setOnAction(actionEvent -> {
                            try {
                                Utility.setActiveMIRS(mirs);
                                if(mirs.getStatus().equals("Pending")){
                                    ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_mirs_approval_form.fxml", Utility.getStackPane(), "../view_all_mirs_controller.fxml");
                                }else if (mirs.getStatus().equals("Releasing")){
                                    ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_mirs_releasing_form.fxml", Utility.getStackPane(), "../view_all_mirs_controller.fxml");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        deleteButton.setOnAction(actionEvent -> {

                        });

                        HBox hBox = new HBox();
                        hBox.setSpacing(2);
                        hBox.getChildren().add(viewButton);
                        hBox.getChildren().add(deleteButton);

                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                        return;
                    }
                }
            });

            allMirsTable.getColumns().removeAll();
            allMirsTable.getColumns().add(mirsIdCol);
            allMirsTable.getColumns().add(mirsDateFiled);
            allMirsTable.getColumns().add(purposeCol);
            allMirsTable.getColumns().add(actionCol);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error initializing table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void populateMirsTable() {
        try {
            Platform.runLater(() -> {
                try {
                    ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getAllMIRS());
                    allMirsTable.getItems().setAll(mirs);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
