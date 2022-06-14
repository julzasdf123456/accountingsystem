package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.PrintPDF;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserMIRSPreview implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label  details, date, mirsNumber, applicant, address, requisitioner, signatories, purpose;

    @FXML
    private JFXButton printRequest;
    @FXML
    private TableView requestedTable;


    private MIRS mirs;
    private List<Releasing> releasedIitems = null;
    private List<ReleasedItemDetails> mirsItemList = null;
    private List<MIRSSignatory> mirsSignatoryList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mirs = Utility.getActiveMIRS();

        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=sig.getStatus().toUpperCase()+" : "+EmployeeDAO.getOne(sig.getUserID(), DB.getConnection()).getFullName();
                signatures+="\n";
            }
            signatories.setText(signatures);

            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            address.setText(mirs.getAddress());
            applicant.setText(mirs.getApplicant());
            requisitioner.setText(mirs.getRequisitioner().getFullName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        populateRequestedMIRSInfo();
    }

    private void populateRequestedMIRSInfo(){
        try {
            mirsItemList = MirsDAO.getReleasedMIRSItems(mirs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObservableList<ReleasedItemDetails> observableList = FXCollections.observableArrayList(mirsItemList);
        TableColumn<ReleasedItemDetails, String> neaCodeCol = new TableColumn<>("NEA Code");
        neaCodeCol.setStyle("-fx-alignment: center;");
        neaCodeCol.setPrefWidth(150);
        neaCodeCol.setMaxWidth(150);
        neaCodeCol.setMinWidth(150);
        neaCodeCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getNeaCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<ReleasedItemDetails, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<ReleasedItemDetails, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setStyle("-fx-alignment: center;");
        quantityCol.setPrefWidth(50);
        quantityCol.setMaxWidth(50);
        quantityCol.setMinWidth(50);
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));


        //requestedTable.getColumns().add(neaCodeCol);
        requestedTable.getColumns().add(descriptionCol);
        requestedTable.getColumns().add(quantityCol);
        requestedTable.setPlaceholder(new Label("No item Added"));
        requestedTable.getItems().setAll(observableList);
    }





}
