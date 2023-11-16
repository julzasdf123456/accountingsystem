package com.boheco1.dev.integratedaccountingsystem.helpers;
import com.boheco1.dev.integratedaccountingsystem.objects.ItemizedMirsItem;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;

public class CustomTableCell<T, E> extends TableCell<T, E> {
    private JFXComboBox<E> comboBox = null;

    public CustomTableCell() {
    }

    public CustomTableCell(ObservableList<E> items) {
        comboBox = new JFXComboBox<>();
        comboBox.setStyle("-jfx-focus-color: transparent; -jfx-unfocus-color: transparent; -jfx-default-color: transparent;");
        comboBox.setPromptText("Select Item");
        comboBox.getItems().addAll(items);

        comboBox.setOnAction(event -> {
            // Handle the ComboBox selection here
            E selectedValue = comboBox.getValue();

            // Update the model with the selected value
            T updateModel = getTableView().getItems().get(getIndex());

            // Assuming the 'updateModel' is of type 'ItemizedMirsItem', you can cast it
            if (updateModel instanceof ItemizedMirsItem) {
                ItemizedMirsItem itemizedMirsItem = (ItemizedMirsItem) updateModel;
                itemizedMirsItem.setBrand(selectedValue.toString());
            }
        });

        // Bind the ComboBox width to the cell's width
        comboBox.prefWidthProperty().bind(this.widthProperty());
    }

    @Override
    protected void updateItem(E item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            comboBox.setValue(item);
            setGraphic(comboBox);
        }
    }
}

