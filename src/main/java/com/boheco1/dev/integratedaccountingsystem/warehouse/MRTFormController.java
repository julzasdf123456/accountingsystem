package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

public class MRTFormController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField searchItem_tf,returned_tf,received_tf, mirs_no_tf, mirs_purpose_tf, mirs_address_tf, mrt_no_tf;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableView releasedItemTable, returnItemTable;

    private ObservableList<MRTItem> mrtItems = null;
    private ObservableList<ReleasedItems> releasedItems = null;
    private MRT currentMRT = null;
    private EmployeeInfo receivedBy = null;
    private MIRS mirs = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeReleasedItemTable();
        this.initializeReturnItemTable();
        this.bindSearchAutocomplete(this.searchItem_tf);
        this.bindReturnedByAutocomplete(this.received_tf, true);
        this.bindReturnedByAutocomplete(this.returned_tf, false);
        try {
            this.receivedBy = EmployeeDAO.getByDesignation("Head, Warehous");
            if (this.receivedBy != null)
                this.received_tf.setText(this.receivedBy.getFullName());
            else {
                this.receivedBy = EmployeeDAO.getByDesignation("Warehouse");
                this.received_tf.setText(this.receivedBy.getFullName());
            }
            this.mrt_no_tf.setText(NumberGenerator.mrtNumber());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Selects the released items from the released items table, displays the quantity to return and displays them in the return items table
     * @return void
     */
    @FXML
    public void returnItem(){
        Object selectedItem = this.releasedItemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertDialogBuilder.messgeDialog("Input Error", "Please select from the released item table before proceeding!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            ReleasedItems item = (ReleasedItems) selectedItem;
            JFXButton returnNumber = new JFXButton("Return");
            JFXTextField qty_tf = new JFXTextField();
            qty_tf.setText(item.getBalance()+"");
            InputValidation.restrictNumbersOnly(qty_tf);
            JFXDialog returnDialog = DialogBuilder.showInputDialog("Quantity To Return","Please enter the quantity:  ", "", qty_tf, returnNumber, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            returnNumber.setOnAction(__ -> {
                if(qty_tf.getText().isEmpty()) {
                    AlertDialogBuilder.messgeDialog("System Message", "No return quantity provided", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else{
                    try{
                        double qty_to_return = Double.parseDouble(qty_tf.getText());

                        if (qty_to_return <= 0){
                            AlertDialogBuilder.messgeDialog("System Message", "No return quantity provided", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        }else if (qty_to_return > item.getBalance()){
                            AlertDialogBuilder.messgeDialog("System Message", "The return quantity provided should not exceed the maximum released quantity!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        }else{
                            item.setBalance(item.getBalance() - qty_to_return);
                            this.releasedItemTable.refresh();
                            MRTItem returnItem = new MRTItem(null, item.getId(), null, qty_to_return);
                            returnItem.setCode(item.getCode());
                            returnItem.setStockID(item.getStockID());
                            boolean ok = true;
                            for (MRTItem i : this.mrtItems){
                                if (i.getReleasingID().equals(returnItem.getReleasingID())) {
                                    i.setQuantity(i.getQuantity() + qty_to_return);
                                    ok = false;
                                    break;
                                }
                            }
                            if (!ok){
                                this.returnItemTable.refresh();
                            }else{
                                this.mrtItems.add(returnItem);
                                this.returnItemTable.setItems(this.mrtItems);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                returnDialog.close();
            });
        }
    }
    /**
     * Creates and MCT and returns the MRT items
     * @return void
     */
    @FXML
    public void returnItems(){
        if (this.mrtItems.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Message", "No items were selected for returning!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.returned_tf.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "No returned by employee was set!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.receivedBy == null) {
            AlertDialogBuilder.messgeDialog("System Message", "No received by employee was set!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else  {
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("MRT","This process is final. Confirm Return Item(s)?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                this.currentMRT = new MRT(this.mrt_no_tf.getText(), this.returned_tf.getText(), this.receivedBy.getId(), LocalDate.now());
                try {
                    MRTDao.create(this.currentMRT);
                    MRTDao.addItems(this.currentMRT, this.mrtItems);
                    Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                    );
                    fileChooser.setInitialFileName("MRT_"+this.currentMRT.getId()+".pdf");
                    File selectedFile = fileChooser.showSaveDialog(stage);
                    if (selectedFile != null) {
                        printMRT(selectedFile, this.currentMRT, this.mirs, Utility.getStackPane());
                    }
                    this.mrt_no_tf.setText(NumberGenerator.mrtNumber());
                    Toast.makeText((Stage) this.contentPane.getScene().getWindow(), "The selected released items were successfully returned!", 2500, 200, 200, "rgba(1, 125, 32, 1)");
                    this.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText((Stage) this.contentPane.getScene().getWindow(), "An error occurred while returning the items due to: " + e.getMessage(), 2500, 200, 200, "rgba(203, 24, 5, 1)");
                }
                dialog.close();
            });
        }
    }

    /**
     * Initializes the Released items table
     * @return void
     */
    public void initializeReleasedItemTable() {
        TableColumn<ReleasedItems, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(85);
        column1.setMaxWidth(85);
        column1.setPrefWidth(85);
        column1.setCellValueFactory(new PropertyValueFactory<>("Code"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ReleasedItems, String> column2 = new TableColumn<>("Description");
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<ReleasedItems, String> column3 = new TableColumn<>("MCT No");
        column3.setMinWidth(100);
        column3.setMaxWidth(100);
        column3.setPrefWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("mctNo"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<ReleasedItems, String> column4 = new TableColumn<>("Price");
        column4.setMinWidth(100);
        column4.setMaxWidth(100);
        column4.setPrefWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("price"));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<ReleasedItems, String> column5 = new TableColumn<>("Qty");
        column5.setMinWidth(75);
        column5.setMaxWidth(75);
        column5.setPrefWidth(75);
        column5.setCellValueFactory(new PropertyValueFactory<>("balance"));
        column5.setStyle("-fx-alignment: center;");

        this.releasedItems =  FXCollections.observableArrayList();
        this.releasedItemTable.setPlaceholder(new Label("No item searched!"));

        this.releasedItemTable.getColumns().add(column1);
        this.releasedItemTable.getColumns().add(column2);
        this.releasedItemTable.getColumns().add(column3);
        this.releasedItemTable.getColumns().add(column4);
        this.releasedItemTable.getColumns().add(column5);
    }

    /**
     * Initializes the Return items table
     * @return void
     */
    public void initializeReturnItemTable() {
        TableColumn<MRTItem, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(85);
        column1.setMaxWidth(85);
        column1.setPrefWidth(85);
        column1.setCellValueFactory(new PropertyValueFactory<>("Code"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column2 = new TableColumn<>("Description");
        column2.setCellValueFactory(item -> {
            try {
                Stock stock = StockDAO.get(ReleasingDAO.get(item.getValue().getReleasingID()).getStockID());
                return new ReadOnlyObjectWrapper<>(stock.getDescription());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column3 = new TableColumn<>("Qty");
        column3.setMinWidth(75);
        column3.setMaxWidth(75);
        column3.setPrefWidth(75);
        column3.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<MRTItem, String> column4 = new TableColumn<>("Action");
        column4.setMinWidth(75);
        column4.setMaxWidth(75);
        column4.setPrefWidth(75);
        Callback<TableColumn<MRTItem, String>, TableCell<MRTItem, String>> removeBtn
                = //
                new Callback<TableColumn<MRTItem, String>, TableCell<MRTItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MRTItem, String> param) {
                        final TableCell<MRTItem, String> cell = new TableCell<MRTItem, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            JFXButton btn = new JFXButton("", icon);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconSize(24);
                                icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        MRTItem selectedItem = getTableView().getItems().get(getIndex());

                                        try {
                                            mrtItems.remove(selectedItem);
                                            returnItemTable.setItems(mrtItems);
                                            for (ReleasedItems i : releasedItems){
                                                if (i.getId().equals(selectedItem.getReleasingID())) {
                                                    i.setBalance(selectedItem.getQuantity() + i.getBalance());
                                                    break;
                                                }
                                            }
                                            releasedItemTable.refresh();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        column4.setCellFactory(removeBtn);
        column4.setStyle("-fx-alignment: center;");

        this.mrtItems =  FXCollections.observableArrayList();
        this.returnItemTable.setPlaceholder(new Label("No item added!"));

        this.returnItemTable.getColumns().add(column1);
        this.returnItemTable.getColumns().add(column2);
        this.returnItemTable.getColumns().add(column3);
        this.returnItemTable.getColumns().add(column4);
    }

    /**
     * Binds the textfield to autosuggest MIRS
     * @return void
     */
    public void bindSearchAutocomplete(JFXTextField textField){
        AutoCompletionBinding<MIRS> mirsSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<MIRS> list = new ArrayList<>();

                    //Perform DB query when length of search string is 2 or above
                    if (query.length() > 1){
                        try {
                            list = MirsDAO.searchMIRS(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(MIRS object) {
                        return object.getId()+": "+ object.getPurpose()+", Address:"+object.getAddress();
                    }

                    @Override
                    public MIRS fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        mirsSuggest.setOnAutoCompleted(event -> {
            MIRS mirs = event.getCompletion();
            searchItem_tf.setText("");
            Platform.runLater(() -> {
                try {
                    this.mirs = mirs;
                    this.releasedItems = FXCollections.observableList(MRTDao.searchReleasedItems(mirs.getId()));
                    this.releasedItemTable.getItems().setAll(this.releasedItems);
                    this.mirs_address_tf.setText(mirs.getAddress());
                    this.mirs_purpose_tf.setText(mirs.getPurpose());
                    this.mirs_no_tf.setText(mirs.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        });
    }

    /**
     * Binds the textfield to autosuggest employee
     * @return void
     */
    public void bindReturnedByAutocomplete(JFXTextField textField, boolean isReceived){
        AutoCompletionBinding<EmployeeInfo> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<EmployeeInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 2 or above
                    if (query.length() > 1){
                        try {
                            list = EmployeeDAO.getEmployeeInfo(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0 && isReceived) {
                       receivedBy = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(EmployeeInfo object) {
                        return object.getFullName();
                    }

                    @Override
                    public EmployeeInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {
            EmployeeInfo user = event.getCompletion();
            textField.setText(user.getFullName());
            if (isReceived) {receivedBy = user;}
        });
    }

    /**
     * Resets all field values
     * @return void
     */
    @FXML
    public void reset(){
        this.mirs = null;
        this.mrtItems = FXCollections.observableArrayList();
        this.releasedItems = FXCollections.observableArrayList();
        this.currentMRT = null;
        this.returned_tf.setText("");
        this.searchItem_tf.setText("");
        this.mirs_no_tf.setText("");
        this.mirs_purpose_tf.setText("");
        this.mirs_address_tf.setText("");
        this.returnItemTable.setItems(this.mrtItems);
        this.releasedItemTable.setItems(this.releasedItems);
        this.returnItemTable.setPlaceholder(new Label("No item added!"));
        this.releasedItemTable.setPlaceholder(new Label("No item searched!"));
    }

    /**
     * Generates a pdf containing the MR
     * @param selectedFile the pointer to the pdf file using the FileChooser
     * @param mrt the MR object to generate
     * @param stackpane the stackpane for the dialogs to display
     * @return void
     */
    public static void printMRT(File selectedFile, MRT mrt, MIRS mirs, StackPane stackpane){
        Platform.runLater(() -> {
            float[] columns = { .95f, .95f, 2.3f, .95f, 0.95f, .5f, .5f};
            PrintPDF mrt_pdf = new PrintPDF(selectedFile, columns);
            //Create Header
            mrt_pdf.header(null, "MATERIALS RETURN TICKET");

            int[] head_span = {1, 3, 1, 2};
            int[] head_aligns = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT};
            int[] head_fonts = {Font.BOLD, Font.NORMAL, Font.BOLD, Font.NORMAL};
            int[] head_borders = {Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER};
            String[] mirs_no = {"", "", "MIRS No", mirs.getId()};
            mrt_pdf.other_details(mirs_no, head_span, head_fonts, head_aligns,head_borders, false);
            String[] mrt_no = {"Particulars", mirs.getPurpose(), "Date", mrt.getDateOfReturned().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))};
            mrt_pdf.other_details(mrt_no, head_span, head_fonts, head_aligns,head_borders, false);
            String[] addr = {"Address", mirs.getAddress(), "MRT No", mrt.getId()};
            mrt_pdf.other_details(addr, head_span, head_fonts, head_aligns,head_borders, false);

            //Create Table Header
            String[] headers = {"Acct Code", "Item Code", "Description", "Unit Cost", "Amount", "Unit", "Qty"};
            int[] header_spans = {1, 1, 1, 1, 1, 1, 1};
            mrt_pdf.tableHeader(headers, header_spans);
            int[] rows_aligns = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_RIGHT,
                    Element.ALIGN_RIGHT, Element.ALIGN_CENTER, Element.ALIGN_RIGHT};
            ArrayList<String[]> rows = new ArrayList<>();
            double total = 0;
            try {
                List<MRTItem> items = MRTDao.getItems(mrt.getId());

                for (MRTItem item : items) {
                    String[] data = {item.getAcctCode(), item.getCode(), item.getDescription(), item.getPrice()+"", item.getAmount()+"", item.getUnit(), item.getQuantity()+""};
                    rows.add(data);
                    total += item.getAmount();
                }
                mrt_pdf.tableContent(rows, header_spans, rows_aligns);
                String[] amount = {"", "", "", "TOTAL", Utility.round(total, 2)+"", "", ""};
                int[] amount_fonts = {Font.NORMAL, Font.NORMAL, Font.NORMAL, Font.NORMAL, Font.NORMAL, Font.NORMAL, Font.NORMAL};
                int[] amount_aligns = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_RIGHT, Element.ALIGN_LEFT, Element.ALIGN_LEFT};
                int[] amount_borders = {Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.BOX, Rectangle.BOX, Rectangle.NO_BORDER, Rectangle.NO_BORDER};
                mrt_pdf.other_details(amount, header_spans, amount_fonts, amount_aligns, amount_borders, true);
                int[] footer_spans = {2, 1, 4};
                String[] signatorees = {"Received by:", "", "Returned by:"};
                EmployeeInfo receivedBy = EmployeeDAO.getOne(mrt.getReceivedBy(), DB.getConnection());
                String returnedBy = mrt.getReturnedBy();
                String names[] = {receivedBy.getEmployeeFirstName()+ " "+receivedBy.getEmployeeLastName(), "", returnedBy};
                String[] designations = {receivedBy.getDesignation(), "", "Contractor/Personnel"};
                mrt_pdf.footer(signatorees, designations, names, footer_spans, true, Element.ALIGN_CENTER, Element.ALIGN_CENTER);
                mrt_pdf.generate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
