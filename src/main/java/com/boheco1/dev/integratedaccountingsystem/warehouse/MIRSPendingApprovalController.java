package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MIRSSignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MIRSPendingApprovalController implements Initializable {


    @FXML
    private Label mirsNumber, date, purpose, requisitioner, dm, gm;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, unitCol, particularsCol, remarksCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;
    @FXML
    private TableView<MIRSItem> tableView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("WarehouseDashboardController: "+WarehouseDashboardController.activeMIRS.getId());
        try {
            MIRS mirs = MirsDAO.getMIRS(WarehouseDashboardController.activeMIRS.getId());
            List<MIRSItem> mirsItemList = MirsDAO.getItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            requisitioner.setText(mirs.getRequisitioner().getFullName());
            dm.setText(""+ UserDAO.get(mirsSignatoryList.get(1).getUserID()).getFullName());
            gm.setText(""+ UserDAO.get(mirsSignatoryList.get(0).getUserID()).getFullName());

            ObservableList<MIRSItem> observableList = FXCollections.observableArrayList(mirsItemList);

            codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));

            particularsCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(StockDAO.get(cellData.getValue().getStockID()).getStockName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

            unitCol.setCellValueFactory(new PropertyValueFactory<>("Unit"));
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
            remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));
            tableView.getItems().setAll(observableList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
