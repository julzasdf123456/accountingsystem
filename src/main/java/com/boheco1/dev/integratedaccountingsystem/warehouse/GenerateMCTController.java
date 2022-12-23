package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.*;

public class GenerateMCTController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField searchMirs, mctNumber;

    @FXML
    private JFXTextArea purpose, applicant, newAddress, remarks;

    @FXML
    private TableView forMCTable;

    @FXML
    private JFXButton addAllQtyBtn, addPartialQtyBtn, removeItemBtn;

    private HashMap<String, List<UnchargedItemDetails>> collectionOfUnchargeReleasedItem;

    private ObservableList<UnchargedItemDetails> unchargedItemDetailsObservableList;
    private List<UnchargedItemDetails> unchargedItemDetailsList;

    private MIRS searchResult;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindMirsAutocomplete(searchMirs);
        loadAllUnchargedItems();
        initializeTable();
    }

    private void initializeTable() {

        TableColumn<UnchargedItemDetails, String> desCol = new TableColumn<>("Description");
        desCol.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getDescription()));

        TableColumn<UnchargedItemDetails, String> qtyCol = new TableColumn<>("QTY");
        qtyCol.setStyle("-fx-alignment: center;");
        qtyCol.setPrefWidth(50);
        qtyCol.setMaxWidth(50);
        qtyCol.setMinWidth(50);
        qtyCol.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getQuantity())));

        forMCTable.getColumns().add(desCol);
        forMCTable.getColumns().add(qtyCol);


        forMCTable.setPlaceholder(new Label("No item Added"));

    }


    @FXML
    private void saveMCT(ActionEvent event) {
        try {
            if (mctNumber.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "MCT number is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                return;
            }else if (purpose.getText().isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "MCT particulars is required.", Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
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
                        mct.setParticulars(purpose.getText());
                        mct.setAddress(newAddress.getText());
                        //not really MIRS number but the MCT description, by default Mirst number serve as description
                        mct.setMirsNo(searchResult.getId());
                        mct.setWorkOrderNo(searchResult.getWorkOrderNo());

                        List<Releasing> list = new ArrayList<>();
                        for (UnchargedItemDetails item : unchargedItemDetailsList) {
                            Releasing r = new Releasing();
                            r.setId(item.getId());
                            r.setQuantity(item.getQuantity());
                            r.setPrice(item.getPrice());
                            r.setMirsID(searchMirs.getId());
                            r.setUserID(ActiveUser.getUser().getId());
                            list.add(r);
                        }

                        String result = MCTDao.create(mct,list);
                        if(result == null){
                            Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, "warehouse_mct_generate.fxml"));
                            AlertDialogBuilder.messgeDialog("System Message", "MCT successfully generated.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                            loadAllUnchargedItems();
                        } else{
                            AlertDialogBuilder.messgeDialog("System Message", "Error encountered: "+result, Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        }

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

    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    private void setFieldData(MIRS mirs) throws Exception {

        unchargedItemDetailsList = collectionOfUnchargeReleasedItem.get(mirs.getId());
        if (unchargedItemDetailsList != null) {
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=EmployeeDAO.getOne(sig.getUserID(),DB.getConnection()).getFullName();
                signatures+="\n";
            }
            applicant.setText(mirs.getApplicant());
            newAddress.setText(mirs.getAddress());
            purpose.setText(mirs.getPurpose());
            remarks.setText(mirs.getDetails());

            unchargedItemDetailsObservableList =  FXCollections.observableArrayList(unchargedItemDetailsList);
            forMCTable.getItems().setAll(unchargedItemDetailsObservableList);
            forMCTable.refresh();
            //unchargedItemDetailsListView.getItems().addAll(collection);


            mctNumber.setText(NumberGenerator.mctNumber());

        }else{
            AlertDialogBuilder.messgeDialog("System Message", "All items under MIRS: " +mirs.getId() +" are either not yet released or already issued with an MCT number.", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }
    }

    private void loadAllUnchargedItems(){
        collectionOfUnchargeReleasedItem = new HashMap<>();
        try {
            List<UnchargedMIRSReleases> mirsItemReleasesList = MirsDAO.getUnchargedMIRSReleases();

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
