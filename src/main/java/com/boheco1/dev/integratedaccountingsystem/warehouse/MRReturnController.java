package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

public class MRReturnController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField stock_id_tf, description_tf, rr_no_tf, qty_tf, unit_price_tf, remarks_tf;

    @FXML
    private Label status_lbl;

    @FXML
    private JFXButton returnBtn;

    private MrItem currentMRItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initializes the MR item to return
        this.currentMRItem = (MrItem) Utility.getSelectedObject();
        //Displays MR Item details
        this.setMRItem(currentMRItem);
    }
    /**
     * Returns the MR Item and update the records e.g. remarks, date returned, etc.
     * @return void
     */
    @FXML
    public void returnMRItem()  {
        String remarks = this.remarks_tf.getText();
        status_lbl.setTextFill(Paint.valueOf(ColorPalette.DANGER));
        status_lbl.setTextAlignment(TextAlignment.CENTER);
        String id = this.stock_id_tf.getText();
        if (remarks.length() == 0 || remarks == null) {
            status_lbl.setText("Please enter a valid remarks!");
        } else {
            status_lbl.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR_DARK));
            try {
                //.returnMR(currentMR, status);
                status_lbl.setText("MR item was successfully returned!");
                this.returnBtn.setDisable(true);
                ViewMRController mrs = Utility.getMrController();
                MR mr = MrDAO.get(this.currentMRItem.getMrNo());
                mrs.populateTable(mr);
            } catch (Exception e) {
                status_lbl.setTextFill(Paint.valueOf(ColorPalette.DANGER));
                status_lbl.setText("Process failed due to: "+e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * Sets the selected MR item to return
     * @return void
     */
    public void setMRItem(MrItem item){
        try {
            Stock stock = item.getStock();
            if (item.getStockID() != null) this.stock_id_tf.setText(item.getStockID());
            this.description_tf.setText(stock.getDescription());
            this.qty_tf.setText(item.getQty()+"");
            this.unit_price_tf.setText(stock.getPrice()+"");
            this.rr_no_tf.setText(item.getMrNo());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
