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
    @FXML private JFXTextField search_box, issuedBy, receivedBy;

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
                MIRS mirs = MirsDAO.getMIRS(mct.getMirsNo());
                float[] columns = {1f,.8f,3f,.8f,.8f,.5f,.5f};
                PrintPDF pdf = new PrintPDF(selectedFile, columns);

                //Create header info
                pdf.header(null, "Material Charge Ticket".toUpperCase(), "".toUpperCase());
                String[] head_info = {" ","MIRS No.:",mct.getMirsNo().replaceAll("/","\n").replaceAll("main-","").replaceAll("sub-",""),"Particulars:",mct.getParticulars(),"Date:", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")),"Address:",mct.getAddress(),"MCT No.:",mct.getMctNo()," ","W.O#:",mct.getWorkOrderNo()};
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
                int[] rows_aligns = {Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_RIGHT,Element.ALIGN_RIGHT,Element.ALIGN_CENTER,Element.ALIGN_CENTER};

                double total=0;
                HashMap<String, Double> acctCodeSummary = new HashMap<String, Double>();
                for (Releasing items : fromReleasing) {
                    List<ItemizedMirsItem> details = MirsDAO.getItemizedMirsItemDetails(items.getStockID(), items.getMirsID());
                    String additionalDescription = "";
                    for(ItemizedMirsItem i : details){
                        //if(i.getStockID().equals(items.getStockID())){
                        Stock stock = StockDAO.get(items.getStockID());
                        additionalDescription += "\n("+stock.controlled()+i.getBrand()+", "+i.getSerial()+", "+i.getRemarks()+")";
                        //}
                    }

                    Stock stock = StockDAO.get(items.getStockID());
                    String itemCode="-";
                    if(stock.getNeaCode() != null || !stock.getNeaCode().isEmpty())
                        itemCode = stock.getNeaCode();
                    else
                        itemCode = stock.getLocalCode();
                    String[] val = {stock.getAcctgCode(), itemCode,stock.getDescription()+additionalDescription, String.format("%,.2f", stock.getPrice()), String.format("%,.2f", (stock.getPrice() * items.getQuantity())), stock.getUnit(), "" + Utility.formatDecimal(items.getQuantity())};
                    total += stock.getPrice() * items.getQuantity();
                    rows.add(val);

                    if(acctCodeSummary.get(stock.getAcctgCode()) == null){
                        acctCodeSummary.put(stock.getAcctgCode(),stock.getPrice() * items.getQuantity());
                    }else{
                        acctCodeSummary.replace(stock.getAcctgCode(),acctCodeSummary.get(stock.getAcctgCode()) + (stock.getPrice() * items.getQuantity()) );
                    }
                }
                pdf.tableContent(rows, header_spans, rows_aligns, Element.ALIGN_TOP, Rectangle.NO_BORDER);

                //create a space
                pdf.createCell(2,columns.length);

                //Create Total display
                pdf.createCell(1,2);
                pdf.createCell("TOTAL", 1, 11, Font.BOLD, Element.ALIGN_RIGHT,Rectangle.NO_BORDER);
                pdf.createCell(" "+String.format("%,.2f",total), 2, 11, Font.BOLD, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);
                pdf.createCell(1,2);

                //Create account code summary
                for(Map.Entry<String, Double> acctCode : acctCodeSummary.entrySet()){
                    pdf.createCell(acctCode.getKey(), 1, 10, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
                    pdf.createCell(String.format("%,.2f", acctCode.getValue()), 6, 10, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
                }
                assert mirs != null;
                pdf.createCell("Remarks: "+mirs.getDetails(),columns.length, 11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);

                //create signatories
                pdf.createCell(2,columns.length);
                pdf.createCell("Issued By:",2, 11, Font.NORMAL, Element.ALIGN_RIGHT, Rectangle.NO_BORDER);
                pdf.createCell(1,1);
                pdf.createCell("Received By:",4, 11, Font.NORMAL, Element.ALIGN_LEFT, Rectangle.NO_BORDER);
                pdf.createCell(1,columns.length);
                pdf.createCell(
                        issuedByEmployee.getEmployeeFirstName().toUpperCase()+" " +
                                issuedByEmployee.getEmployeeMidName().toUpperCase().charAt(0)+". " +
                                issuedByEmployee.getEmployeeLastName().toUpperCase(),3, 11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

                /*String mname = receivedByEmployee.getEmployeeMidName().toUpperCase();
                String character;
                if(mname.isEmpty()){
                    character = " ";
                }else {
                    character = mname.charAt(0)+". ";
                }
                pdf.createCell(
                        receivedByEmployee.getEmployeeFirstName().toUpperCase()+" " +
                                character +
                                receivedByEmployee.getEmployeeLastName().toUpperCase(),4, 11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);*/
                pdf.createCell(receivedBy.getText().toUpperCase(),4, 11, Font.BOLD, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

                pdf.createCell(issuedByEmployee.getDesignation(),3, 8, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);

                //pdf.createCell(receivedByEmployee.getDesignation(),4, 8, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);
                pdf.createCell(" ",4, 8, Font.NORMAL, Element.ALIGN_CENTER, Rectangle.NO_BORDER);


                pdf.generate();
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
