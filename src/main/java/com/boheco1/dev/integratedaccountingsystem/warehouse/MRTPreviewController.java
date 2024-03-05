package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MRTPreviewController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField returned_tf,received_tf, mirs_no_tf, purpose_tf, dateReturned_tf, mrt_no_tf, total_tf;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableView itemsTable;

    private ObservableList<MRTItem> mrtItems = null;

    private MRT currentMRT = null;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeItemsTable();
        this.currentMRT = (MRT) Utility.getSelectedObject();
        this.set();
    }
    @FXML
    public void print(){
        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("MRT_"+this.currentMRT.getId()+".pdf");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                MRTFormController.printMRT(selectedFile, this.currentMRT, MirsDAO.getMIRS(this.currentMRT.getMirsId()), Utility.getStackPane());
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        this.mrt_no_tf.setText(NumberGenerator.mrtNumber());
        Toast.makeText((Stage) this.contentPane.getScene().getWindow(), "The selected released items were successfully returned!", 2500, 200, 200, "rgba(1, 125, 32, 1)");

    }

    /**
     * Initializes the Return items table
     * @return void
     */
    public void initializeItemsTable() {
        TableColumn<MRTItem, String> column1 = new TableColumn<>("Acct Code");
        column1.setMinWidth(115);
        column1.setMaxWidth(115);
        column1.setPrefWidth(115);
        column1.setCellValueFactory(new PropertyValueFactory<>("AcctCode"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column2 = new TableColumn<>("Item Code");
        column2.setMinWidth(115);
        column2.setMaxWidth(115);
        column2.setPrefWidth(115);
        column2.setCellValueFactory(new PropertyValueFactory<>("Code"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column3 = new TableColumn<>("Description");
        column3.setCellValueFactory(new PropertyValueFactory<>("Description"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column4 = new TableColumn<>("Unit Price");
        column4.setMinWidth(115);
        column4.setMaxWidth(115);
        column4.setPrefWidth(115);
        column4.setCellValueFactory(new PropertyValueFactory<>("Price"));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<MRTItem, String> column5 = new TableColumn<>("Amount");
        column5.setMinWidth(115);
        column5.setMaxWidth(115);
        column5.setPrefWidth(115);
        column5.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<MRTItem, String> column6 = new TableColumn<>("Unit");
        column6.setMinWidth(100);
        column6.setMaxWidth(100);
        column6.setPrefWidth(100);
        column6.setCellValueFactory(new PropertyValueFactory<>("Unit"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MRTItem, String> column7 = new TableColumn<>("Qty");
        column7.setMinWidth(75);
        column7.setMaxWidth(75);
        column7.setPrefWidth(75);
        column7.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column7.setStyle("-fx-alignment: center;");



        this.mrtItems =  FXCollections.observableArrayList();
        this.itemsTable.setPlaceholder(new Label("No item added!"));

        this.itemsTable.getColumns().add(column1);
        this.itemsTable.getColumns().add(column2);
        this.itemsTable.getColumns().add(column3);
        this.itemsTable.getColumns().add(column4);
        this.itemsTable.getColumns().add(column5);
        this.itemsTable.getColumns().add(column6);
        this.itemsTable.getColumns().add(column7);
    }

    public void set(){
        this.mrt_no_tf.setText(this.currentMRT.getId());
        try {
            this.mrtItems = FXCollections.observableArrayList(MRTDao.getItems(this.currentMRT.getId()));
            this.total_tf.setText(MRTDao.getTotal(this.currentMRT.getId())+"");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText((Stage) contentPane.getScene().getWindow(), "An error occurred due to: " + e.getMessage(), 2500, 200, 200, "rgba(203, 24, 5, 1)");
        }
        this.returned_tf.setText(this.currentMRT.getReturnedBy());
        this.received_tf.setText(this.currentMRT.getReceivedEmployee());
        this.mirs_no_tf.setText(this.currentMRT.getMirsId());
        this.purpose_tf.setText(this.currentMRT.getPurpose());
        this.dateReturned_tf.setText(this.currentMRT.getDateOfReturned().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        this.itemsTable.setItems(this.mrtItems);
        this.itemsTable.setPlaceholder(new Label("No item added!"));
    }
}
