package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ViewAllMCTController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView mctTable;
    @FXML private JFXTextField search_box, issuedBy, receivedBy, designation;

    private EmployeeInfo issuedByEmployee = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
        this.mctTable.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    printMCT(null);
                }
            });
            return row ;
        });
        try {
            issuedByEmployee = EmployeeDAO.getByDesignation("HEAD, WAREHOUSING SECTION");
            if(issuedByEmployee!=null)
                issuedBy.setText(issuedByEmployee.getFullName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bindEmployeeInfoAutocomplete(issuedBy);
        bindEmployeeInfoAutocomplete(receivedBy);
        populateTable("");
    }

    private void initializeMirsTable() {
        try {
            TableColumn<MIRS, String> mirsIdCol = new TableColumn<>("MCT #");
            mirsIdCol.setPrefWidth(150);
            mirsIdCol.setMaxWidth(150);
            mirsIdCol.setMinWidth(150);
            mirsIdCol.setCellValueFactory(new PropertyValueFactory<>("mctNo"));

            TableColumn<MIRS, String> mirsDateFiled = new TableColumn<>("Date Filed");
            mirsDateFiled.setPrefWidth(100);
            mirsDateFiled.setMaxWidth(100);
            mirsDateFiled.setMinWidth(100);
            mirsDateFiled.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

            TableColumn<MIRS, String> mirsStatus = new TableColumn<>("Address");
            mirsStatus.setPrefWidth(400);
            mirsStatus.setMaxWidth(400);
            mirsStatus.setMinWidth(400);
            mirsStatus.setCellValueFactory(new PropertyValueFactory<>("address"));

            TableColumn<MIRS, String> purposeCol = new TableColumn<>("Particulars");
            purposeCol.setCellValueFactory(new PropertyValueFactory<>("particulars"));

            mctTable.getColumns().removeAll();
            mctTable.getColumns().add(mirsIdCol);
            mctTable.getColumns().add(mirsDateFiled);
            mctTable.getColumns().add(mirsStatus);
            mctTable.getColumns().add(purposeCol);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error initializing table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void printMCT(ActionEvent event) {

        try {
            MCT selected = (MCT) mctTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertDialogBuilder.messgeDialog("System Information", "Select item before proceeding!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                return;
            }

            if(issuedByEmployee == null || receivedBy.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "Both signatories are required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            MCTReleasings mctReleasings = MCTDao.getMCTReleasing(selected.getMctNo());
            if(mctReleasings == null){
                AlertDialogBuilder.messgeDialog("System Information", "Can not find selected MCT record.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                return;
            }


            Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("MCT_report_"+selected.getMctNo()+".pdf");
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) {
                print(selectedFile, mctReleasings.getMct(), mctReleasings.getReleasings());
            }
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("Error", "Error printing MCT due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            e.printStackTrace();
        }
    }

    @FXML
    private void searchMCT(ActionEvent event) {
        populateTable(search_box.getText());
    }

    private void populateTable(String q) {
        try {
            ObservableList<MCT> mirs = FXCollections.observableList(MCTDao.getAllMCT(q));
            mctTable.getItems().setAll(mirs);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", "Error populating table: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void print(File selectedFile, MCT mct, List<Releasing> fromReleasing){
        Platform.runLater(() -> {
            try {
                String issued = issuedByEmployee.getEmployeeFirstName().toUpperCase()+" " +
                        issuedByEmployee.getEmployeeMidName().toUpperCase().charAt(0)+". " +
                        issuedByEmployee.getEmployeeLastName().toUpperCase();
                String issuedDesignation = issuedByEmployee.getDesignation();
                String received = receivedBy.getText();
                String receivedDesignation = designation.getText();

                String[] signatories = {issued, received, issuedDesignation, receivedDesignation};
                PrintMCT printMCT = new PrintMCT(selectedFile, mct, fromReleasing, signatories);
                printMCT.generate();

            }catch (Exception e){
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

    private void bindEmployeeInfoAutocomplete(JFXTextField textField){
        AutoCompletionBinding<EmployeeInfo> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<EmployeeInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = EmployeeDAO.getEmployeeInfo(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                       if(textField == this.issuedBy)
                            issuedByEmployee = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(EmployeeInfo object) {
                        return object.getEmployeeFirstName() + " "+ object.getEmployeeLastName();
                    }

                    @Override
                    public EmployeeInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {

            if(textField == this.issuedBy) {
                issuedByEmployee = event.getCompletion();
                issuedBy.setText(issuedByEmployee.getFullName());
            }
        });
    }
}
