package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class GenerateMCTController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField newAddress, searchMirs, mctNumber;

    @FXML
    private JFXTextArea particulars;

    @FXML
    private Label purpose, requisitioner, mirsNumber, date, address, applicant, signatories;

    @FXML
    private JFXListView<UnchargedItemDetails> unchargedItemDetailsListView, forMctItemListView;

    @FXML
    private JFXButton addAllQtyBtn, addPartialQtyBtn, removeItemBtn;

    private HashMap<String, List<UnchargedItemDetails>> collectionOfUnchargeReleasedItem;

    private MIRS searchResult;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        unchargedItemDetailsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        forMctItemListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bindMirsAutocomplete(searchMirs);

        collectionOfUnchargeReleasedItem = new HashMap<>();

        try {
            List<UnchargedMIRSReleases> unchargedMIRSReleasesList = MirsDAO.getUnchargedMIRSReleases();
            for(UnchargedMIRSReleases un : unchargedMIRSReleasesList){
                collectionOfUnchargeReleasedItem.put(un.getMirs().getId(),un.getReleases());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void searchMirs(ActionEvent event) {

       /*try {
            mirs = MirsDAO.(searchMirs.getText());
            List<MIRSItem> mirsItemList = MirsDAO.getUnreleasedItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=UserDAO.get(sig.getUserID()).getFullName();
                signatures+="\n";
            }
            signatories.setText(signatures);

            initializeItemList(mirsItemList);

            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            address.setText(mirs.getAddress());
            applicant.setText(mirs.getApplicant());
            requisitioner.setText(mirs.getRequisitioner().getFullName());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void initializeItemList(List<MIRSItem> mirsItemList) throws Exception {
        /*for (MIRSItem item: mirsItemList) {
            item.setQuantity(MirsDAO.getBalance(item));
            requestedList.getItems().add(item);
        }*/
    }

    private void save() {
        try{
            AlertDialogBuilder.messgeDialog("System Message", "MIRS items released.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Error", "Item released: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }
    }

    @FXML
    private void addAllQty(ActionEvent event) throws Exception {
        //addRemoveItem(addAllQtyBtn);
        //public MCT(String mctNo, String particulars, String address, String mirsNo, String workOrderNo, LocalDate createdAt) {
        ObservableList<UnchargedItemDetails> selectedItems = FXCollections.observableArrayList(unchargedItemDetailsListView.getSelectionModel().getSelectedItems());
        if(selectedItems.size() == 0){
            AlertDialogBuilder.messgeDialog("System Message", "No item(s) selected, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }else{
            for (UnchargedItemDetails selected : selectedItems) {
                unchargedItemDetailsListView.getItems().remove(selected);
                forMctItemListView.getItems().add(selected);
            }

        }
        unchargedItemDetailsListView.getSelectionModel().clearSelection();
        forMctItemListView.getSelectionModel().clearSelection();
        /*MCT mct = new MCT();
                mct.setMctNo(mctNumber.getText());
                mct.setParticulars(particulars.getText());
                mct.setAddress(address.getText());
                mct.setMirsNo(mirsNumber.getText());
                mct.setWorkOrderNo(searchResult.getWorkOrderNo());*/
    }

    @FXML
    private void removeItem(ActionEvent event) {
        //addRemoveItem(removeItemBtn);
        ObservableList<UnchargedItemDetails> selectedItems = FXCollections.observableArrayList(forMctItemListView.getSelectionModel().getSelectedItems());
        if(selectedItems.size() == 0){
            AlertDialogBuilder.messgeDialog("System Message", "No item(s) selected, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }else{
            for (UnchargedItemDetails selected : selectedItems) {
                unchargedItemDetailsListView.getItems().add(selected);
                forMctItemListView.getItems().remove(selected);
            }
        }
        unchargedItemDetailsListView.getSelectionModel().clearSelection();
        forMctItemListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void addPartialQty(ActionEvent event) {
        addRemoveItem(addPartialQtyBtn);

    }

    @FXML
    private void saveMCT(ActionEvent event) {
            try {
            if(forMctItemListView.getItems().size() == 0) {
                AlertDialogBuilder.messgeDialog("System Message", "No available item(s) listed for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("MCT","Are sure you want to save this MCT?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    save();
                    dialog.close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void genMctNum(ActionEvent event) {
        mctNumber.setText(NumberGenerator.mctNumber(mctNumber.getText()));
    }

    private void addRemoveItem(JFXButton btn){
        /*JFXListView itemListView = null;
        if(btn == this.addAllQtyBtn || btn == this.addPartialQtyBtn){
            itemListView = releasedItemListView;
        }else if(btn == this.removeItemBtn){
            itemListView = mctItemListView;
        }

        if(itemListView.getItems().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "No available item.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        }else{
            ObservableList<UnchargedItemDetails> selectedItems = FXCollections.observableArrayList(itemListView.getSelectionModel().getSelectedItems());
            if(selectedItems.size() == 0){
                AlertDialogBuilder.messgeDialog("System Message", "No item(s) selected, please try again.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }else{
            //   ReleasedItemDetails temp = new ReleasedItemDetails();
                if(btn == addAllQtyBtn) {
                    ObservableList<UnchargedItemDetails> toBeReleased = FXCollections.observableArrayList(releasedItemListView.getItems());
                    for (UnchargedItemDetails selected : selectedItems) {
                        temp.setMirsID(selected.);
                        boolean found = false;
                        for (UnchargedItemDetails listed : toBeReleased) {
                            if (listed.getId().equals(temp.getStockID())) {
                                listed.setQuantity(listed.getQuantity() + temp.getQuantity());
                                releasedItemListView.getItems().remove(temp);
                                found = true;
                            }
                        }
                        if (!found) {
                            releasedItemListView.getItems().remove(temp);
                            releasedItemListView.getItems().add(temp);
                        }

                        releasedItemListView.refresh();
                        releasedItemListView.refresh();
                    }
                }else if(btn == removeItemBtn){
                    ObservableList<MIRSItem> requested = FXCollections.observableArrayList(releasedItemListView.getItems());
                    for (MIRSItem selected : selectedItems) {

                        if(selected.getId() == null){
                            releasedItemListView.getItems().remove(selected);
                            break;
                        }

                        boolean found = false;
                        for (MIRSItem listed : requested) {
                            if (listed.getId().equals(selected.getId())) {
                                listed.setQuantity(listed.getQuantity() + selected.getQuantity());
                                releasedItemListView.getItems().remove(selected);
                                found = true;
                            }
                        }
                        if (!found) {
                            releasedItemListView.getItems().add(selected);
                            releasedItemListView.getItems().remove(selected);
                        }

                        releasedItemListView.refresh();
                        releasedItemListView.refresh();
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

                                        ObservableList<MIRSItem> toBeReleased = FXCollections.observableArrayList(releasedItemListView.getItems());
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

                                            selectedItems.get(0).setQuantity(selectedItems.get(0).getQuantity() - Integer.parseInt(input.getText()));
                                            releasedItemListView.getItems().add(mirsItem);

                                            if(selectedItems.get(0).getQuantity() == 0)
                                                releasedItemListView.getItems().remove(selectedItems.get(0));
                                        }
                                        releasedItemListView.refresh();
                                        releasedItemListView.refresh();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.close();
                            }
                        });
                    }
                }*/

         //   }
        //}
       // mctItemListView.getSelectionModel().clearSelection();
       // releasedItemListView.getSelectionModel().clearSelection();
    }

    private void setFieldData(MIRS mirs) throws Exception {

        List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

        String signatures = "";
        for (MIRSSignatory sig:mirsSignatoryList) {
            signatures+=UserDAO.get(sig.getUserID()).getFullName();
            signatures+="\n";
        }
        signatories.setText(signatures);
        mirsNumber.setText(""+mirs.getId());
        date.setText(""+mirs.getDateFiled());
        purpose.setText(mirs.getPurpose());
        address.setText(mirs.getAddress());
        applicant.setText(mirs.getApplicant());
        requisitioner.setText(mirs.getRequisitioner().getFullName());

        newAddress.setText(mirs.getAddress());
        particulars.setText(mirs.getPurpose());

        unchargedItemDetailsListView.getItems().clear();
        List<UnchargedItemDetails> collection = collectionOfUnchargeReleasedItem.get(mirs.getId());
        if (collection != null){
            unchargedItemDetailsListView.getItems().addAll(collection);
        }
    }

    private void bindMirsAutocomplete(JFXTextField textField){
        AutoCompletionBinding<MIRS> suggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<MIRS> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = MirsDAO.searchMIRS(query);
                            MIRS dummy = new MIRS();
                            dummy.setId("dummy");
                            dummy.setPurpose("--end of result--");//just to add padding between last item and horizontal scrollbar
                            list.add(dummy);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(MIRS object) {
                        return object.getPurpose();
                    }

                    @Override
                    public MIRS fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        suggest.setMinWidth(500);
        //This will set the actions once the user clicks an item from the popupmenu.
        suggest.setOnAutoCompleted(event -> {
            textField.setText("");
            searchResult = event.getCompletion();
            if(searchResult.getId().equals("dummy"))
                return;
            try {
                setFieldData(searchResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
