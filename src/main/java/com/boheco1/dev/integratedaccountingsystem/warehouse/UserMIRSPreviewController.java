package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserMIRSPreviewController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label  details, date, mirsNumber, applicant, address, requisitioner, signatories, purpose, itemCounter;
    @FXML private JFXButton approvedBtn,rejectBtn;
    @FXML
    private JFXTextField comment;
    @FXML
    private TableView requestedTable;


    private MIRS mirs;
    private List<Releasing> releasedIitems = null;
    private List<ReleasedItemDetails> mirsItemList = null;
    private List<MIRSSignatory> mirsSignatoryList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mirs = Utility.getActiveMIRS();

        try {
            mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            String signatures = "";
            for (MIRSSignatory sig:mirsSignatoryList) {
                signatures+=sig.getStatus().toUpperCase()+" : "+EmployeeDAO.getOne(sig.getUserID(), DB.getConnection()).getFullName();
                signatures+="\n";
            }
            signatories.setText(signatures);

            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            address.setText(mirs.getAddress());
            applicant.setText(mirs.getApplicant());
            requisitioner.setText(mirs.getRequisitioner().getFullName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        populateRequestedMIRSInfo();
    }

    private void populateRequestedMIRSInfo(){
        try {
            mirsItemList = MirsDAO.getReleasedMIRSItems(mirs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<ReleasedItemDetails> observableList = FXCollections.observableArrayList(mirsItemList);

        TableColumn<ReleasedItemDetails, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                String prefix = "";
                MIRSItem temp = MirsDAO.getItems(cellData.getValue().getMirsItemId());
                if(temp.isAdditional())
                    prefix="+";

                return new SimpleStringProperty(prefix+""+Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<ReleasedItemDetails, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setStyle("-fx-alignment: center;");
        quantityCol.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getQuantity())));

        requestedTable.getColumns().add(descriptionCol);
        requestedTable.getColumns().add(quantityCol);
        requestedTable.setPlaceholder(new Label("No item Added"));
        requestedTable.getItems().setAll(observableList);

        itemCounter.setText(observableList.size() + " item(s) found");
    }

    @FXML
    private void approvedMirs(ActionEvent event) {
        action(Utility.APPROVED);
    }

    @FXML
    private void rejectMirs(ActionEvent event) {
        action(Utility.REJECTED);
    }

    private void action(String status){
        if(status.equals(Utility.REJECTED) && comment.getText().isEmpty()){
            AlertDialogBuilder.messgeDialog("System Message", "Please provide reason or comment for rejecting this MIRS.",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }


        JFXButton accept = new JFXButton("Proceed");
        JFXDialog MIRSapprovalDialog = DialogBuilder.showConfirmDialog("MIRS Approval","Confirm action on MIRS application.", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try {

                        MIRSSignatory temp = new MIRSSignatory();
                        temp.setMirsID(mirs.getId());
                        temp.setUserID(ActiveUser.getUser().getId());
                        temp.setStatus(status);
                        temp.setComments(comment.getText());
                        MIRSSignatoryDAO.updateStatus(temp);
                        String message = "";
                        if(status.equals(Utility.REJECTED))
                            message = " reason is "+comment.getText();

                        mirs.setDetails(mirs.getDetails()+"\n"+
                                status+" by "+ActiveUser.getUser().getFullName()+ message);
                        MirsDAO.update(mirs);

                        String notif_details = "MIRS ("+mirs.getId()+") was "+status+".";
                        Notifications torequisitioner = new Notifications(notif_details, Utility.NOTIF_INFORMATION, ActiveUser.getUser().getEmployeeID(), mirs.getRequisitionerID(), mirs.getId());
                        NotificationsDAO.create(torequisitioner);
                        AlertDialogBuilder.messgeDialog("System Message", "MIRS application has been "+status,
                                Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);

                        approvedBtn.setDisable(true);
                        rejectBtn.setDisable(true);

                        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, "user_task_approval_mirs.fxml"));
                        ModalBuilderForWareHouse.MODAL_CLOSE();
                }catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("Error on UserTaskApprovalController", e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                MIRSapprovalDialog.close();
            }
        });
    }



}
