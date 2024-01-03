package com.boheco1.dev.integratedaccountingsystem.helpers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class ParseHelper {

    public static int intifyOrZero(String str) {
        int value;
        try {
            value = Integer.parseInt(str);
            return value;
        }catch(NumberFormatException ex) {
            return 0;
        }
    }

    public static double doublifyOrZero(String str) {
        double value;
        try {
            value = Double.parseDouble(str);
            return value;
        }catch(NumberFormatException ex) {
            return 0.0;
        }
    }

}
