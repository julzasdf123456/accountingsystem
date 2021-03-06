package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class MIRSReleasingController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private Label details, inStock, pending, available;

    @FXML
    private JFXTextField stockItem, quantity;

    @FXML
    private JFXButton addAllQtyBtn, addPartialQtyBtn, removeItemBtn, detailstemBtn;

    //@FXML
    //private JFXListView<MIRSItem> requestedList, releasingList;
    @FXML
    private TableView<MIRSItem> requestedItemTable,  releasingItemTable;

    private Stock selectedStock = null;
    private List<MIRSItem> additionalMirsItem = new ArrayList<>();
    private MIRS mirs;
    private List<MIRSItem> requestedMirsItem;
    private List<MIRSSignatory> signatories;
    private ObservableList<MIRSItem> requestedItem, releasingItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputValidation.restrictNumbersOnly(quantity);
        bindParticularsAutocomplete(stockItem);
        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            requestedMirsItem = MirsDAO.getUnreleasedItems(mirs);

            //update remaining qty
            for (MIRSItem updateQty : requestedMirsItem){
                updateQty.setQuantity(MirsDAO.getBalance(updateQty));
            }


            signatories = MIRSSignatoryDAO.get(mirs);
            initializeRequestTable();
            initializeReleasingTable();
            //clear hashmap that contains all itemized records
            Utility.getItemizedMirsItems().clear();

            //set Paren tController for ObjectTransaction
            Utility.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeRequestTable() throws Exception {
        TableColumn<MIRSItem, CheckBox> reqCheckBoxCol = new TableColumn("#");
        reqCheckBoxCol.setPrefWidth(30);
        reqCheckBoxCol.setMaxWidth(30);
        reqCheckBoxCol.setMinWidth(30);
        reqCheckBoxCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MIRSItem, CheckBox>, ObservableValue<CheckBox>>() {
            @Override
            public ObservableValue<CheckBox> call(
                    TableColumn.CellDataFeatures<MIRSItem, CheckBox> arg0) {
                MIRSItem mirsItem = arg0.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().setValue(mirsItem.isSelected());
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> ov,
                                        Boolean old_val, Boolean new_val) {

                        mirsItem.setSelected(new_val);
                    }
                });
                return new SimpleObjectProperty<CheckBox>(checkBox);
            }
        });


        TableColumn<MIRSItem, String> reqDescriptionCol = new TableColumn<>("Description");
        reqDescriptionCol.setStyle("-fx-alignment: center-left;");
        reqDescriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> reqQuantityCol = new TableColumn<>("Qty");
        reqQuantityCol.setStyle("-fx-alignment: center;");
        reqQuantityCol.setPrefWidth(50);
        reqQuantityCol.setMaxWidth(50);
        reqQuantityCol.setMinWidth(50);
        reqQuantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        requestedItemTable.getColumns().add(reqCheckBoxCol);
        requestedItemTable.getColumns().add(reqDescriptionCol);
        requestedItemTable.getColumns().add(reqQuantityCol);
        requestedItemTable.setPlaceholder(new Label("No item found"));

        requestedItem =  FXCollections.observableArrayList(requestedMirsItem);
        requestedItemTable.setItems(requestedItem);
    }

    private void initializeReleasingTable() throws Exception {
        TableColumn<MIRSItem, String> relDescriptionCol = new TableColumn<>("Description");
        relDescriptionCol.setStyle("-fx-alignment: center-left;");
        relDescriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> relQuantityCol = new TableColumn<>("Qty");
        relQuantityCol.setStyle("-fx-alignment: center;");
        relQuantityCol.setPrefWidth(50);
        relQuantityCol.setMaxWidth(50);
        relQuantityCol.setMinWidth(50);
        relQuantityCol.setCellValueFactory((new PropertyValueFactory<>("Quantity")));

        TableColumn<MIRSItem, String> relBrandCol = new TableColumn<>("Brand");
        relBrandCol.setStyle("-fx-alignment: center;");
        relBrandCol.setPrefWidth(100);
        relBrandCol.setMaxWidth(100);
        relBrandCol.setMinWidth(100);
        relBrandCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(StockDAO.get(cellData.getValue().getStockID()).getBrand());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> itemizeItemCol = new TableColumn<>("");
        itemizeItemCol.setPrefWidth(50);
        itemizeItemCol.setMaxWidth(50);
        itemizeItemCol.setMinWidth(50);
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> itemizeCellFactory
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                            FontIcon icon = new FontIcon("mdi2s-sitemap");
                            private final JFXButton btn = new JFXButton("", icon);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    try {
                                        MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                        if(StockDAO.get(mirsItem.getStockID()).isIndividualized()) {
                                            icon.setIconSize(24);
                                            icon.setIconColor(Paint.valueOf(ColorPalette.INFO));
                                            btn.setOnAction(event -> {
                                                Utility.setSelectedObject(mirsItem);
                                                if (Utility.getSelectedObject() != null)
                                                    ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mirs_item_itemized.fxml", Utility.getStackPane());

                                            });
                                            setGraphic(btn);
                                            setText(null);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                        return cell;
                    }
                };

        itemizeItemCol.setCellFactory(itemizeCellFactory);
        itemizeItemCol.setStyle("-fx-alignment: center;");


        TableColumn<MIRSItem, String> removeItemCol = new TableColumn<>("");
        removeItemCol.setPrefWidth(50);
        removeItemCol.setMaxWidth(50);
        removeItemCol.setMinWidth(50);
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> removeCellFactory
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
                                                    releasingItem.remove(mirsItem);
                                                    if(!mirsItem.isAdditional()) {
                                                        boolean found = false;
                                                        for (MIRSItem m : requestedItem){
                                                            if(m.getId().equals(mirsItem.getId())){
                                                                int updateQty = m.getQuantity() + mirsItem.getQuantity();
                                                                m.setQuantity(updateQty);
                                                                found = true;
                                                                break;
                                                            }
                                                        }
                                                        if(!found)
                                                            requestedItem.add(mirsItem);

                                                    }
                                                    mirsItem.setSelected(false);
                                                    requestedItemTable.refresh();
                                                    releasingItemTable.refresh();
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

        removeItemCol.setCellFactory(removeCellFactory);
        removeItemCol.setStyle("-fx-alignment: center;");


        releasingItemTable.getColumns().add(relDescriptionCol);
        releasingItemTable.getColumns().add(relBrandCol);
        releasingItemTable.getColumns().add(relQuantityCol);
        releasingItemTable.getColumns().add(itemizeItemCol);
        releasingItemTable.getColumns().add(removeItemCol);
        releasingItemTable.setPlaceholder(new Label("No item found"));

        releasingItem =  FXCollections.observableArrayList();
        releasingItemTable.setItems(releasingItem);
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

    private String hasMultipleBrand() throws Exception {
        for (MIRSItem mirsItem : requestedItem){
            if(mirsItem.isSelected()){
                String description = StockDAO.get(mirsItem.getStockID()).getDescription();
                if(StockDAO.hasMultiple(description)){
                    return description;
                }
            }
        }
        return null;
    }

    private String isIndividualize() throws Exception {
        for (MIRSItem mirsItem : requestedItem){
            if(mirsItem.isSelected()){
                Stock stock = StockDAO.get(mirsItem.getStockID());
                if(stock.isIndividualized()){
                    boolean found = false;
                    HashMap<String, ItemizedMirsItem> holder = Utility.getItemizedMirsItems();
                    for (Map.Entry i : holder.entrySet()) {
                        ItemizedMirsItem item = holder.get(i.getKey());
                        if(item.getMirsItemID().equals(mirsItem.getId())){
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        return stock.getDescription();
                    }
                }
            }
        }
        return null;
    }

    private int countSelected(){
        int counter = 0;
        for (MIRSItem mirsItem : requestedItem){
            if(mirsItem.isSelected()){
                counter++;
            }
        }
        return counter;
    }

    private String checkForBrandandIndividualized() throws Exception {
        String msg = "";
        String hasMultiple = hasMultipleBrand();
        if(hasMultiple != null)
            msg += hasMultiple +" has multiple brand and need to be release separately.\n";

        return msg;
    }

    @FXML
    void add(ActionEvent event) throws Exception {
        int countSelected = countSelected();
        if(countSelected == 0) {
            AlertDialogBuilder.messgeDialog("System Message", "No available item(s) selected for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }else if(countSelected == 1) {
            for (final MIRSItem mirsItem : requestedItem){
                if(mirsItem.isSelected()){
                    String hasMultipleBrand = hasMultipleBrand(); // return description
                    if(hasMultipleBrand != null){
                        Utility.setSelectedObject(mirsItem);
                        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mirs_releasing_select_item.fxml", Utility.getStackPane());
                    }else{
                        System.out.println(mirsItem.getQuantity());
                        releasingItem.add(mirsItem);
                        requestedItem.removeAll(mirsItem);
                        requestedItemTable.refresh();
                        releasingItemTable.refresh();
                    }
                    break;
                }
            }
        }else if(countSelected > 1) {
            String msg = checkForBrandandIndividualized();
            if(msg.isEmpty()){
                for (MIRSItem mirsItem : requestedItem){
                    if(mirsItem.isSelected()){
                        releasingItem.add(mirsItem);
                    }
                }
                requestedItem.removeAll(releasingItem);
                requestedItemTable.refresh();
                releasingItemTable.refresh();
            }else{
                AlertDialogBuilder.messgeDialog("System Message", "Cannot released selected item. " +
                        "\n\nReason:\n" +
                        ""+msg+"\n\n" +
                        "Note: Items that has multiple brand or requires to be individualized cannot be released in batch.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
        }
    }

    @FXML
    void partial(ActionEvent event) throws Exception {
        int countSelected = countSelected();
        if(countSelected == 0) {
            AlertDialogBuilder.messgeDialog("System Message", "No available item(s) selected for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }else if(countSelected == 1){
            for (MIRSItem mirsItem : requestedItem){
                if(mirsItem.isSelected()){
                    String hasMultipleBrand = hasMultipleBrand(); // return description
                    if(hasMultipleBrand != null){
                        AlertDialogBuilder.messgeDialog("System Message", "Item "+hasMultipleBrand+" has multiple brand available.\n\n" +
                                "Note: Please use regular adding process for item with multiple brand.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    }else{
                        JFXButton accept = new JFXButton("Accept");
                        JFXTextField input = new JFXTextField();
                        InputValidation.restrictNumbersOnly(input);
                        JFXDialog dialog = DialogBuilder.showInputDialog("Partial Quantity","Enter desired quantity:  ", "Max value "+ mirsItem.getQuantity(), input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
                        accept.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent __) {
                                try {
                                    if(input.getText().length() == 0 || Integer.parseInt(input.getText()) > mirsItem.getQuantity()) {
                                        AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid quantity!",
                                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                    }else {
                                        int reqQty = Integer.parseInt(input.getText());
                                        MIRSItem parMirsItem = new MIRSItem();
                                        parMirsItem.setId(mirsItem.getId());
                                        parMirsItem.setMirsID(mirsItem.getMirsID());
                                        parMirsItem.setStockID(mirsItem.getStockID());
                                        parMirsItem.setQuantity(reqQty);
                                        parMirsItem.setPrice(mirsItem.getPrice());
                                        parMirsItem.setRemarks(mirsItem.getRemarks());
                                        parMirsItem.setCreatedAt(mirsItem.getCreatedAt());
                                        parMirsItem.setUpdatedAt(mirsItem.getUpdatedAt());
                                        parMirsItem.setAdditional(false);
                                        releasingItem.add(parMirsItem);
                                        mirsItem.setQuantity(mirsItem.getQuantity()-reqQty);
                                        for (MIRSItem m : requestedItem){
                                            if(m.getId().equals(mirsItem.getId())){
                                                if(m.getQuantity() == 0) {
                                                    requestedItem.remove(m);
                                                }
                                                break;
                                            }
                                        }
                                        requestedItemTable.refresh();
                                        releasingItemTable.refresh();

                                    }
                                } catch (Exception e) {
                                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                }
                                dialog.close();
                            }
                        });
                    }
                }
            }
        }else if (countSelected > 1){
            AlertDialogBuilder.messgeDialog("System Message", "Can not perform multiple selection on partial release of item, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }
    }

    @FXML
    void addNewMIRSItem(ActionEvent event) throws Exception {
        if(selectedStock == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if(quantity.getText().length() == 0 || Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            MIRSItem mirsItem = new MIRSItem();
            mirsItem.setMirsID(mirs.getId());
            mirsItem.setStockID(selectedStock.getId());
            mirsItem.setParticulars(selectedStock.getDescription());
            mirsItem.setUnit(selectedStock.getUnit());
            mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
            mirsItem.setPrice(selectedStock.getPrice());
            mirsItem.setId(Utility.generateRandomId());
            mirsItem.setAdditional(true);
            additionalMirsItem.add(mirsItem);
            releasingItem.add(mirsItem);
            releasingItemTable.refresh();

            selectedStock = null; //set to null for validation
            stockItem.setText("");
            quantity.setText("");
            stockItem.requestFocus();
            inStock.setText("In Stock: 0");
            pending.setText("Pending: 0");
            available.setText("Available: 0");
        }
    }

    @FXML
    void releaseItem(ActionEvent event) {
        if(releasingItem.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Message", "No available item(s) listed for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        JFXButton accept = new JFXButton("Accept");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Releasing","Are sure you want to release listed item(s)?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try{
                    List<MIRSItem> forReleasing = releasingItem;
                    List<MIRSItem> remainingRequest = requestedItem;
                    List<Releasing> readyForRelease = new ArrayList<>();

                    for (MIRSItem mirsItem : forReleasing){
                        Stock stock = StockDAO.get(mirsItem.getStockID());
                        if(stock.isIndividualized()){
                            boolean found = false;
                            HashMap<String, ItemizedMirsItem> holder = Utility.getItemizedMirsItems();
                            for (Map.Entry i : holder.entrySet()) {
                                ItemizedMirsItem item = holder.get(i.getKey());
                                if(item.getMirsItemID().equals(mirsItem.getId())){
                                    found = true;
                                    break;
                                }
                            }
                            if(!found){
                                AlertDialogBuilder.messgeDialog("System Message", "Item specification is needed for the item " +stock.getDescription(), Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                                return;
                            }
                        }
                    }

                    for (MIRSItem mirsItem : forReleasing){
                        Releasing releasing = new Releasing();
                        releasing.setStockID(mirsItem.getStockID());
                        releasing.setMirsID(mirsItem.getMirsID());
                        releasing.setQuantity(mirsItem.getQuantity());
                        releasing.setPrice(mirsItem.getPrice());
                        releasing.setUserID(ActiveUser.getUser().getId());

                        boolean found = false;
                        /*
                        for(MIRSItem rem : remainingRequest){
                            if(rem.getId().equals(mirsItem.getId())) {
                                releasing.setStatus(Utility.PARTIAL_RELEASED);
                                found = true;
                                break;
                            }
                        }
                        */

                        for(MIRSItem rem : remainingRequest) {
                            if (StockDAO.get(rem.getStockID()).getDescription().equals(StockDAO.get(mirsItem.getStockID()).getDescription())) {
                                releasing.setStatus(Utility.PARTIAL_RELEASED);
                                found = true;
                                break;
                            }
                        }

                        if(!found) {
                            releasing.setStatus(Utility.RELEASED);
                            //ReleasingDAO.updateReleasedItem(releasing);
                        }
                        readyForRelease.add(releasing);
                        //ReleasingDAO.add(releasing);
                        //Stock temp = StockDAO.get(mirsItem.getStockID()); //temp stock object for quantity deduction
                        //StockDAO.deductStockQuantity(temp, mirsItem.getQuantity());
                    }

                    if(requestedItem.size() == 0)
                        mirs.setStatus(Utility.CLOSED);
                    else
                        mirs.setStatus(Utility.RELEASING);

                    //MirsDAO.update(mirs);

                    if(ReleasingDAO.add(readyForRelease, additionalMirsItem, mirs)) {
                        //clear hashmap that contains all itemized records
                        Utility.getItemizedMirsItems().clear();
                        String notif_details = "MIRS (" + mirs.getId() + ") was released.";
                        Notifications torequisitioner = new Notifications(notif_details, Utility.NOTIF_INFORMATION, ActiveUser.getUser().getEmployeeID(), mirs.getRequisitionerID(), mirs.getId());
                        NotificationsDAO.create(torequisitioner);
                        releasingItem.clear();
                        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(MIRSReleasingController.class, "../warehouse_dashboard_controller.fxml"));
                        AlertDialogBuilder.messgeDialog("System Message", "MIRS items released.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                    }else{
                        AlertDialogBuilder.messgeDialog("System Message", "Sorry an error was encountered during saving, please try again.",
                                Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    }
                    dialog.close();
                }catch (Exception e){
                    AlertDialogBuilder.messgeDialog("System Error", "Item released: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                    e.printStackTrace();
                }
            }
        });
    }

    /*@FXML
    private void releaseItem(ActionEvent event) {
        try {
            if(releasingList.getItems().size() == 0) {
                AlertDialogBuilder.messgeDialog("System Message", "No available item(s) listed for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Releasing","Are sure you want to release listed item(s)?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    save(null);
                    dialog.close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void releaseMCTItem(ActionEvent event) {
        try {
            if(releasingList.getItems().size() == 0) {
                AlertDialogBuilder.messgeDialog("System Message", "No available item(s) listed for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            JFXButton accept = new JFXButton("Accept");
            JFXTextField townCode = new JFXTextField();
            InputValidation.restrictNumbersOnly(townCode);
            JFXDialog dialogMCTNumber = DialogBuilder.showInputDialog("Release wth MCT","Provide Town Code:  ", "", townCode, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    if(townCode.getText().isEmpty()) {
                        AlertDialogBuilder.messgeDialog("System Message", "No MCT number provided", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                    }else{
                        JFXButton release = new JFXButton("Accept");
                        release.setDefaultButton(true);
                        release.getStyleClass().add("JFXButton");
                        release.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));

                        JFXTextField mctNumber = new JFXTextField();
                        mctNumber.setStyle("-fx-alignment: center; -fx-text-fill: "+ ColorPalette.BLACK + ";");
                        mctNumber.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 18));
                        mctNumber.setLabelFloat(false);
                        String mctNum = NumberGenerator.mctNumber(townCode.getText());
                        mctNumber.setText(mctNum);
                        JFXDialog dialogReleasing = DialogBuilder.showInputDialog("Releasing","Are sure you want to release listed item(s)?", Orientation.HORIZONTAL, mctNumber, release, DialogBuilder.WARNING_DIALOG, Utility.getStackPane());

                        release.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent __) {
                                if(mctNumber.getText().isEmpty()) {
                                    dialogReleasing.close();
                                    AlertDialogBuilder.messgeDialog("System Message", "No MCT number provided", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                                    return;
                                }else{
                                    save(mctNum);
                                }
                                dialogReleasing.close();
                            }
                        });
                    }
                    dialogMCTNumber.close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save(String mctNumber) {
        try{
            List<MIRSItem> forReleasing = releasingList.getItems();
            List<MIRSItem> remainingRequest = requestedList.getItems();
            List<Releasing> readyForRelease = new ArrayList<>();
            MCT mct = null;

            for (MIRSItem mirsItem : forReleasing){
                Stock stock = StockDAO.get(mirsItem.getStockID());
                if(stock.isIndividualized()){
                    boolean found = false;
                    HashMap<String, ItemizedMirsItem> holder = Utility.getItemizedMirsItems();
                    for (Map.Entry i : holder.entrySet()) {
                        ItemizedMirsItem item = holder.get(i.getKey());
                        if(item.getMirsItemID().equals(mirsItem.getId())){
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        AlertDialogBuilder.messgeDialog("System Message", "Item specification is needed for the item " +stock.getDescription(), Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                        return;
                    }
                }
            }

            for (MIRSItem mirsItem : forReleasing){
                Releasing releasing = new Releasing();
                releasing.setStockID(mirsItem.getStockID());
                releasing.setMirsID(mirsItem.getMirsID());
                releasing.setQuantity(mirsItem.getQuantity());
                releasing.setPrice(mirsItem.getPrice());
                releasing.setUserID(ActiveUser.getUser().getId());

                boolean found = false;
                for(MIRSItem rem : remainingRequest){
                    if(rem.getId().equals(mirsItem.getId())) {
                        releasing.setStatus(Utility.PARTIAL_RELEASED);
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    releasing.setStatus(Utility.RELEASED);
                    //ReleasingDAO.updateReleasedItem(releasing);
                }
                readyForRelease.add(releasing);
                //ReleasingDAO.add(releasing);
                //Stock temp = StockDAO.get(mirsItem.getStockID()); //temp stock object for quantity deduction
                //StockDAO.deductStockQuantity(temp, mirsItem.getQuantity());
            }

            if(mctNumber != null) {
                mct = new MCT();
                mct.setMctNo(mctNumber);
                mct.setParticulars(mirs.getPurpose());
                mct.setAddress(mirs.getAddress());
                mct.setMirsNo(mirs.getId());
                mct.setWorkOrderNo(mirs.getWorkOrderNo());
                //MCTDao.create(mct, readyForRelease);
            }

            if(requestedList.getItems().size() == 0)
                mirs.setStatus(Utility.CLOSED);
            else
                mirs.setStatus(Utility.RELEASING);

            //MirsDAO.update(mirs);

            if(ReleasingDAO.add(readyForRelease, additionalMirsItem, mct, mirs)) {
                //clear hashmap that contains all itemized records
                Utility.getItemizedMirsItems().clear();
                String notif_details = "MIRS (" + mirs.getId() + ") was released.";
                Notifications torequisitioner = new Notifications(notif_details, Utility.NOTIF_INFORMATION, ActiveUser.getUser().getEmployeeID(), mirs.getRequisitionerID(), mirs.getId());
                NotificationsDAO.create(torequisitioner);
                releasingList.getItems().clear();
                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(MIRSReleasingController.class, "../warehouse_dashboard_controller.fxml"));
                AlertDialogBuilder.messgeDialog("System Message", "MIRS items released.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
            }else{
                AlertDialogBuilder.messgeDialog("System Message", "Sorry an error was encountered during saving, please try again.",
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
        }catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Error", "Item released: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewMIRSItem(ActionEvent event) throws Exception {
        if(selectedStock == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(quantity.getText().length() == 0 || Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setMirsID(mirsNumber.getText());
        mirsItem.setStockID(selectedStock.getId());
        mirsItem.setParticulars(selectedStock.getDescription());
        mirsItem.setUnit(selectedStock.getUnit());
        mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
        mirsItem.setPrice(selectedStock.getPrice());
        mirsItem.setId(Utility.generateRandomId());
        mirsItem.setAdditional(true);
        additionalMirsItem.add(mirsItem);
        releasingList.getItems().add(mirsItem);

        selectedStock = null; //set to null for validation
        particulars.setText("");
        quantity.setText("");
        particulars.requestFocus();
        inStock.setText("In Stock: 0");
        pending.setText("Pending: 0");
        available.setText("Available: 0");
    }

    @FXML
    private void addAllQty(ActionEvent event) throws Exception {
        MIRSItem selected = requestedList.getSelectionModel().getSelectedItem();
        if (selected == null){
            AlertDialogBuilder.messgeDialog("System Message", "No item(s) selected, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        ObservableList<MIRSItem> selectedItems = FXCollections.observableArrayList(requestedList.getSelectionModel().getSelectedItems());
        if(selectedItems.size() == 1){

        }else {

        }

        if(StockDAO.hasMultiple(StockDAO.get(selected.getStockID()).getDescription())){
            Utility.setSelectedObject(selected);
            ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mirs_releasing_select_item.fxml", Utility.getStackPane());
        }else{
            addRemoveItem(addAllQtyBtn);
        }
    }

    @FXML
    private void removeItem(ActionEvent event) {
        addRemoveItem(removeItemBtn);
    }

    @FXML
    private void addPartialQty(ActionEvent event) {
        addRemoveItem(addPartialQtyBtn);
    }

    private void addRemoveItem(JFXButton btn){
        JFXListView<MIRSItem> mirsItemJFXListView = null;
        if(btn == this.addAllQtyBtn){
            mirsItemJFXListView = requestedList;
        }else if(btn == this.addPartialQtyBtn){
            mirsItemJFXListView = requestedList;
        }else if(btn == this.removeItemBtn){
            mirsItemJFXListView = releasingList;
        }

        if(mirsItemJFXListView.getItems().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "No available item.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }else{
            ObservableList<MIRSItem> selectedItems = FXCollections.observableArrayList(mirsItemJFXListView.getSelectionModel().getSelectedItems());
            if(selectedItems.size() == 0){
                AlertDialogBuilder.messgeDialog("System Message", "No item(s) selected, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }else{
                if(btn == addAllQtyBtn) {
                    ObservableList<MIRSItem> toBeReleased = FXCollections.observableArrayList(releasingList.getItems());
                    for (MIRSItem selected : selectedItems) {

                        boolean found = false;
                        for (MIRSItem listed : toBeReleased) {
                            if (listed.getId().equals(selected.getId())) {
                                listed.setQuantity(listed.getQuantity() + selected.getQuantity());
                                requestedList.getItems().remove(selected);
                                found = true;
                            }
                        }
                        if (!found) {
                            requestedList.getItems().remove(selected);
                            releasingList.getItems().add(selected);
                        }

                        requestedList.refresh();
                        releasingList.refresh();
                    }
                }else if(btn == removeItemBtn){
                    ObservableList<MIRSItem> requested = FXCollections.observableArrayList(requestedList.getItems());
                    for (MIRSItem selected : selectedItems) {

                        if(selected.getId() == null){
                            releasingList.getItems().removeAll(additionalMirsItem); //remove additional items from the requested list
                            releasingList.getItems().remove(selected); //remove selected item from the releasing list
                            additionalMirsItem.remove(selected); //remove additional items from the additionalMirsItem list
                            break;
                        }

                        boolean found = false;
                        for (MIRSItem listed : requested) {
                            if (listed.getId().equals(selected.getId())) {
                                listed.setQuantity(listed.getQuantity() + selected.getQuantity());
                                releasingList.getItems().remove(selected);
                                found = true;
                            }
                        }
                        if (!found) {
                            requestedList.getItems().add(selected);
                            releasingList.getItems().remove(selected);
                        }

                        requestedList.refresh();
                        releasingList.refresh();
                    }
                }else if (btn == addPartialQtyBtn){
                    if(selectedItems.size() > 1){
                        AlertDialogBuilder.messgeDialog("System Message", "Can not perform multiple selection on partial release of item, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    }else{
                        JFXButton accept = new JFXButton("Accept");
                        JFXTextField input = new JFXTextField();
                        InputValidation.restrictNumbersOnly(input);
                        JFXDialog dialog = DialogBuilder.showInputDialog("Partial Quantity","Enter desired quantity:  ", "Requested quantity: "+ selectedItems.get(0).getQuantity(), input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
                        accept.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent __) {
                                try {
                                    if(input.getText().length() == 0 || Integer.parseInt(input.getText()) > selectedItems.get(0).getQuantity()) {
                                        AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid partial quantity!",
                                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                    }else{

                                        ObservableList<MIRSItem> toBeReleased = FXCollections.observableArrayList(releasingList.getItems());
                                        boolean found = false;
                                        for (MIRSItem listed : toBeReleased) {
                                            if (listed.getId().equals(selectedItems.get(0).getId())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (found) {
                                            AlertDialogBuilder.messgeDialog("System Message", "Please remove item to change partial release quantity.",
                                                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                        }else{
                                            MIRSItem mirsItem = new MIRSItem();
                                            mirsItem.setId(selectedItems.get(0).getId());
                                            mirsItem.setMirsID(selectedItems.get(0).getMirsID());
                                            mirsItem.setStockID(selectedItems.get(0).getStockID());
                                            mirsItem.setQuantity(Integer.parseInt(input.getText()));
                                            mirsItem.setPrice(selectedItems.get(0).getPrice());
                                            mirsItem.setRemarks(selectedItems.get(0).getRemarks());
                                            mirsItem.setCreatedAt(selectedItems.get(0).getCreatedAt());
                                            mirsItem.setUpdatedAt(selectedItems.get(0).getUpdatedAt());
                                            mirsItem.setAdditional(selectedItems.get(0).isAdditional());
                                            //mirsItem.setWorkOrderNo(selectedItems.get(0).getWorkOrderNo());

                                            selectedItems.get(0).setQuantity(selectedItems.get(0).getQuantity() - Integer.parseInt(input.getText()));
                                            releasingList.getItems().add(mirsItem);

                                            if(selectedItems.get(0).getQuantity() == 0)
                                                requestedList.getItems().remove(selectedItems.get(0));
                                        }
                                        requestedList.refresh();
                                        releasingList.refresh();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.close();
                            }
                        });
                    }
                }

            }
        }
        releasingList.getSelectionModel().clearSelection();
        requestedList.getSelectionModel().clearSelection();
    }

    @FXML
    private void addMoreDetails(ActionEvent event) throws Exception {
        MIRSItem mirsItem = releasingList.getSelectionModel().getSelectedItem();
        if(mirsItem == null) {
            AlertDialogBuilder.messgeDialog("System Message", "Please select an item from the Releasing MIRS list.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            if(StockDAO.get(mirsItem.getStockID()).isIndividualized()) {
                Utility.setSelectedObject(releasingList.getSelectionModel().getSelectedItem());
                if (Utility.getSelectedObject() != null)
                    ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mirs_item_itemized.fxml", Utility.getStackPane());
            }else{
                AlertDialogBuilder.messgeDialog("System Message", "Item can not be individualized.",
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
        }

    }

     */

    private void bindParticularsAutocomplete(JFXTextField textField){
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
                        selectedStock = null;
                        quantity.setText("");
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
                selectedStock = StockDAO.get(result.getId());
                int av = StockDAO.countAvailable(selectedStock);
                if(av == 0) {
                    AlertDialogBuilder.messgeDialog("System Warning", "Insufficient stock.",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    stockItem.setText("");
                    selectedStock = null;
                }else{

                    List<MIRSItem> requests = requestedItem;
                    for(MIRSItem r : requests){
                        if(selectedStock.getId().equals(r.getStockID())){
                            AlertDialogBuilder.messgeDialog("System Message", "Can not add addition "+selectedStock.getDescription()+" ,since item can still be listed as item for releasing.",
                                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                            selectedStock = null;
                            stockItem.setText("");
                            quantity.setText("");
                            return;
                        }
                    }
                    quantity.requestFocus();
                    inStock.setText("In Stock: "+ selectedStock.getQuantity());
                    pending.setText("Pending: "+ (StockDAO.countPendingRequest(selectedStock)));
                    available.setText("Available: "+ (av));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

    @Override
    public void receive(Object o) {
        MIRSItem mirsItem = (MIRSItem) o;
        releasingItem.add(mirsItem);

        for (MIRSItem m : requestedItem){
            if(m.getId().equals(mirsItem.getId())){
                if(m.getQuantity() == 0) {
                    requestedItem.remove(m);
                }
                break;
            }
        }
        requestedItemTable.refresh();
        releasingItemTable.refresh();
    }
}
