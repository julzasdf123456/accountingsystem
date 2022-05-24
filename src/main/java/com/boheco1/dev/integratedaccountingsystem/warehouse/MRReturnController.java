package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class MRReturnController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField stock_id_tf, description_tf, date_mr_tf, qty_tf, unit_price_tf;

    @FXML
    private Label status_lbl;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private JFXRadioButton return_unserviceable_rb, return_serviceable_rb;

    @FXML
    private JFXButton returnBtn;

    private MR currentMR = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentMR = Utility.getSelectedMR();
        this.setMR(currentMR);
    }

    @FXML
    public void returnMR()  {
        status_lbl.setTextFill(Paint.valueOf(ColorPalette.DANGER));
        status_lbl.setTextAlignment(TextAlignment.CENTER);
        String id = this.stock_id_tf.getText();
        if (id.length() == 0) {
            status_lbl.setText("Please enter a valid Stock ID/code!");
        }else if (this.toggleGroup.getSelectedToggle() == null){
            status_lbl.setText("Please select the MR status!");
        } else {
            String status = "";
            if (this.toggleGroup.getSelectedToggle() == this.return_serviceable_rb){
                status = Utility.MR_RETURNED_SERVICEABLE;
            }else{
                status = Utility.MR_RETURNED_UNSERVICEABLE;
            }
            status_lbl.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR_DARK));
            this.currentMR.setStockId(id);
            try {
                MrDAO.returnMR(currentMR, status);
                status_lbl.setText("MR item was successfully returned!");
                this.returnBtn.setDisable(true);
                ViewMRController mrs = Utility.getMrController();
                mrs.populateTable();
            } catch (Exception e) {
                status_lbl.setTextFill(Paint.valueOf(ColorPalette.DANGER));
                status_lbl.setText("Process failed due to: "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setMR(MR mr){
        if (mr.getStockId() != null) this.stock_id_tf.setText(mr.getStockId());
        this.description_tf.setText(mr.getExtItem());
        this.qty_tf.setText(mr.getQuantity()+"");
        this.unit_price_tf.setText(mr.getPrice()+"");
        this.date_mr_tf.setText(mr.getDateOfMR().toString());
    }
}
