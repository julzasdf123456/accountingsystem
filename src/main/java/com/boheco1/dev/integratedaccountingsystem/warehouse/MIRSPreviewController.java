package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MIRSPreviewController implements Initializable {

    @FXML
    private TableView requestedTable;
    @FXML
    private TableView releasedTable;

    private MIRS mirs;
    private List<Releasing> releasedIitems = null;
    private List<ReleasedItemDetails> mirsItemList = null;
    private List<MIRSSignatory> signatories;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mirs = Utility.getActiveMIRS();

        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            signatories = MIRSSignatoryDAO.get(mirs);
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
        TableColumn<ReleasedItemDetails, String> stockIdCol = new TableColumn<>("Stock Id");
        stockIdCol.setStyle("-fx-alignment: center;");
        stockIdCol.setPrefWidth(150);
        stockIdCol.setMaxWidth(150);
        stockIdCol.setMinWidth(150);
        stockIdCol.setCellValueFactory(cellData -> {
            try {
                Stock stock = StockDAO.get(cellData.getValue().getStockID());
                String code = "";
                if(stock.getNeaCode() == null || stock.getNeaCode().isEmpty() || stock.getNeaCode().length() <= 4)
                    code = stock.getLocalCode();
                else if(stock.getLocalCode() == null || stock.getLocalCode().isEmpty() || stock.getLocalCode().length() <= 4)
                    code = stock.getNeaCode();
                return new SimpleStringProperty(Objects.requireNonNull(code));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<ReleasedItemDetails, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                String prefix = "";
                MIRSItem temp = MirsDAO.getItems(cellData.getValue().getMirsItemId());
                if(temp.isAdditional())
                    prefix="+";

                return new SimpleStringProperty(prefix+""+Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
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
        quantityCol.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getQuantity())));

        TableColumn<ReleasedItemDetails, String> remainingCol = new TableColumn<>("Rem");
        remainingCol.setStyle("-fx-alignment: center;");
        remainingCol.setPrefWidth(50);
        remainingCol.setMaxWidth(50);
        remainingCol.setMinWidth(50);
        remainingCol.setCellValueFactory(obj-> {
            try {
                return new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getRemaining()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        TableColumn<ReleasedItemDetails, String> actualCol = new TableColumn<>("Act");
        actualCol.setStyle("-fx-alignment: center;");
        actualCol.setPrefWidth(50);
        actualCol.setMaxWidth(50);
        actualCol.setMinWidth(50);
        actualCol.setCellValueFactory(obj-> {
            try {
                return new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getActualReleased()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        TableColumn<ReleasedItemDetails, String> statusCol = new TableColumn<>("Status");
        statusCol.setStyle("-fx-alignment: center;");
        statusCol.setPrefWidth(100);
        statusCol.setMaxWidth(100);
        statusCol.setMinWidth(100);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("Status"));

        requestedTable.getColumns().add(quantityCol);
        requestedTable.getColumns().add(remainingCol);
        requestedTable.getColumns().add(actualCol);
        //requestedTable.getColumns().add(stockIdCol);
        requestedTable.getColumns().add(descriptionCol);
        //requestedTable.getColumns().add(statusCol);
        requestedTable.setPlaceholder(new Label("No item Added"));
        requestedTable.getItems().setAll(observableList);
    }

    @FXML
    void mirsDetails(MouseEvent event) throws Exception {

        String details = "";

        String signatures = "";
        for (MIRSSignatory sig:signatories) {
            signatures+=EmployeeDAO.getOne(sig.getUserID(),DB.getConnection()).getFullName();
            signatures+=", ";
        }
        details += "MIRS #: "+mirs.getId();
        details += "\nDate Filed: "+mirs.getDateFiled();
        details += "\nRequisitioner: "+mirs.getRequisitioner().getFullName();
        details += "\nSignatories: "+signatures;
        details += "\n\nPurpose:\n"+mirs.getPurpose();
        details += "\n\nDetails:\n"+mirs.getDetails();



        details +="\n\nAdditional Info\n";

        details += "Applicant: "+mirs.getApplicant();
        details += "\nAddress: "+mirs.getAddress();

        AlertDialogBuilder.messgeDialog("MIRS Details", details, Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
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
        TableColumn<Releasing, String> stockIdCol = new TableColumn<>("Stock Id");
        stockIdCol.setStyle("-fx-alignment: center;");
        stockIdCol.setPrefWidth(150);
        stockIdCol.setMaxWidth(150);
        stockIdCol.setMinWidth(150);
        stockIdCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(cellData.getValue().getStockID()));
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

        TableColumn<Releasing, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setStyle("-fx-alignment: center;");
        quantityCol.setPrefWidth(50);
        quantityCol.setMaxWidth(50);
        quantityCol.setMinWidth(50);
        quantityCol.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getQuantity())));

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

        releasedTable.getColumns().add(dateCol);
        releasedTable.getColumns().add(quantityCol);
        //releasedTable.getColumns().add(stockIdCol);
        releasedTable.getColumns().add(descriptionCol);
        releasedTable.setPlaceholder(new Label("No item Added"));
        releasedTable.getItems().setAll(observableList);
    }


    @FXML
    private void printRequestMIRS(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );
                fileChooser.setInitialFileName("MIRS_report_"+mirs.getId()+".pdf");
                File selectedFile = fileChooser.showSaveDialog(stage);
                if (selectedFile != null) {
                    float[] columns = {1f,1f,1f,1f,1f,1f};
                    PrintPDF pdf = new PrintPDF(selectedFile, columns);

                    //Create header info
                    pdf.header(null, "Material Issuance Requisition Slip ".toUpperCase(), "".toUpperCase());
                    String[] head_info = {"MIRS No.: ",mirs.getId(),"Work Order No.: ",mirs.getWorkOrderNo(),"Date: ", mirs.getDateFiled().format(DateTimeFormatter.ofPattern("MM/dd/yy"))};
                    int[] head_span = {5,1,5,1,5,1};
                    int[] head_aligns = {
                            Element.ALIGN_RIGHT, Element.ALIGN_LEFT,
                            Element.ALIGN_RIGHT, Element.ALIGN_LEFT,
                            Element.ALIGN_RIGHT, Element.ALIGN_LEFT,
                    };

                    int[] head_fonts = {
                            com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL,
                            com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL,
                            com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL};

                    int[] head_borders = {
                            Rectangle.NO_BORDER, Rectangle.NO_BORDER,
                            Rectangle.NO_BORDER, Rectangle.NO_BORDER,
                            Rectangle.NO_BORDER, Rectangle.NO_BORDER};

                    pdf.other_details(head_info, head_span, head_fonts, head_aligns,head_borders, true);

                    //MIRS description
                    pdf.createCell("   TO: The General Manager\n   Please furnish the following materials/supplies for:", columns.length, 11, Font.NORMAL, Element.ALIGN_LEFT);

                    pdf.createCell("Description: ".toUpperCase(), 1, 11, Font.NORMAL, Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
                    pdf.createCell(mirs.getPurpose(), 4, 11, Font.NORMAL, Element.ALIGN_LEFT,Rectangle.NO_BORDER);
                    pdf.createCell(1,1);

                    pdf.createCell("Applicant: ".toUpperCase(), 1, 11, Font.NORMAL, Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
                    pdf.createCell(mirs.getApplicant(), 4, 11, Font.NORMAL, Element.ALIGN_LEFT,Rectangle.NO_BORDER);
                    pdf.createCell(1,1);

                    pdf.createCell("Address: ".toUpperCase(), 1, 11, Font.NORMAL, Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
                    pdf.createCell(mirs.getAddress(), 4, 11, Font.NORMAL, Element.ALIGN_LEFT,Rectangle.NO_BORDER);
                    pdf.createCell(1,1);

                    pdf.createCell("NEA Code".toUpperCase(), 1, 11, Font.BOLD, Element.ALIGN_CENTER);
                    pdf.createCell("Description".toUpperCase(), 4, 11, Font.BOLD, Element.ALIGN_CENTER);
                    pdf.createCell("Project\nRequirements".toUpperCase(), 1, 10, Font.BOLD, Element.ALIGN_CENTER);

                    //table content
                    for(MIRSItem mirsItem : MirsDAO.getItems(mirs)){
                        Stock stock = StockDAO.get(mirsItem.getStockID());
                        pdf.createCell(stock.getNeaCode(), 1, 11, Font.NORMAL, Element.ALIGN_CENTER);
                        pdf.createCell(stock.getDescription().toUpperCase(), 4, 11, Font.NORMAL, Element.ALIGN_LEFT);
                        pdf.createCell(Utility.formatDecimal(mirsItem.getQuantity()), 1, 10, Font.NORMAL, Element.ALIGN_CENTER);
                    }
                    pdf.createCell(1,columns.length);

                    //Footer Note
                    pdf.createCell("   I HEREBY CERTIFY THAT the above-requested Materials/Supplies are necessary and will be used SOLELY for the\n   purpose stated above", columns.length, 11, Font.NORMAL, Element.ALIGN_LEFT);
                    pdf.createCell("Note: "+mirs.getDetails(), columns.length, 11, Font.NORMAL, Element.ALIGN_LEFT,Rectangle.NO_BORDER);
                    pdf.createCell(2,columns.length);



                    //Signatories
                    pdf.createCell("Prepared by:", 2, 11, Font.NORMAL, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell("Checked by:", 2, 11, Font.NORMAL, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell("Approved by:", 2, 11, Font.NORMAL, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell(2,columns.length);
                    pdf.createCell(mirs.getRequisitioner().getSignatoryNameFormat(), 2, 11, Font.BOLD, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell(EmployeeDAO.getOne(signatories.get(0).getUserID(), DB.getConnection()).getSignatoryNameFormat(), 2, 11, Font.BOLD, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell(EmployeeDAO.getOne(signatories.get(1).getUserID(), DB.getConnection()).getSignatoryNameFormat(), 2, 11, Font.BOLD, Element.ALIGN_CENTER,Rectangle.NO_BORDER);


                    pdf.createCell(mirs.getRequisitioner().getDesignation(), 2, 9, Font.NORMAL, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell(EmployeeDAO.getOne(signatories.get(0).getUserID(), DB.getConnection()).getDesignation(), 2, 9, Font.NORMAL, Element.ALIGN_CENTER,Rectangle.NO_BORDER);
                    pdf.createCell(EmployeeDAO.getOne(signatories.get(1).getUserID(), DB.getConnection()).getDesignation(), 2, 9, Font.NORMAL, Element.ALIGN_CENTER,Rectangle.NO_BORDER);

                    pdf.generate();
                    ModalBuilderForWareHouse.MODAL_CLOSE();
                }


            }catch (Exception e){
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

}
