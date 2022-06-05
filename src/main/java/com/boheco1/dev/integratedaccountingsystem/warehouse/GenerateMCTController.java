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
    private Label requisitioner, mirsNumber, date, applicant, signatories;

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
    private void addItemToListView(ActionEvent event) throws Exception {
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
        /**/
    }

    @FXML
    private void removeItemFromListView(ActionEvent event) {
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
    private void saveMCT(ActionEvent event) {
            try {
            if(forMctItemListView.getItems().size() == 0) {
                AlertDialogBuilder.messgeDialog("System Message", "No available item(s) listed for releasing.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            if (mctNumber.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "MCT number is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("MCT","Are sure you want to save this MCT?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    try{

                        MCT mct = new MCT();
                        mct.setMctNo(mctNumber.getText());
                        mct.setParticulars(particulars.getText());
                        mct.setAddress(newAddress.getText());
                        mct.setMirsNo(mirsNumber.getText());
                        mct.setWorkOrderNo(searchResult.getWorkOrderNo());

                        List<Releasing> list = new ArrayList<>();
                        for (UnchargedItemDetails item : forMctItemListView.getItems()) {
                            Releasing r = new Releasing();
                            r.setId(item.getId());
                            list.add(r);
                        }

                        MCTDao.create(mct,list);

                        AlertDialogBuilder.messgeDialog("System Message", "MCT successfully generated.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                    }catch (Exception e){
                        e.printStackTrace();
                        AlertDialogBuilder.messgeDialog("System Error", "MCT: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                    }
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
        applicant.setText(mirs.getApplicant());
        requisitioner.setText(mirs.getRequisitioner().getFullName());

        newAddress.setText(mirs.getAddress());
        particulars.setText(mirs.getPurpose());

        unchargedItemDetailsListView.getItems().clear();
        List<UnchargedItemDetails> collection = collectionOfUnchargeReleasedItem.get(mirs.getId());
        if (collection != null){
            unchargedItemDetailsListView.getItems().addAll(collection);
        }

        //remove items that are already added to the list for MCT
        unchargedItemDetailsListView.getItems().removeAll(forMctItemListView.getItems());

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
