package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MIRSSignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSApprovalFormController implements Initializable {
    @FXML
    private StackPane stackPane;

    @FXML
    private HBox btnHolder;

    @FXML
    private TextArea details, purpose;

    @FXML
    private Label mirsNumber, date, requisitioner, dm, gm;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, unitCol, particularsCol, remarksCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;
    @FXML
    private TableView<MIRSItem> tableView;



    private MIRS mirs;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSItem> mirsItemList = MirsDAO.getItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            requisitioner.setText(mirs.getRequisitioner().getFullName());

            dm.setText(""+ UserDAO.get(mirsSignatoryList.get(0).getUserID()).getFullName());
            gm.setText(""+ UserDAO.get(mirsSignatoryList.get(1).getUserID()).getFullName());

            ObservableList<MIRSItem> observableList = FXCollections.observableArrayList(mirsItemList);

            codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));

            particularsCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

            unitCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getUnit());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
            remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));
            tableView.getItems().setAll(observableList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acceptBtn(ActionEvent event) {
        try {
            if(MIRSSignatoryDAO.getSignatoryCount(mirs.getId()) > 0){
                AlertDialogBuilder.messgeDialog("System Message", "Sorry but approval of both DM and GM are required.", stackPane, AlertDialogBuilder.WARNING_DIALOG);
                return;
            }
            Utility.getActiveMIRS().setStatus(Utility.RELEASING);
            Utility.getActiveMIRS().setDetails(details.getText());
            MirsDAO.update(Utility.getActiveMIRS());
            AlertDialogBuilder.messgeDialog("System Message", "MIRS application approved and ready for releasing.", stackPane, AlertDialogBuilder.INFO_DIALOG);
            btnHolder.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rejectBtn(ActionEvent event) {
        try {
            Utility.getActiveMIRS().setStatus(Utility.REJECTED);
            Utility.getActiveMIRS().setDetails(details.getText());
            MirsDAO.update(Utility.getActiveMIRS());
            AlertDialogBuilder.messgeDialog("System Message", "MIRS application rejected.", stackPane, AlertDialogBuilder.WARNING_DIALOG);
            btnHolder.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
