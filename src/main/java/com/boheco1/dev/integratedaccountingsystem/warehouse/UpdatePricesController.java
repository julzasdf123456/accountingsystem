package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import com.opencsv.CSVReader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdatePricesController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXCheckBox confirm_cb;

    @FXML
    private JFXTextField file_tf;

    @FXML
    private TextArea status_ta;

    @FXML
    private JFXButton select_btn, update_btn;

    @FXML
    private ProgressBar progressBar;

    private File selectedFile = null;
    private String status = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.update_btn.setDisable(true);
        this.confirm_cb.setOnAction(confirm -> {
            this.update_btn.setDisable(!this.confirm_cb.isSelected());
        });
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.progressBar.setVisible(false);
        this.status_ta.setWrapText(true);
    }


    @FXML
    public void updatePrices(){
        if (selectedFile != null) {
            // Create a background Task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    showProgressBar(true);
                    try {
                        CSVReader reader = new CSVReader(new FileReader(selectedFile.getAbsolutePath()));

                        List<String[]> allRows = reader.readAll();

                        List<Stock> stocks = new ArrayList<>();

                        for(int i = 1; i < allRows.size(); i++){
                            String[] row =  allRows.get(i);
                            String id = row[0];
                            Double new_price = Double.parseDouble(row[row.length - 1]);
                            Stock stock = StockDAO.get(id);
                            if (stock == null)
                                throw new Exception("Stock with the id: "+id+" does not exist.");
                            stock.setOldPrice(stock.getPrice());
                            stock.setPrice(new_price);
                            stocks.add(stock);
                        }
                        StockDAO.batchUpdatePrices(stocks);
                        status_ta.appendText("Process completed successfully. All selected stocks have their prices successfully updated!");
                    } catch (Exception e) {
                        status_ta.appendText("Process failed due to: "+e.getMessage());
                    }
                    return null;
                }
            };

            task.setOnSucceeded(wse -> {
                this.selectedFile = null;
                this.file_tf.setText("");
                this.confirm_cb.setSelected(false);
                this.status = "";
                this.showProgressBar(false);
            });

            task.setOnFailed(workerStateEvent -> {
                this.showProgressBar(false);
                status_ta.appendText(status);
            });

            new Thread(task).start();
        }else{
            AlertDialogBuilder.messgeDialog("System Warning", "Please select a CSV file before proceeding!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    public void selectFile(){

        Stage stage = (Stage) stackPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        selectedFile = fileChooser.showOpenDialog(stage);
        this.file_tf.setText(selectedFile.getAbsolutePath());
    }

    public void showProgressBar(boolean show){
        this.progressBar.setVisible(show);
        this.update_btn.setDisable(!show);
    }

}
