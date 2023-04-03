package com.boheco1.dev.integratedaccountingsystem.supplier;

import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.SupplierInfo;
import com.boheco1.dev.integratedaccountingsystem.warehouse.WarehouseDashboardController;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddSupplierController extends MenuControllerHandler implements Initializable {
    @FXML
    private Label status_lbl;
    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField company_tf;

    @FXML
    private TextArea address_tf;
    @FXML
    private JFXComboBox<String> nature_cb;

    @FXML
    private JFXRadioButton vat_rb;

    @FXML
    private JFXRadioButton non_rb;

    @FXML
    private JFXTextField tin_tf;

    @FXML
    private JFXTextField contact_tf;

    @FXML
    private JFXTextField fax_tf;

    @FXML
    private JFXTextField zip_tf;

    @FXML
    private JFXTextField email_tf;

    @FXML
    private JFXTextField phone_tf;

    @FXML
    private JFXTextField mobile_tf;

    @FXML
    private TextArea notes_tf;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private JFXButton save_btn;

    private ToggleGroup tg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.status_lbl.setText("");
        this.tg = new ToggleGroup();
        this.vat_rb.setToggleGroup(this.tg);
        this.non_rb.setToggleGroup(this.tg);
        this.nature_cb.getItems().add("Manufacturing");
        this.nature_cb.getItems().add("Utilities(Electricity, Water, Gas, Etc.)");
        this.nature_cb.getItems().add("Construction");
        this.nature_cb.getItems().add("Transportation & Storage");
        this.nature_cb.getItems().add("Accommodation & Food Services Activities");
        this.nature_cb.getItems().add("Wholesale/Retail Trade");
        this.nature_cb.getItems().add("IT & Communication");
        this.nature_cb.getItems().add("Consultancy/Professional/Management Services");
        this.nature_cb.getItems().add("Education");
        this.nature_cb.getItems().add("Rental");
        this.nature_cb.getItems().add("Other Service Activities");
    }
    /**
     * Add supplier record
     * @return void
     */
    public void add(){
        String companyName = this.company_tf.getText();
        String address = this.address_tf.getText();
        String nature = "";
        if (this.nature_cb.getSelectionModel().getSelectedItem() != null)
            nature = this.nature_cb.getSelectionModel().getSelectedItem();

        String type = "";
        if (this.tg.getSelectedToggle() != null)
            if (this.tg.getSelectedToggle().equals(this.vat_rb))
                type = "VAT";
            else if (this.tg.getSelectedToggle().equals(this.non_rb))
                type = "NON-VAT";
        String tin = this.tin_tf.getText();
        String contact = this.contact_tf.getText();
        String fax = this.fax_tf.getText();
        String zip = this.zip_tf.getText();
        String email = this.email_tf.getText();
        String phone = this.phone_tf.getText();
        String mobile = this.mobile_tf.getText();
        String notes = this.notes_tf.getText();

        if (companyName.isEmpty()) {
            this.status_lbl.setText("Company Name is required!");
            this.company_tf.requestFocus();
        }else if (address.isEmpty() ){
            this.status_lbl.setText("Company Address is required!");
            this.address_tf.requestFocus();
        }else if (nature.isEmpty()) {
            this.status_lbl.setText("Nature of Company is required!");
            this.nature_cb.requestFocus();
        }else if (type.equals("")){
            this.status_lbl.setText("Tax Type is required!");
            this.vat_rb.requestFocus();
        }else{
            this.status_lbl.setText("");
            SupplierInfo supplier = new SupplierInfo("",
                    companyName,
                    address,
                    tin,
                    contact,
                    zip,
                    phone,
                    mobile,
                    email,
                    fax,
                    type,
                    nature,
                    notes,
                    "Active");
            try {
                SupplierDAO.add(supplier);
                this.status_lbl.setText("Supplier Record was successfully saved!");
                this.reset();
            } catch (Exception e) {
                this.status_lbl.setText("Process failed due to: "+e.getMessage());
            }
        }
    }
    /**
     * Resets the fields
     * @return void
     */
    public void reset(){
        this.company_tf.setText("");
        this.address_tf.setText("");
        this.nature_cb.getSelectionModel().clearSelection();
        this.vat_rb.setSelected(false);
        this.non_rb.setSelected(false);
        this.tin_tf.setText("");
        this.contact_tf.setText("");
        this.fax_tf.setText("");
        this.zip_tf.setText("");
        this.email_tf.setText("");
        this.phone_tf.setText("");
        this.mobile_tf.setText("");
        this.notes_tf.setText("");
    }
}
