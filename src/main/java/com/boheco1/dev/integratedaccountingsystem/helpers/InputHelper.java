package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.TextField;

public class InputHelper {
    public static void restrictNumbersOnly(TextField tf){
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+")){
                tf.setText(oldValue);
            }
        });
    }
    public static void restrictNumbersOnly(JFXTextField tf){
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+")){
                tf.setText(oldValue);
            }
        });
    }
}
