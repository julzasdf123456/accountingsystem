package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.itextpdf.text.DocumentException;
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
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewAllReleasedItemController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView allReleasedItemTable;

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

            TableColumn<MIRS, MIRS> printCol = new TableColumn<>("Print");
            printCol.setStyle("-fx-alignment: center;");
            // ADD ACTION BUTTONS
            printCol.setCellValueFactory(mirsmirsCellDataFeatures -> new ReadOnlyObjectWrapper<>(mirsmirsCellDataFeatures.getValue()));
            printCol.setCellFactory(mirsmirsTableColumn -> new TableCell<>(){

                FontIcon printIcon =  new FontIcon("mdi2p-printer");
                private final JFXButton printButton = new JFXButton("", printIcon);

                @Override
                protected void updateItem(MIRS mirs, boolean b) {
                    super.updateItem(mirs, b);

                    if (mirs != null) {

                        printButton.setStyle("-fx-background-color: #ff9800;");
                        printIcon.setIconSize(13);
                        printIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        printButton.setOnAction(actionEvent -> {
                            try {
                                new PrintReleasedItems(mirs);
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
                        hBox.getChildren().add(printButton);

                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                        return;
                    }
                }
            });
            allReleasedItemTable.getColumns().removeAll();
            allReleasedItemTable.getColumns().add(mirsIdCol);
            allReleasedItemTable.getColumns().add(mirsDateFiled);
            allReleasedItemTable.getColumns().add(purposeCol);
            allReleasedItemTable.getColumns().add(printCol);
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
                    allReleasedItemTable.getItems().setAll(mirs);
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
