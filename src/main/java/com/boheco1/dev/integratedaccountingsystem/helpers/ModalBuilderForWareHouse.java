package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.warehouse.CriticalStockController;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class ModalBuilderForWareHouse {
    public static void showModalFromXML(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.setOnDialogClosed(e ->
                    Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, "../warehouse_dashboard_controller.fxml")));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
