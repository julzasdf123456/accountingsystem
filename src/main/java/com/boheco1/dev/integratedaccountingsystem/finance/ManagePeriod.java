package com.boheco1.dev.integratedaccountingsystem.finance;

import com.boheco1.dev.integratedaccountingsystem.dao.PeriodDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ManagePeriod extends MenuControllerHandler implements Initializable, ObjectTransaction {
    @FXML
    TextField yearField;
    @FXML
    TableView<Period> periodsTable;


    private ObservableList<Period> periods;
    private int year;

    private StackPane stackPane;
    @Override
    public void receive(Object o) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackPane = Utility.getStackPane();
        year = LocalDate.now().getYear();
        yearField.setText(String.valueOf(year));

        refreshTable();
    }

    private void refreshTable() {
        try {
            periods = FXCollections.observableList(PeriodDAO.get(year));

            periodsTable.getColumns().clear();

            TableColumn periodCol = new TableColumn<Period, String>("Period");
            periodCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("formattedPeriod"));

            TableColumn statusCol = new TableColumn<Period, String>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("status"));

            TableColumn lockedByCol = new TableColumn<Period, String>("Locked By");
            lockedByCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("lockedBy"));

            TableColumn dateLockedCol = new TableColumn<Period, String>("Date Locked");
            dateLockedCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("formattedDateLocked"));

            TableColumn unlockedByCol = new TableColumn<Period, String>("Unlocked By");
            unlockedByCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("unlockedBy"));

            TableColumn dateUnlockedCol = new TableColumn<Period, String>("Date Unlocked");
            dateUnlockedCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("formattedDateUnlocked"));

            TableColumn<Period, Period> actionCol = new TableColumn<>("Action");
            actionCol.setMinWidth(40);
            actionCol.setStyle( "-fx-alignment: CENTER;");

            actionCol.setCellValueFactory(stockStringCellDataFeatures -> new ReadOnlyObjectWrapper<Period>(stockStringCellDataFeatures.getValue()));
            actionCol.setCellFactory(periodPeriodTableColumn -> new TableCell<>(){
                FontIcon lockIcon =  new FontIcon("mdi2l-lock");
                FontIcon unlockIcon = new FontIcon("mdi2l-lock-open-variant");
                JFXButton lockBtn = new JFXButton("", lockIcon);
                JFXButton unlockBtn = new JFXButton("", unlockIcon);

                @Override
                protected void updateItem(Period period, boolean b) {
                    super.updateItem(period, b);
                    if(period!=null) {

                        lockBtn.setStyle("-fx-background-color: red");
                        lockIcon.setIconSize(13);
                        lockIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        unlockBtn.setStyle("-fx-background-color: blue");
                        unlockIcon.setIconSize(13);
                        unlockIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        lockBtn.setOnAction(actionEvent -> {
                            lockPeriod(period);
                        });

                        unlockBtn.setOnAction(actionEvent -> {
                            unlockPeriod(period);
                        });

                        if(period.isLocked()) setGraphic(unlockBtn);
                        else setGraphic(lockBtn);

                    }
                }
            });

            periodsTable.getColumns().add(periodCol);
            periodsTable.getColumns().add(statusCol);
            periodsTable.getColumns().add(lockedByCol);
            periodsTable.getColumns().add(dateLockedCol);
            periodsTable.getColumns().add(unlockedByCol);
            periodsTable.getColumns().add(dateUnlockedCol);
            periodsTable.getColumns().add(actionCol);

            periodsTable.getColumns().get(0).setMinWidth(200);
            periodsTable.getColumns().get(1).setMinWidth(200);
            periodsTable.getColumns().get(2).setMinWidth(200);
            periodsTable.getColumns().get(3).setMinWidth(200);
            periodsTable.getColumns().get(4).setMinWidth(200);
            periodsTable.getColumns().get(4).setMinWidth(200);

            periodsTable.setItems(periods);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void lockPeriod(Period p) {
        JFXButton confirm = new JFXButton("Confirm");

        final JFXDialog confirmDialog = DialogBuilder.showConfirmDialog("Lock Period?","You are about to lock this period. Please confirm your action.", confirm, stackPane, DialogBuilder.DANGER_DIALOG);;
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    PeriodDAO.setLocked(p, true);
                    refreshTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirmDialog.close();
            }
        });
    }

    private void unlockPeriod(Period period) {
        if(!ActiveUser.getUser().can("unlock-period")) {
            AlertDialogBuilder.messgeDialog("Unauthorized","Sorry, you are not authorized to unlock a period.", stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        JFXButton confirm = new JFXButton("Confirm");

        final JFXDialog confirmDialog = DialogBuilder.showConfirmDialog("Unlock Period?","You are about to unlock this period. Please confirm your action.", confirm, stackPane, DialogBuilder.DANGER_DIALOG);;
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    PeriodDAO.setLocked(period, false);
                    refreshTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirmDialog.close();
            }
        });
    }

    public void onGenerateBtn() {
        try {
            year = Integer.parseInt(yearField.getText());
            PeriodDAO.generate(year);
            refreshTable();
        }catch(NumberFormatException ex) {
            AlertDialogBuilder.messgeDialog("Error!", "Your entry for year is invalid.",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
