package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.APP;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ManageAppController extends MenuControllerHandler implements Initializable {

    @FXML
    TableView appTable;

    @FXML
    JFXButton createButton;

    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.stackPane = Utility.getStackPane();
        try {
            ArrayList<APP> apps = AppDAO.getAll();
            renderTable(apps);
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Unexpected Error",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void renderTable(ArrayList<APP> apps) {
        ObservableList<APP> fxApp = FXCollections.observableArrayList(apps);

        TableColumn yearCol = new TableColumn<APP, String>("Budget Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<APP, String>("year"));

        TableColumn boardResoCol = new TableColumn<APP, String>("Board Resolution");
        boardResoCol.setCellValueFactory(new PropertyValueFactory<APP, String>("boardRes"));

        TableColumn cobCol = new TableColumn<APP, String>("COB");
        cobCol.setCellValueFactory(new PropertyValueFactory<APP, String>("formattedTotalBudget"));
        cobCol.setStyle("-fx-alignment: CENTER_RIGHT");

        TableColumn isOpenCol = new TableColumn<APP, String>("Is Open?");
        isOpenCol.setCellValueFactory(new PropertyValueFactory<APP, String>("isOpenString"));

        TableColumn<APP, APP> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(stockStringCellDataFeatures -> new ReadOnlyObjectWrapper<>(stockStringCellDataFeatures.getValue()));
        actionCol.setStyle("-fx-alignment: CENTER");
        actionCol.setCellFactory(stockStockTableColumn -> new TableCell<>(){
            final FontIcon openIcon =  new FontIcon("mdi2d-door-open");
            private final JFXButton openButton = new JFXButton("", openIcon);

            @Override
            protected void updateItem(APP app, boolean b) {
                super.updateItem(app, b);
                openButton.setStyle("-fx-background-color: #009688;");
                openIcon.setIconSize(14);
                openIcon.setIconColor(Color.WHITE);

                if(app!=null) {
                    setGraphic(openButton);

                    openButton.setOnAction(actionEvent->{

                    });
                }
            }
        });

        appTable.getColumns().add(yearCol);
        appTable.getColumns().add(boardResoCol);
        appTable.getColumns().add(cobCol);
        appTable.getColumns().add(isOpenCol);
        appTable.getColumns().add(actionCol);

        appTable.setItems(fxApp);
    }
    @FXML
    public void onCreate() {
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budgeting/create_app.fxml"));
    }
}
