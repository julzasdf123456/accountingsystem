package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.warehouse.ReceivingEntryController;
import com.boheco1.dev.integratedaccountingsystem.warehouse.ViewMRController;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utility {
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


    private static AnchorPane contentPane;
    private static MIRS activeMIRS;
    private static User selectedUser;

    private static EmployeeInfo selectedEmployee;

    private static StackPane stackPane;

    private static Stock selectedStock;

    private static MR selectedMR;

    private static Receiving selectedReceiving;

    private static ViewMRController mrController;

    private static ObjectTransaction parentController;

    public static MIRS getActiveMIRS() {
        return activeMIRS;
    }

    public static void setActiveMIRS(MIRS activeMIRS) {
        Utility.activeMIRS = activeMIRS;
    }

    public static Stock getSelectedStock() {
        return selectedStock;
    }

    public static void setSelectedStock(Stock stock) {
        Utility.selectedStock = stock;
    }

    public static EmployeeInfo getSelectedEmployee() {
        return selectedEmployee;
    }

    public static void setSelectedEmployee(EmployeeInfo employee) {
        Utility.selectedEmployee = employee;
    }

    public static Receiving getSelectedReceiving() {
        return selectedReceiving;
    }

    public static void setSelectedReceiving(Receiving receiving) {
        Utility.selectedReceiving = receiving;
    }

    public static AnchorPane getContentPane() {
        return contentPane;
    }

    public static void setContentPane(AnchorPane contentPane) {
        Utility.contentPane = contentPane;
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

    public static User getSelectedUser() {
        return selectedUser;
    }

    public static void setSelectedUser(User selectedUser) {
        Utility.selectedUser = selectedUser;
    }

    public static StackPane getStackPane() {
        return stackPane;
    }

    public static void setStackPane(StackPane stackPane) {
        Utility.stackPane = stackPane;
    }

    public static MR getSelectedMR() {
        return selectedMR;
    }

    public static void setSelectedMR(MR selectedMR) {
        Utility.selectedMR = selectedMR;
    }

    public static ViewMRController getMrController() { return mrController; }

    public static void setMrController(ViewMRController mrController) { Utility.mrController = mrController; }

    public static String CURRENT_YEAR() {
        Calendar cal = Calendar.getInstance();
        return ""+cal.get(Calendar.YEAR);
    }

    public static ObjectTransaction getParentController() {
        return parentController;
    }

    public static void setParentController(ObjectTransaction parentController) {
        Utility.parentController = parentController;
    }
}