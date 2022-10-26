package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.warehouse.ViewMRController;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.apache.commons.text.WordUtils;
import pl.allegro.finance.tradukisto.MoneyConverters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Utility {
    public static boolean TOGGLE_SEARCH = true;
    public static String NOT_FOUND = "NOT FOUND";
    public static String ADDED = "ADDED";
    public static String OUT_OF_STOCK = "OUT OF STOCK";
    public static String INSUFFICIENT_STOCK = "INSUFFICIENT";
    public static double TAX = 12;
    public static String STATION = "main"; //main or sub
    public static String OFFICE_PREFIX = "OSD";
    public static int ROW_PER_PAGE = 20;
    public static String RELEASING = "releasing";
    public static String REJECTED = "rejected";
    public static String RELEASED = "released";
    public static String PARTIAL_RELEASED = "partial";
    public static String CLOSED = "closed";
    public static String PENDING = "pending";
    public static String APPROVED = "approved";
    public static String UNAVAILABLE = "unavailable";

    public static String MR_FILED = "filed";
    public static String MR_RECOMMENDING = "recommended";
    public static String MR_APPROVED = "approved";
    public static String MR_RELEASED = "released";
    public static String MR_RETURNED = "returned";
    public static String MR_ACTIVE = "active";

    public static String NOTIF_MIRS_APROVAL = "MIRS_APPROVAL"; // TYPE
    public static String NOTIF_MIRS_APPROVAL_ICON = "mdi2n-notebook-check"; // TYPE

    public static String NOTIF_INFORMATION = "INFORMATION"; // TYPE
    public static String NOTIF_INFORMATION_ICON = "mdi2i-information"; // TYPE

    public static String NOTIF_READ = "READ";
    public static String NOTIF_UNREAD = "UNREAD";
    private static AnchorPane contentPane;
    private static MIRS activeMIRS;
    private static HashMap<String, ItemizedMirsItem> itemizedMirsItems = new HashMap<>();

    private static StackPane stackPane;

    private static Object selected;

    private static HashMap dictionary;
    private static FlowPane subToolbar;

    private static ViewMRController mrController;

    private static ObjectTransaction parentController;

    public static MIRS getActiveMIRS() {
        return activeMIRS;
    }

    public static void setActiveMIRS(MIRS activeMIRS) {
        Utility.activeMIRS = activeMIRS;
    }

    public static Object getSelectedObject() {
        return selected;
    }

    public static void setSelectedObject(Object obj) {
        Utility.selected = obj;
    }

    public static AnchorPane getContentPane() {
        return contentPane;
    }

    public static void setContentPane(AnchorPane contentPane) {
        Utility.contentPane = contentPane;
    }

    public static HashMap<String, ItemizedMirsItem> getItemizedMirsItems() {
        return itemizedMirsItems;
    }

    public static String generateRandomId() {
        return new Date().getTime() + "-" + generateRandomString(15);
    }

    public static String generateRandomString(int max) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < max) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static StackPane getStackPane() {
        return stackPane;
    }

    public static void setStackPane(StackPane stackPane) {
        Utility.stackPane = stackPane;
    }

    public static FlowPane getSubToolbar() {
        return subToolbar;
    }

    public static void setSubToolbar(FlowPane subToolbar) {
        Utility.subToolbar = subToolbar;
    }

    public static ViewMRController getMrController() {
        return mrController;
    }

    public static void setMrController(ViewMRController mrController) {
        Utility.mrController = mrController;
    }

    public static String CURRENT_YEAR() {
        Calendar cal = Calendar.getInstance();
        return "" + cal.get(Calendar.YEAR);
    }

    public static ObjectTransaction getParentController() {
        return parentController;
    }

    public static void setParentController(ObjectTransaction parentController) {
        Utility.parentController = parentController;
    }

    public static HashMap getDictionary() {
        return dictionary;
    }

    public static void setDictionary(HashMap dictionary) {
        Utility.dictionary = dictionary;
    }

    public static double getTotalAmount(Collection<?> data) {
        double amount = 0;
        for (Object e : data) {
            if (e instanceof Bill) {
                Bill bill = (Bill) e;
                amount += bill.getTotalAmount();
            }else if (e instanceof Check){
                Check check = (Check) e;
                amount += check.getAmount();
            }
        }
        amount = round(amount,2);
        return amount;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String doubleAmountToWords(double amount) {

//        ValueConverters converters = ValueConverters.ENGLISH_INTEGER;

        MoneyConverters converters = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;

        StringBuffer words = new StringBuffer();
        words.append(converters.asWords(BigDecimal.valueOf(amount)));
        words.append(" pesos");

        return WordUtils.capitalizeFully(words.toString(), new char[]{' ','_'});
    }

    public static String formatDecimal(double val){
        if(val%1 == 0)
            return String.format("%,.0f",val);
        return String.format("%,.2f",val);
    }

}