package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MRTDao;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MRT;
import com.boheco1.dev.integratedaccountingsystem.objects.Toast;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MRTViewAllController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView mrtTable;
    @FXML private JFXTextField search_box;
    @FXML
    private AnchorPane contentPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMRTTable();
        populateMRTTable("");
    }

    private void initializeMRTTable() {
        try {
            TableColumn<MRT, String> idCol = new TableColumn<>("MRT #");
            idCol.setStyle("-fx-alignment: center;");
            idCol.setPrefWidth(150);
            idCol.setMaxWidth(150);
            idCol.setMinWidth(150);
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<MRT, String> mirsCol = new TableColumn<>("MIRS #");
            mirsCol.setStyle("-fx-alignment: center;");
            mirsCol.setPrefWidth(150);
            mirsCol.setMaxWidth(150);
            mirsCol.setMinWidth(150);
            mirsCol.setCellValueFactory(new PropertyValueFactory<>("mirsId"));

            TableColumn<MRT, String> purposeCol = new TableColumn<>("Purpose");
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));

            TableColumn<MRT, String> dateReturnedCol = new TableColumn<>("Date Returned");
            dateReturnedCol.setStyle("-fx-alignment: center;");
            dateReturnedCol.setPrefWidth(110);
            dateReturnedCol.setMaxWidth(110);
            dateReturnedCol.setMinWidth(110);
            dateReturnedCol.setCellValueFactory(new PropertyValueFactory<>("dateOfReturned"));

            TableColumn<MRT, String> returnedCol = new TableColumn<>("Returned By");
            returnedCol.setStyle("-fx-alignment: left;");
            returnedCol.setPrefWidth(250);
            returnedCol.setMaxWidth(250);
            returnedCol.setMinWidth(250);
            returnedCol.setCellValueFactory(new PropertyValueFactory<>("returnedBy"));

            TableColumn<MRT, String> receivedCol = new TableColumn<>("Received By");
            receivedCol.setStyle("-fx-alignment: left;");
            receivedCol.setPrefWidth(250);
            receivedCol.setMaxWidth(250);
            receivedCol.setMinWidth(250);
            receivedCol.setCellValueFactory(new PropertyValueFactory<>("receivedEmployee"));

            mrtTable.getColumns().removeAll();
            mrtTable.getColumns().add(idCol);
            mrtTable.getColumns().add(mirsCol);
            mrtTable.getColumns().add(purposeCol);
            mrtTable.getColumns().add(dateReturnedCol);
            mrtTable.getColumns().add(returnedCol);
            mrtTable.getColumns().add(receivedCol);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText((Stage) contentPane.getScene().getWindow(), "Error initializing table: " + e.getMessage(), 2500, 200, 200, "rgba(203, 24, 5, 1)");
        }
    }

    @FXML
    private void mrtTableClicked(MouseEvent event) {
        if(event.getClickCount() == 2){
            MRT selected = (MRT) mrtTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Toast.makeText((Stage) contentPane.getScene().getWindow(), "Select item(s) before proceeding!", 2500, 200, 200, "rgba(203, 24, 5, 1)");
                return;
            }
            try {
                Utility.setSelectedObject(selected);
                ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mrt_preview.fxml", Utility.getStackPane());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void searchMRT(ActionEvent event) {
        populateMRTTable(search_box.getText());
    }

    @FXML
    private void populateMRTTable(String q) {
        try {
            if (q.equals("")) {
                ObservableList<MRT> mrts = FXCollections.observableList(MRTDao.getAllMRT());
                mrtTable.getItems().setAll(mrts);
            }else {
                ObservableList<MRT> mrts = FXCollections.observableList(MRTDao.searchMRT(q));
                mrtTable.getItems().setAll(mrts);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText((Stage) contentPane.getScene().getWindow(), "Error initializing table: " + e.getMessage(), 2500, 200, 200, "rgba(203, 24, 5, 1)");
        }
    }
}
