package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class COBApprovalController extends MenuControllerHandler implements Initializable {

    @FXML
    private TableView cob_items;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
    }

    /**
     * Initializes the DCR transactions table
     * @return void
     */
    public void createTable(){
        TableColumn<String, String> column = new TableColumn<>("Particulars");
        /**column0.setCellValueFactory(new PropertyValueFactory<>("billNo"));*/
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<String, String> column1 = new TableColumn<>("Est. Cost");
        column1.setPrefWidth(100);
        column1.setMaxWidth(100);
        column1.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<String, String> column2 = new TableColumn<>("Unit");
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column3 = new TableColumn<>("Qty");
        column3.setPrefWidth(50);
        column3.setMaxWidth(50);
        column3.setMinWidth(50);
        column3.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column4 = new TableColumn<>("Amount");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<String, String> column5 = new TableColumn<>("1st Qtr");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column6 = new TableColumn<>("2nd Qtr");
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column7 = new TableColumn<>("3rd Qtr");
        column7.setPrefWidth(100);
        column7.setMaxWidth(100);
        column7.setMinWidth(100);
        column7.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column8 = new TableColumn<>("4th Qtr");
        column8.setPrefWidth(100);
        column8.setMaxWidth(100);
        column8.setMinWidth(100);
        column8.setStyle("-fx-alignment: center;");

        this.cob_items.setFixedCellSize(35);
        this.cob_items.setPlaceholder(new Label("No Items added"));

        this.cob_items.getColumns().add(column);
        this.cob_items.getColumns().add(column1);
        this.cob_items.getColumns().add(column2);
        this.cob_items.getColumns().add(column3);
        this.cob_items.getColumns().add(column4);
        this.cob_items.getColumns().add(column5);
        this.cob_items.getColumns().add(column6);
        this.cob_items.getColumns().add(column7);
        this.cob_items.getColumns().add(column8);
    }
}
