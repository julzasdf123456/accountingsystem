package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MCTDao;
import com.boheco1.dev.integratedaccountingsystem.dao.ReleasingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewAllMCTController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML TableView mctTable;
    @FXML private JFXTextField search_box;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMirsTable();
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
            mirsStatus.setPrefWidth(200);
            mirsStatus.setMaxWidth(200);
            mirsStatus.setMinWidth(200);
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
                float[] columns = {1f,.8f,3f,.8f,.8f,.5f,.5f};
                PrintPDF pdf = new PrintPDF(selectedFile, columns);

                //Create header info
                pdf.header(null, "Material Charge Ticket".toUpperCase(), "".toUpperCase());
                String[] head_info = {" ","MIRS No.:",mct.getMirsNo().replaceAll("/","\n"),"Particulars:",mct.getParticulars(),"Date:", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")),"Address:",mct.getAddress(),"MCT No.:",mct.getMctNo()," ","W.O#:",mct.getWorkOrderNo()};
                int[] head_span = {4, 1, 2, 1,3,1,2,1,3,1,2,4,1,2};
                int[] head_aligns = {Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_RIGHT,
                        Element.ALIGN_LEFT, Element.ALIGN_LEFT,Element.ALIGN_LEFT,Element.ALIGN_RIGHT,
                        Element.ALIGN_LEFT, Element.ALIGN_LEFT,Element.ALIGN_LEFT,Element.ALIGN_RIGHT,
                        Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_RIGHT};

                int[] head_fonts = {com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL,
                        com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL,
                        com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL,
                        com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.Font.NORMAL};
                int[] head_borders = {Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER,
                        Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER,
                        Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER,
                        Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER};
                pdf.other_details(head_info, head_span, head_fonts, head_aligns,head_borders, true);


                //Create Table Header
                String[] headers = {"Acct. Code", "Item Code", "Description", "Unit Cost", "Amount", "Unit", "Qty"};
                int[] header_spans = {1, 1, 1, 1, 1, 1, 1};
                pdf.tableHeader(headers, header_spans);

                //Create Table Content
                ArrayList<String[]> rows = new ArrayList<>();
                int[] rows_aligns = {Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_RIGHT,Element.ALIGN_RIGHT,Element.ALIGN_CENTER,Element.ALIGN_RIGHT};

                double total=0;

                for (Releasing items : fromReleasing) {
                    Stock stock = StockDAO.get(ReleasingDAO.get(items.getId()).getStockID());
                    String[] val = {stock.getAcctgCode(), stock.getLocalCode(),stock.getDescription(), String.format("%,.2f", items.getPrice()), String.format("%,.2f", (items.getPrice() * items.getQuantity())), stock.getUnit(), "" + items.getQuantity()};
                    total += items.getPrice() * items.getQuantity();
                    rows.add(val);
                }


                pdf.tableContent(rows, header_spans, rows_aligns);

                //Create Footer
                pdf.createCell(1,3);
                pdf.createCell("TOTAL", 1, 11, Font.BOLD, Element.ALIGN_CENTER);
                pdf.createCell(String.format("%,.2f",total), 1, 11, Font.BOLD, Element.ALIGN_CENTER);
                pdf.createCell(1,2);
                pdf.generate();
            }catch (Exception e){
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }
}
