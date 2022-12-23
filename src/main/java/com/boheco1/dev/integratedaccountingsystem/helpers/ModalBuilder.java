package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class ModalBuilder {
    private static JFXDialog dialog;
    public static JFXDialog showModalFromXML(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showModalFromXMLNoClose(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MODAL_CLOSE(){
        dialog.close();
    }
}
