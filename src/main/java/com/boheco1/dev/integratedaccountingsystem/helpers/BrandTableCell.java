package com.boheco1.dev.integratedaccountingsystem.helpers;
import com.boheco1.dev.integratedaccountingsystem.objects.Config;
import com.boheco1.dev.integratedaccountingsystem.objects.ItemizedMirsItem;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;

public class BrandTableCell extends TableCell<ItemizedMirsItem, String> {
    private JFXComboBox<ItemizedMirsItem> comboBox = null;
    private ItemizedMirsItem updatedValue;
    public BrandTableCell() {
    }

    public BrandTableCell(ObservableList<ItemizedMirsItem> items) {
        comboBox = new JFXComboBox<>();
        comboBox.setStyle("-jfx-focus-color: transparent; -jfx-unfocus-color: transparent; -jfx-default-color: transparent;");
        comboBox.setPromptText("Select Item");
        comboBox.getItems().addAll(items);

        comboBox.setOnAction(event -> {
            // Handle the ComboBox selection here
            ItemizedMirsItem selectedValue = comboBox.getValue();

            // Update the model with the selected value
            ItemizedMirsItem currentValue = getTableView().getItems().get(getIndex());

            updatedValue = (ItemizedMirsItem) currentValue;
            updatedValue.setBrand(selectedValue.getBrand());
            updatedValue.setPrice(selectedValue.getPrice());
            updatedValue.setId(selectedValue.getId());

        });

        // Bind the ComboBox width to the cell's width
        comboBox.prefWidthProperty().bind(this.widthProperty());
    }

    @Override
    protected void updateItem(String  item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            comboBox.setValue(updatedValue);
            setGraphic(comboBox);
        }
    }

    public ItemizedMirsItem getValue() {
        return updatedValue;
    }
}

