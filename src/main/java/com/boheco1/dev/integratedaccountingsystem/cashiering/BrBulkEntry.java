package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.BankRemittance;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class BrBulkEntry extends MenuControllerHandler implements Initializable {

    @FXML
    Label transactionCodeLabel;
    @FXML
    JFXButton saveEntriesBtn;
    @FXML
    TextArea entryArea;

    List<BankRemittance> remittances;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remittances = new ArrayList<>();
    }

    @FXML
    public void onSaveEntries() {
        String entryStr = entryArea.getText();

        final var scanner = new Scanner(entryStr);
        try{
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] elements = line.split("\t");
                BankRemittance br = new BankRemittance();
                BankAccount ba = BankAccountDAO.getByAcctNo(elements[0]);

                if(ba==null) throw new Exception("Bank account not found for Account #" +elements[0]);

                br.setAccountCode(ba.getAccountCode());
                br.setBankAccount(ba);
                br.setDepositedDate(LocalDate.parse(elements[1], DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                br.setAmount(Double.parseDouble(elements[2].replace(",","")));

                System.out.println(br.getDepositedDate());

                remittances.add(br);
            }
            Utility.getParentController().receive(remittances);
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Update Error",ex.getMessage(),Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

}
