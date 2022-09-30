package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import com.opencsv.CSVReader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class FileMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    String messageLog = "Log: \n";//set to global to access it in the Task class
    String currentItem = "";
    String messageType = "";
    String messageTitle = "";
    @FXML
    private DatePicker filingDate;

    @FXML
    private JFXTextField applicant, address, prepared, checked, approved, items, quantity;

    @FXML
    private JFXTextArea purpose, remarks;

    @FXML
    private Label available, inStock, pending, countRow;


    @FXML
    private TableView<MIRSItem> mirsItemTable;

    @FXML
    private TextArea logsTA;

    private Stock stockToBeAdded = null;
    private EmployeeInfo preparedEmployeeInfo = null;
    private EmployeeInfo checkedEmployeeInfo = null;
    private EmployeeInfo approvedEmployeeInfo = null;
    private ObservableList<MIRSItem> mirsItemRequested;
    private HashMap<String, Double> forQtyUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        forQtyUpdate = new HashMap<>();

        filingDate.setValue(LocalDate.now());
        bindItemSearchAutocomplete(items);
        bindEmployeeInfoAutocomplete(prepared);
        bindEmployeeInfoAutocomplete(checked);
        bindEmployeeInfoAutocomplete(approved);
        InputValidation.restrictNumbersOnly(quantity);
        initializeItemTable();
        try {
            preparedEmployeeInfo = EmployeeDAO.getOne(ActiveUser.getUser().getId(),DB.getConnection());
            prepared.setText(preparedEmployeeInfo.getFullName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //character limiter
        purpose.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 365 ? change : null));
        remarks.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 200 ? change : null));
    }

    private void resetInputFields() {
        stockToBeAdded = null;
        mirsItemRequested.clear();
        mirsItemTable.getItems().clear();
        purpose.setText("");
        items.setText("");
        quantity.setText("");
        applicant.setText("-");
        address.setText("-");
        remarks.setText("");
    }

    @FXML
    private void submitMirsApplication(ActionEvent event) {
        if(purpose.getText().isEmpty() ||
                preparedEmployeeInfo == null ||
                checkedEmployeeInfo == null ||
                approvedEmployeeInfo == null
        ){
            AlertDialogBuilder.messgeDialog("Input validation", "Please provide all required information, and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        if(mirsItemRequested.isEmpty()){
            AlertDialogBuilder.messgeDialog("Table Validation", "No item Added",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        JFXButton accept = new JFXButton("Proceed");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("File MIRS","Confirm MIRS application", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try {
                    List<MIRSSignatory> mirsSignatoryList = new ArrayList<>();
                    MIRS mirs = new MIRS();
                    mirs.setDateFiled(filingDate.getValue());
                    mirs.setPurpose(purpose.getText());
                    mirs.setDetails(remarks.getText());
                    mirs.setStatus(Utility.PENDING);
                    mirs.setRequisitionerID(preparedEmployeeInfo.getId());
                    mirs.setUserID(ActiveUser.getUser().getId());
                    mirs.setAddress(address.getText());
                    mirs.setApplicant(applicant.getText());


                    EmployeeInfo[] signatories = {checkedEmployeeInfo, approvedEmployeeInfo};
                    for(EmployeeInfo s : signatories){
                        MIRSSignatory mirsSignatory = new MIRSSignatory();
                        mirsSignatory.setUserID(s.getId());
                        mirsSignatory.setStatus(Utility.PENDING);
                        mirsSignatoryList.add(mirsSignatory);
                    }

                    if(MirsDAO.create(mirs, mirsItemRequested,mirsSignatoryList)){ //return true saved successfully
                        AlertDialogBuilder.messgeDialog("System Message", "MIRS request successfully filed, please wait for the approval, thank you!",
                                Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);

                        resetInputFields();
                    }else{
                        AlertDialogBuilder.messgeDialog("System Message", "Sorry an error was encountered during saving, please try again.",
                                Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                dialog.close();
            }
        });
    }

    @FXML
    private void addItemToTable(ActionEvent event) throws Exception {
        //Enter key on the qty text field to add items on the table
        if(stockToBeAdded == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(quantity.getText().length() == 0 || Double.parseDouble(quantity.getText()) == 0 || Double.parseDouble(quantity.getText()) > StockDAO.countAvailable(stockToBeAdded) ) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }

        addItem(stockToBeAdded, Double.parseDouble(quantity.getText()), false);
    }
    private void addItem(Stock stock, double qty, boolean isUploaded) throws Exception {

        //will update quantity if item is already added to the request
        for(MIRSItem added: mirsItemRequested){
            if(added.getStockID().equals(stock.getId())){
                double newQty = added.getQuantity() + qty;
                if(newQty > StockDAO.countAvailable(stock)){
                    AlertDialogBuilder.messgeDialog("System Message", "Insufficient stock for item "+stock.getId(),
                            Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                }else{
                    added.setQuantity(added.getQuantity() + qty);
                    if (!isUploaded) {
                        items.setText("");
                        quantity.setText("");
                        stockToBeAdded = null;
                        inStock.setText("In Stock: 0");
                        pending.setText("Pending: 0");
                        available.setText("Available: 0");
                        mirsItemTable.refresh();
                        items.requestFocus();
                    }
                }

                return;
            }
        }

        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setStockID(stock.getId());
        mirsItem.setParticulars(stock.getDescription());
        mirsItem.setUnit(stock.getUnit());
        mirsItem.setQuantity(qty);
        mirsItem.setPrice(stock.getPrice());
        mirsItem.setAdditional(false);

        mirsItemRequested.add(mirsItem);
        mirsItemTable.refresh();
        if (!isUploaded) {
            stockToBeAdded = null; //set to null for validation
            items.setText("");
            quantity.setText("");
            items.requestFocus();
            inStock.setText("In Stock: 0");
            pending.setText("Pending: 0");
            available.setText("Available: 0");
            countRow.setText("" + mirsItemRequested.size());
        }
    }

    @FXML
    void uploadMirsItem(ActionEvent event) {

        messageLog = "";
        Stage stage = (Stage) Utility.getStackPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing command.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);

        if (selectedFile != null) {
            try {
                if (selectedFile != null) {

                    // Create a background Task
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() {
                            try {
                                dialog.show();
                                CSVReader reader = new CSVReader(new FileReader(selectedFile.getAbsolutePath()));

                                List<String[]> allRows = reader.readAll();
                                for(int i = 1; i < allRows.size(); i++) { //start looping at 1 to skip column header
                                    String[] row = allRows.get(i);
                                    String code = row[0];
                                    currentItem = row[1];
                                    double qty = Double.parseDouble(row[row.length - 1].replace(" ",""));
                                    Stock stock = StockDAO.getStockViaNEALocalCode(code);

                                    if (stock == null) {
                                        messageLog += "CANNOT FIND: "+currentItem + " ("+ code +").\n";
                                    }else{
                                        double availableStock = StockDAO.countAvailable(stock);
                                        stock.setQuantity(availableStock);

                                        if (stock.getQuantity() == 0.0) {
                                            messageLog += "OUT OF STOCK: "+stock.getDescription() + " ("+ code +").\n";
                                        } else {
                                            //check if Insufficient stock
                                            if(qty > stock.getQuantity()){
                                                messageLog += "INSUFFICIENT STOCK: "+ stock.getDescription().substring(0,30) + " ("+ code +"), adding stock on-hand.\n";
                                                //add to table the available stock
                                                addItem(stock, availableStock, true);
                                                //keep a copy of item added to table using the stocks available
                                                forQtyUpdate.put(stock.getId(), StockDAO.countAvailable(stock));
                                            }else{
                                                addItem(stock, qty, true);
                                                messageLog += "ADDED: "+stock.getDescription() + " ("+ code +").\n";
                                            }
                                        }
                                    }
                                }
                                messageTitle = "Task Complete";
                                messageType = AlertDialogBuilder.SUCCESS_DIALOG;
                            }catch (NumberFormatException e) {
                                e.printStackTrace();
                                messageTitle = "Error Encounter";
                                messageType = AlertDialogBuilder.DANGER_DIALOG;
                                messageLog +="Invalid quantity provided for item "+currentItem+".";
                                dialog.close();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                                messageTitle = "Error Encounter";
                                messageType = AlertDialogBuilder.DANGER_DIALOG;
                                messageLog +=e.getMessage()+"\n";
                                dialog.close();
                            }catch (Exception e){
                                e.printStackTrace();
                                messageTitle = "Error Encounter";
                                messageType = AlertDialogBuilder.DANGER_DIALOG;
                                messageLog +=e.getMessage()+"\n";
                                dialog.close();
                            }
                            return null;
                        }
                    };

                    task.setOnSucceeded(wse -> {
                        dialog.close();
                        AlertDialogBuilder.messgeDialog("System Message", "Task Complete, please view log panel for process logs." ,
                                Utility.getStackPane(), messageType);
                        logsTA.setText(messageLog);
                        items.setText("");
                        quantity.setText("");
                        stockToBeAdded = null;
                        inStock.setText("In Stock: 0");
                        pending.setText("Pending: 0");
                        available.setText("Available: 0");
                        mirsItemTable.refresh();
                        items.requestFocus();
                        countRow.setText("" + mirsItemRequested.size());
                    });

                    task.setOnFailed(workerStateEvent -> {
                        dialog.close();
                        AlertDialogBuilder.messgeDialog("System Message", "Task Complete, please view log panel for process logs." ,
                                Utility.getStackPane(), messageType);
                        logsTA.setText(messageLog);
                        items.setText("");
                        quantity.setText("");
                        stockToBeAdded = null;
                        inStock.setText("In Stock: 0");
                        pending.setText("Pending: 0");
                        available.setText("Available: 0");
                        mirsItemTable.refresh();
                        items.requestFocus();
                        countRow.setText("" + mirsItemRequested.size());
                    });

                    new Thread(task).start();
                }else{
                    AlertDialogBuilder.messgeDialog("System Warning", "Please select a CSV file before proceeding!",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    items.setText("");
                    quantity.setText("");
                    stockToBeAdded = null;
                    inStock.setText("In Stock: 0");
                    pending.setText("Pending: 0");
                    available.setText("Available: 0");
                    mirsItemTable.refresh();
                    items.requestFocus();
                    countRow.setText("" + mirsItemRequested.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    private void initializeItemTable() {
        /*TableColumn<MIRSItem, String> neaCodeCol = new TableColumn<>("Stock ID");
        neaCodeCol.setStyle("-fx-alignment: center;");
        neaCodeCol.setPrefWidth(150);
        neaCodeCol.setMaxWidth(150);
        neaCodeCol.setMinWidth(150);
        neaCodeCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(cellData.getValue().getStockID()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });*/

        //disable row highlight
        mirsItemTable.setSelectionModel(null);

        TableColumn<MIRSItem, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setStyle("-fx-alignment: center-left;");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setPrefWidth(100);
        quantityCol.setMaxWidth(100);
        quantityCol.setMinWidth(100);
        //quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> qtycellFactory
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                    //set font color RED if item is added to table using the available stock not the requested quantity
                                    if(forQtyUpdate.containsKey(mirsItem.getStockID())) {
                                        setStyle("-fx-text-fill: " + ColorPalette.DANGER + "; -fx-alignment: center-right;");
                                    }else{
                                        setStyle("-fx-text-fill: " + ColorPalette.BLACK + "; -fx-alignment: center-right;");
                                    }
                                    setGraphic(null);
                                    double req = mirsItem.getQuantity();
                                    if(req%1 == 0)
                                        setText(""+(int) req);
                                    else
                                        setText(""+req);
                                }
                            }
                        };
                        return cell;
                    }
                };

        quantityCol.setCellFactory(qtycellFactory);

        TableColumn<MIRSItem, String> removeCol = new TableColumn<>(" ");
        removeCol.setPrefWidth(100);
        removeCol.setMaxWidth(100);
        removeCol.setMinWidth(100);
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> removeColCellFactory
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            private final JFXButton btn = new JFXButton("", icon);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    //btn.setStyle("-fx-background-color: "+ColorPalette.DANGER+";");
                                    icon.setIconSize(24);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                    btn.setOnAction(event -> {
                                        MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                        try{
                                            JFXButton accept = new JFXButton("Accept");
                                            JFXDialog dialog = DialogBuilder.showConfirmDialog("Remove Item","Confirm cancellation of MIRS item: "+StockDAO.get(mirsItem.getStockID()).getDescription(), accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
                                            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
                                            accept.setOnAction(new EventHandler<ActionEvent>() {
                                                @Override
                                                public void handle(ActionEvent __) {
                                                    mirsItemRequested.remove(mirsItem);
                                                    mirsItemTable.refresh();
                                                    countRow.setText(""+ mirsItemRequested.size());
                                                    dialog.close();
                                                }
                                            });
                                        }catch (Exception e){
                                            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while Filing MIRS reason may be: ",
                                                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                            return;
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

        removeCol.setCellFactory(removeColCellFactory);
        removeCol.setStyle("-fx-alignment: center;");

        TableColumn<MIRSItem, String> updateCol = new TableColumn<>(" ");
        updateCol.setPrefWidth(100);
        updateCol.setMaxWidth(100);
        updateCol.setMinWidth(100);
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> updateColCellFactory
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                            FontIcon icon = new FontIcon("mdi2f-file-document-edit");
                            private final JFXButton btn = new JFXButton("", icon);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    //btn.setStyle("-fx-background-color: "+ColorPalette.DANGER+";");
                                    icon.setIconSize(24);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.INFO));
                                    btn.setOnAction(event -> {
                                        try{
                                            MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                            Stock stock = StockDAO.get(mirsItem.getStockID());
                                            JFXButton accept = new JFXButton("Accept");
                                            JFXTextField input = new JFXTextField();
                                            InputValidation.restrictNumbersOnly(input);
                                            JFXDialog dialog = DialogBuilder.showInputDialog("Update Quantity","Enter desired quantity:  ", "Max value "+ StockDAO.getTotalStockViaNEALocalCode(stock.getNeaCode()), input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
                                            accept.setOnAction(new EventHandler<ActionEvent>() {
                                                @Override
                                                public void handle(ActionEvent __) {
                                                    try {
                                                        if(input.getText().length() == 0 || Double.parseDouble(input.getText()) > stock.getQuantity()) {
                                                            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid quantity!",
                                                                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                                        }else {
                                                            double reqQty = Double.parseDouble(input.getText());
                                                            MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                                            mirsItem.setQuantity(reqQty);
                                                            setStyle("-fx-text-fill: " + ColorPalette.BLACK + "; -fx-alignment: center-right;");

                                                            mirsItemTable.refresh();
                                                        }
                                                    } catch (Exception e) {
                                                        AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                                    }
                                                    dialog.close();
                                                }
                                            });
                                        }catch (Exception e){
                                            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while Filing MIRS reason may be: ",
                                                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                            return;
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

        updateCol.setCellFactory(updateColCellFactory);
        updateCol.setStyle("-fx-alignment: center;");


        //mirsItemTable.getColumns().add(neaCodeCol);
        mirsItemTable.getColumns().add(descriptionCol);
        mirsItemTable.getColumns().add(quantityCol);
        mirsItemTable.getColumns().add(removeCol);
        mirsItemTable.getColumns().add(updateCol);
        mirsItemTable.setPlaceholder(new Label("No item Added"));

        mirsItemRequested =  FXCollections.observableArrayList();
        mirsItemTable.setItems(mirsItemRequested);
    }

    private void bindItemSearchAutocomplete(JFXTextField textField){
        AutoCompletionBinding<StockDescription> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<StockDescription> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.searchDescription(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        stockToBeAdded = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(StockDescription object) {
                        return object.getDescription();
                    }

                    @Override
                    public StockDescription fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });
        stockSuggest.setVisibleRowCount(10);
        stockSuggest.setMinWidth(500);
        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(event -> {
            StockDescription result = event.getCompletion();
            try {
                stockToBeAdded = StockDAO.get(result.getId());
                stockToBeAdded.setQuantity(result.getQuantity());
                double av = StockDAO.countAvailable(stockToBeAdded);
                if(av == 0) {
                    AlertDialogBuilder.messgeDialog("System Warning", "Insufficient stock.",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    items.setText("");
                }else{

                    quantity.requestFocus();

                    inStock.setText("In Stock: "+ stockToBeAdded.getQuantity());
                    pending.setText("Pending: "+ StockDAO.countPendingRequest(stockToBeAdded));
                    available.setText("Available: "+ av);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
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
                        if(textField == this.prepared)
                            preparedEmployeeInfo = null;
                        else if(textField == this.checked)
                            checkedEmployeeInfo = null;
                        else if(textField == this.approved)
                            approvedEmployeeInfo = null;
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
            if(textField == this.prepared) {
                preparedEmployeeInfo = event.getCompletion();
                prepared.setText(preparedEmployeeInfo.getFullName());
            }else if(textField == this.checked){
                checkedEmployeeInfo = event.getCompletion();
                checked.setText(checkedEmployeeInfo.getFullName());
            }else if(textField == this.approved){
                approvedEmployeeInfo = event.getCompletion();
                approved.setText(approvedEmployeeInfo.getFullName());
            }
        });
    }
}
