package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.FontWeight;
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

public class GenerateMCTController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField description,newAddress, searchMirs, mctNumber;

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
        loadAllChargedItems(false);
    }

    @FXML
    private void addItemToListView(ActionEvent event) throws Exception {
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
    }

    @FXML
    private void removeItemFromListView(ActionEvent event) {
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
            }else if (description.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "MCT description is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }else if (newAddress.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "MCT address is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }else if (particulars.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "MCT particulars is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }

            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("MCT","Are sure you want to save this MCT?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    try{
                        /*Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                        );
                        fileChooser.setInitialFileName("MCT_report_"+mctNumber.getText()+".pdf");
                        File selectedFile = fileChooser.showSaveDialog(stage);
                        if (selectedFile != null) {
                            }*/
                        MCT mct = new MCT();
                        mct.setMctNo(mctNumber.getText());
                        mct.setParticulars(particulars.getText());
                        mct.setAddress(newAddress.getText());
                        //not really MIRS number but the MCT description, by default Mirst number serve as description
                        mct.setMirsNo(description.getText());
                        mct.setWorkOrderNo(searchResult.getWorkOrderNo());

                        List<Releasing> list = new ArrayList<>();
                        for (UnchargedItemDetails item : forMctItemListView.getItems()) {
                            Releasing r = new Releasing();
                            r.setId(item.getId());
                            r.setQuantity(item.getQuantity());
                            r.setPrice(item.getPrice());
                            r.setMirsID(searchMirs.getId());
                            r.setUserID(ActiveUser.getUser().getId());
                            list.add(r);
                        }

                        MCTDao.create(mct,list);

                        //PrintMCT.print(selectedFile, mct, list);
                        //loadAllUnchargeItems();

                        //reload the class
                        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, "warehouse_generate_mct.fxml"));
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

        unchargedItemDetailsListView.getItems().clear();
        List<UnchargedItemDetails> collection = collectionOfUnchargeReleasedItem.get(mirs.getId());
        if (collection != null) {
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=UserDAO.get(sig.getUserID()).getFullName();
                signatures+="\n";
            }
            signatories.setText(signatures);
            mirsNumber.setText(""+mirs.getId());

            if(forMctItemListView.getItems().size() >0){
                description.setText(description.getText()+"/"+mirs.getId());
            }else{
                description.setText(mirs.getId());
            }

            date.setText(""+mirs.getDateFiled());
            applicant.setText(mirs.getApplicant());
            requisitioner.setText(mirs.getRequisitioner().getFullName());

            newAddress.setText(mirs.getAddress());
            particulars.setText(mirs.getPurpose());

            unchargedItemDetailsListView.getItems().addAll(collection);

            //remove items that are already added to the list for MCT
            unchargedItemDetailsListView.getItems().removeAll(forMctItemListView.getItems());
        }else{
            AlertDialogBuilder.messgeDialog("System Message", "All released items under MIRS: " +mirs.getId() +" are ready for MCT issuance.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }
    }

    private void loadAllChargedItems(boolean loadAll){
        System.out.println(loadAll);
        collectionOfUnchargeReleasedItem = new HashMap<>();
        try {
            List<UnchargedMIRSReleases> mirsItemReleasesList = MirsDAO.getUnchargedMIRSReleases();
            if(loadAll)
                mirsItemReleasesList = MirsDAO.getChargedMIRSReleases();

            for(UnchargedMIRSReleases un : mirsItemReleasesList){
                collectionOfUnchargeReleasedItem.put(un.getMirs().getId(),un.getReleases());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
