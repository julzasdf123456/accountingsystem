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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ViewAllMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView allMirsTable;
    @FXML private JFXTextField search_box;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
        populateMirsTable("");
    }

    private void initializeMirsTable() {
        try {

            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MIRS Number");
            mirsIdCol.setPrefWidth(150);
            mirsIdCol.setMaxWidth(150);
            mirsIdCol.setMinWidth(150);
            mirsIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<MIRS, String> mirsDateFiled = new TableColumn<>("Date Filed");
            mirsDateFiled.setPrefWidth(150);
            mirsDateFiled.setMaxWidth(150);
            mirsDateFiled.setMinWidth(150);
            mirsDateFiled.setCellValueFactory(new PropertyValueFactory<>("dateFiled"));

            TableColumn<MIRS, String> mirsStatus = new TableColumn<>("Status");
            mirsStatus.setPrefWidth(150);
            mirsStatus.setMaxWidth(150);
            mirsStatus.setMinWidth(150);
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

    private void populateMirsTable(String q) {
        try {
            try {
                if(q.equals("")) {
                    ObservableList<MIRS> mirs = FXCollections.observableList(MirsDAO.getAllMIRS());
                    allMirsTable.getItems().setAll(mirs);
                }else {
                    allMirsTable.getItems().clear();
                    allMirsTable.getItems().add(MirsDAO.getMIRS(q));
                }

            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
