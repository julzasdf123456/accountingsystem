package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class ModalBuilder {
    public static JFXDialog showModalFromXML(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
