package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ReleasingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.PrintMIRS;
import com.boheco1.dev.integratedaccountingsystem.helpers.PrintReleasedItems;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Releasing;
import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSPreview implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private Label date_requested, date_released, requisitioner, mirsNumber, releasedBy;

    @FXML
    private TextArea purpose, details;

    @FXML
    private JFXButton printRequest, printReleased;

    @FXML
    private TableView requestedTable;

    @FXML
    private TableColumn<MIRSItem, String> req_particularsCol, req_unitCol;

    @FXML
    private TableColumn<MIRSItem, Integer> req_quantityCol;

    @FXML
    private TableColumn<MIRSItem, Double> req_price;

    @FXML
    private TableView releasedTable;

    @FXML
    private TableColumn<Releasing, String> rel_particularsCol, rel_unitCol;

    @FXML
    private TableColumn<Releasing, Integer> rel_quantityCol;

    @FXML
    private TableColumn<Releasing, Double> rel_price;

    private MIRS mirs;
    private List<Releasing> releasedIitems = null;
    private List<MIRSItem> mirsItemList = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mirs = Utility.getActiveMIRS();

        populateRequestedMIRSInfo();
        populateReleasedMIRSInfo();
    }

    private void populateRequestedMIRSInfo(){
        try {
            mirsItemList = MirsDAO.getItems(mirs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObservableList<MIRSItem> observableList = FXCollections.observableArrayList(mirsItemList);
        req_particularsCol = new TableColumn<>("Paticulars");
        req_particularsCol.setMinWidth(200);
        req_particularsCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        req_unitCol = new TableColumn<>("Unit");
        req_unitCol.setMinWidth(50);
        req_unitCol.setStyle("-fx-alignment: center;");
        req_unitCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getUnit());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        req_quantityCol = new TableColumn<>("Qty");
        req_quantityCol.setMinWidth(50);
        req_quantityCol.setStyle("-fx-alignment: center;");
        req_quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        req_price = new TableColumn<>("Price");
        req_price.setMinWidth(100);
        req_price.setStyle("-fx-alignment: center-right;");
        req_price.setCellValueFactory(new PropertyValueFactory<>("Price"));

        requestedTable.getColumns().removeAll();
        requestedTable.getColumns().add(req_particularsCol);
        requestedTable.getColumns().add(req_unitCol);
        requestedTable.getColumns().add(req_quantityCol);
        requestedTable.getColumns().add(req_price);
        requestedTable.getItems().setAll(observableList);

        try {
            requisitioner.setText("Requested By: "+mirs.getRequisitioner().getFullName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mirsNumber.setText("MIRS Number: " + mirs.getId());
        date_requested.setText("Requested on: " + mirs.getDateFiled());
        purpose.setText(mirs.getPurpose());
        details.setText(mirs.getDetails());
    }

    private void populateReleasedMIRSInfo() {
        try {
            releasedIitems = ReleasingDAO.get(mirs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(releasedIitems.size() == 0)
            return;

        ObservableList<Releasing> observableList = FXCollections.observableArrayList(releasedIitems);
        rel_particularsCol = new TableColumn<>("Paticulars");
        rel_particularsCol.setMinWidth(200);
        rel_particularsCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        rel_unitCol = new TableColumn<>("Unit");
        rel_unitCol.setMinWidth(50);
        rel_unitCol.setStyle("-fx-alignment: center;");
        rel_unitCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getUnit());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        rel_quantityCol = new TableColumn<>("Qty");
        rel_quantityCol.setMinWidth(50);
        rel_quantityCol.setStyle("-fx-alignment: center;");
        rel_quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        rel_price = new TableColumn<>("Price");
        rel_price.setMinWidth(100);
        rel_price.setStyle("-fx-alignment: center-right;");
        rel_price.setCellValueFactory(new PropertyValueFactory<>("Price"));

        releasedTable.getColumns().removeAll();
        releasedTable.getColumns().add(rel_particularsCol);
        releasedTable.getColumns().add(rel_unitCol);
        releasedTable.getColumns().add(rel_quantityCol);
        releasedTable.getColumns().add(rel_price);
        releasedTable.getItems().setAll(observableList);

        date_released.setText("Released on: " + releasedIitems.get(0).getCreatedAt().toLocalDate());
        try {
            releasedBy.setText("Released By: "+UserDAO.get(releasedIitems.get(0).getUserID()).getFullName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void printReleasedMIRS(ActionEvent event) {
        if(releasedIitems.size() == 0)
            return;

        try {
            new PrintReleasedItems(mirs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void printRequestMIRS(ActionEvent event) {
        try {
            new PrintMIRS(mirs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
