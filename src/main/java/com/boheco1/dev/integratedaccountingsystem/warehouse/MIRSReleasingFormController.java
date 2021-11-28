package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MIRSSignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MIRSReleasingFormController implements Initializable {
    @FXML
    private StackPane stackPane;

    @FXML
    private HBox btnHolder;

    @FXML
    private TextArea details, purpose;

    @FXML
    private Label mirsNumber, date, requisitioner, dm, gm;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, unitCol, particularsCol, remarksCol, editCol, deleteCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;
    @FXML
    private TableView tableView;
    private ObservableList<MIRSItem> observableList = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            MIRS mirs = MirsDAO.getMIRS(Utility.getActiveMIRS().getId());
            List<MIRSItem> mirsItemList = MirsDAO.getItems(mirs);
            List<MIRSSignatory> mirsSignatoryList = MIRSSignatoryDAO.get(mirs);

            mirsNumber.setText(""+mirs.getId());
            date.setText(""+mirs.getDateFiled());
            purpose.setText(mirs.getPurpose());
            details.setText(mirs.getDetails());
            requisitioner.setText(mirs.getRequisitioner().getFullName());
            dm.setText(""+ UserDAO.get(mirsSignatoryList.get(0).getUserID()).getFullName());
            gm.setText(""+ UserDAO.get(mirsSignatoryList.get(1).getUserID()).getFullName());

            observableList = FXCollections.observableArrayList(mirsItemList);

            codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));

            particularsCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(StockDAO.get(cellData.getValue().getStockID()).getStockName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

            unitCol.setCellValueFactory(cellData -> {
                try {
                    return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getUnit());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
            remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));

            editCol.setStyle("-fx-alignment: center;");
            Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> editBtn
                    = //
                    new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                        @Override
                        public TableCell call(final TableColumn<MIRSItem, String> param) {
                            final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                                Button btn = new Button("");
                                FontIcon icon = new FontIcon("mdi2f-file-document-edit");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                    btn.setStyle("-fx-background-color: #2196f3");
                                    btn.setGraphic(icon);
                                    btn.setGraphicTextGap(5);
                                    btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        btn.setOnAction(event -> {
                                            MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                            try {

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            editCol.setCellFactory(editBtn);

            deleteCol.setStyle("-fx-alignment: center;");
            Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> removeBtn
                    = //
                    new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                        @Override
                        public TableCell call(final TableColumn<MIRSItem, String> param) {
                            final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                                Button btn = new Button("");
                                FontIcon icon = new FontIcon("mdi2d-delete");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                    btn.setStyle("-fx-background-color: #f44336");
                                    btn.setGraphic(icon);
                                    btn.setGraphicTextGap(5);
                                    btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        btn.setOnAction(event -> {
                                            MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                            try {
                                                observableList.remove(mirsItem);
                                                tableView.setItems(observableList);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            deleteCol.setCellFactory(removeBtn);



            tableView.setFixedCellSize(35);
            tableView.setPlaceholder(new Label("No rows to display"));
            tableView.getItems().setAll(observableList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acceptBtn(ActionEvent event) {
        try {
            Utility.getActiveMIRS().setStatus("Releasing");
            MirsDAO.update(Utility.getActiveMIRS());
            AlertDialogBuilder.messgeDialog("System Message", "MIRS application approved and ready for releasing.", stackPane, AlertDialogBuilder.INFO_DIALOG);
            btnHolder.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addBtn(ActionEvent event) {

    }
}
