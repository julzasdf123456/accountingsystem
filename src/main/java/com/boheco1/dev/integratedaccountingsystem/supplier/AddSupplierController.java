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
    private StackPane stackPane;

    @FXML
    private JFXTextField company_tf;

    @FXML
    private TextArea address_tf;

    @FXML
    private JFXComboBox<?> nature_cb;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
