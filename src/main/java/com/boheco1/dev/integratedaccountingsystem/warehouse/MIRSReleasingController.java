package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.*;

public class MIRSReleasingController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private Label details, inStock, pending, available, date, itemCounter, mirsNumber, applicant, requisitioner, address, signatories, purpose;

    @FXML
    private JFXTextField particulars, quantity;

    @FXML
    private JFXButton addAllQtyBtn, addPartialQtyBtn, removeItemBtn, detailstemBtn;

    @FXML
    private JFXListView<MIRSItem> requestedList, releasingList;

    private Stock selectedStock = null;
    private List<MIRSItem> additionalMirsItem = new ArrayList<>();
    private MIRS mirs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        InputValidation.restrictNumbersOnly(quantity);
        requestedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        releasingList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bindParticularsAutocomplete(particulars);
        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSItem> mirsItemList = MirsDAO.getUnreleasedItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=EmployeeDAO.getOne(sig.getUserID(),DB.getConnection()).getFullName();
                signatures+="\n";
            }
            signatories.setText(signatures);

            initializeItemList(mirsItemList);

            itemCounter.setText(mirsItemList.size()+" items found");
            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            address.setText(mirs.getAddress());
            applicant.setText(mirs.getApplicant());
            requisitioner.setText(mirs.getRequisitioner().getFullName());

            //clear hashmap that contains all itemized records
            Utility.getItemizedMirsItems().clear();
            Utility.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeItemList(List<MIRSItem> mirsItemList) throws Exception {
        for (MIRSItem item: mirsItemList) {
            item.setQuantity(MirsDAO.getBalance(item));
            requestedList.getItems().add(item);
        }
    }

    @FXML
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
        if (selected == null) return;

        if(!StockDAO.hasMultiple(StockDAO.get(selected.getStockID()).getDescription())){
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
                    particulars.setText("");
                    selectedStock = null;
                }else{

                    List<MIRSItem> requests = requestedList.getItems();
                    for(MIRSItem r : requests){
                        if(selectedStock.getId().equals(r.getStockID())){
                            AlertDialogBuilder.messgeDialog("System Message", "Can not add addition "+selectedStock.getDescription()+" ,since item can still be listed as item for releasing.",
                                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                            selectedStock = null;
                            particulars.setText("");
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
        System.out.println(mirsItem);
    }
}
