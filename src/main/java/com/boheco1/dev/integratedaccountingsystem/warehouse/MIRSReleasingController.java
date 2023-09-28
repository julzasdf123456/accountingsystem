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
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
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
    private JFXButton addAllQtyBtn, addPartialQtyBtn, removeItemBtn, detailstemBtn, checkAllBtn;

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

    private HashMap<String, Double> selected_items = new HashMap<>();

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
        reqDescriptionCol.setStyle("-fx-alignment: CENTER-LEFT;");
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> reqDescriptioncellFactory
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
                                    try {

                                        MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                        String description ;
                                        if(mirsItem.getRemarks().contains("Controlled"))
                                            description = "CON-"+mirsItem.getParticulars();
                                        else
                                            description = mirsItem.getParticulars();
                                        Text text = new Text(description);
                                        text.setStyle("" +
                                                "-fx-fill: #212121; " +
                                                "-fx-alignment: center-left;" +
                                                "-fx-text-wrap: true;");
                                        //setStyle("-fx-background-color: #f7e1df; -fx-text-alignment: center-left;");
                                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                                        setPrefHeight(text.getLayoutBounds().getHeight()+10);
                                        setMinHeight(text.getLayoutBounds().getHeight()+10);
                                        setGraphic(text);
                                        setText(null);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        };
                        return cell;
                    }
                };
        reqDescriptionCol.setCellFactory(reqDescriptioncellFactory);
        /*reqDescriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });*/

        TableColumn<MIRSItem, String> reqQuantityCol = new TableColumn<>("Qty");
        reqQuantityCol.setStyle("-fx-alignment: center;");
        reqQuantityCol.setPrefWidth(80);
        reqQuantityCol.setMaxWidth(80);
        reqQuantityCol.setMinWidth(80);
        //reqQuantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
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
                                    setGraphic(null);
                                    setText(Utility.formatDecimal(mirsItem.getQuantity()));

                                }
                            }
                        };
                        return cell;
                    }
                };

        reqQuantityCol.setCellFactory(qtycellFactory);

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
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> relDescriptioncellFactory
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
                                    try {
                                        MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                        String description ;
                                        if(mirsItem.getRemarks().contains("Controlled"))
                                            description = "CON-"+mirsItem.getParticulars();
                                        else
                                            description = mirsItem.getParticulars();
                                        Text text = new Text(description);
                                        text.setStyle("" +
                                                "-fx-fill: #212121; " +
                                                "-fx-alignment: center-left;" +
                                                "-fx-text-wrap: true;");
                                        //setStyle("-fx-background-color: #f7e1df; -fx-text-alignment: center-left;");
                                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                                        setPrefHeight(text.getLayoutBounds().getHeight()+10);
                                        setMinHeight(text.getLayoutBounds().getHeight()+10);
                                        setGraphic(text);
                                        setText(null);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        };
                        return cell;
                    }
                };
        relDescriptionCol.setCellFactory(relDescriptioncellFactory);
        /*relDescriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });*/

        TableColumn<MIRSItem, String> relQuantityCol = new TableColumn<>("Qty");
        relQuantityCol.setStyle("-fx-alignment: center;");
        relQuantityCol.setPrefWidth(50);
        relQuantityCol.setMaxWidth(50);
        relQuantityCol.setMinWidth(50);
        relQuantityCol.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getQuantity())));

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
                                                    if(!selected_items.isEmpty()){
                                                        //Get quantity of stock in the hashmap
                                                        double qty_current = selected_items.get(mirsItem.getStockID());
                                                        //Deduct from the current quantity and update value in hashmap
                                                        selected_items.put(mirsItem.getStockID(), qty_current - mirsItem.getQuantity());
                                                    }

                                                    if(!mirsItem.isAdditional()) {
                                                        boolean found = false;
                                                        for (MIRSItem m : requestedItem){
                                                            if(m.getId().equals(mirsItem.getId())){
                                                                double updateQty = m.getQuantity() + mirsItem.getQuantity();
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
    private void checkAll(ActionEvent event) {
        if(checkAllBtn.getText().equals("Check All")){
            checkAllBtn.setText("Uncheck All");
        }else if(checkAllBtn.getText().equals("Uncheck All")){
            checkAllBtn.setText("Check All");
        }
        for(MIRSItem m : requestedMirsItem){
            if(m.getParticulars().toLowerCase().contains("current") ||
                    m.getParticulars().toLowerCase().contains("fuse")||
                    m.getParticulars().toLowerCase().contains("Pole"))
                continue;
            m.setSelected(!m.isSelected());
        }
        requestedItemTable.refresh();
    }

    @FXML
    private void mirsDetails(MouseEvent event) throws Exception {

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
                        Utility.setDictionary(selected_items);
                        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mirs_releasing_select_item.fxml", Utility.getStackPane());
                    }else{
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
                                    if(input.getText().length() == 0 || Double.parseDouble(input.getText()) > mirsItem.getQuantity()) {
                                        AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid quantity!",
                                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                    }else {
                                        double reqQty = Double.parseDouble(input.getText());
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
        }else if(quantity.getText().length() == 0 || Double.parseDouble(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            MIRSItem mirsItem = new MIRSItem();
            mirsItem.setMirsID(mirs.getId());
            mirsItem.setStockID(selectedStock.getId());
            mirsItem.setParticulars(selectedStock.getDescription());
            mirsItem.setUnit(selectedStock.getUnit());
            mirsItem.setQuantity(Double.parseDouble(quantity.getText()));
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
                            //if (StockDAO.get(rem.getStockID()).getDescription().equals(StockDAO.get(mirsItem.getStockID()).getDescription())) {
                            if (rem.getStockID().equals(mirsItem.getStockID())) {
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
                        Utility.setActiveMIRS(mirs);
                        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(MIRSReleasingController.class, "../warehouse_mirs_releasing.fxml"));
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
                selectedStock.setQuantity(result.getQuantity());
                double av = StockDAO.countAvailable(selectedStock);
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
                    inStock.setText("In Stock: "+ Utility.formatDecimal(selectedStock.getQuantity()));
                    pending.setText("Pending: "+ Utility.formatDecimal(StockDAO.countPendingRequest(selectedStock)));
                    available.setText("Available: "+ Utility.formatDecimal(av));
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

    @Override
    public void receive(Object o) {
        if (o instanceof MIRSItem) {
            MIRSItem mirsItem = (MIRSItem) o;
            releasingItem.add(mirsItem);
            for (MIRSItem m : requestedItem) {
                if (m.getId().equals(mirsItem.getId())) {
                    if (m.getQuantity() == 0) {
                        requestedItem.remove(m);
                    }
                    break;
                }
            }
            requestedItemTable.refresh();
            releasingItemTable.refresh();
        }else{

        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
}
