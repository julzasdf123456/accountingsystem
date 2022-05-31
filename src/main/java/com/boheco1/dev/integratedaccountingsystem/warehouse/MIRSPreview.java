package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.PrintMIRS;
import com.boheco1.dev.integratedaccountingsystem.helpers.PrintReleasedItems;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXButton;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSPreview implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label  date, mirsNumber, applicant, address, requisitioner, signatories, purpose;
    @FXML
    private TextArea details;
    @FXML
    private JFXButton printRequest, printReleased;
    @FXML
    private TableView requestedTable;
    @FXML
    private TableView releasedTable;

    private MIRS mirs;
    private List<Releasing> releasedIitems = null;
    private List<ReleasedItemDetails> mirsItemList = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mirs = Utility.getActiveMIRS();

        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=UserDAO.get(sig.getUserID()).getFullName();
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
        populateReleasedMIRSInfo();
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

        TableColumn<ReleasedItemDetails, String> remainingCol = new TableColumn<>("Rem");
        remainingCol.setStyle("-fx-alignment: center;");
        remainingCol.setPrefWidth(50);
        remainingCol.setMaxWidth(50);
        remainingCol.setMinWidth(50);
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("Remaining"));

        TableColumn<ReleasedItemDetails, String> actualCol = new TableColumn<>("Act");
        actualCol.setStyle("-fx-alignment: center;");
        actualCol.setPrefWidth(50);
        actualCol.setMaxWidth(50);
        actualCol.setMinWidth(50);
        actualCol.setCellValueFactory(new PropertyValueFactory<>("ActualReleased"));

        TableColumn<ReleasedItemDetails, String> statusCol = new TableColumn<>("Status");
        statusCol.setStyle("-fx-alignment: center;");
        statusCol.setPrefWidth(100);
        statusCol.setMaxWidth(100);
        statusCol.setMinWidth(100);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("Status"));

        //requestedTable.getColumns().add(neaCodeCol);
        requestedTable.getColumns().add(descriptionCol);
        requestedTable.getColumns().add(quantityCol);
        requestedTable.getColumns().add(remainingCol);
        requestedTable.getColumns().add(actualCol);
        requestedTable.getColumns().add(statusCol);
        requestedTable.setPlaceholder(new Label("No item Added"));
        requestedTable.getItems().setAll(observableList);
    }

    private void populateReleasedMIRSInfo() {
        try {
            releasedIitems = ReleasingDAO.getAllReleasedAndPartial(mirs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(releasedIitems.size() == 0)
            return;

        ObservableList<Releasing> observableList = FXCollections.observableArrayList(releasedIitems);

        TableColumn<Releasing, String> neaCodeCol = new TableColumn<>("NEA Code");
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

        TableColumn<Releasing, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<Releasing, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setStyle("-fx-alignment: center;");
        quantityCol.setPrefWidth(50);
        quantityCol.setMaxWidth(50);
        quantityCol.setMinWidth(50);
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        TableColumn<Releasing, String> dateCol = new TableColumn<>("Date");
        dateCol.setStyle("-fx-alignment: center;");
        dateCol.setPrefWidth(100);
        dateCol.setMaxWidth(100);
        dateCol.setMinWidth(100);
        dateCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        //releasedTable.getColumns().add(neaCodeCol);
        releasedTable.getColumns().add(descriptionCol);
        releasedTable.getColumns().add(quantityCol);
        releasedTable.getColumns().add(dateCol);
        releasedTable.setPlaceholder(new Label("No item Added"));
        releasedTable.getItems().setAll(observableList);
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
