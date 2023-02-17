package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.warehouse.ViewMRController;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.apache.commons.text.WordUtils;
import pl.allegro.finance.tradukisto.MoneyConverters;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    public static String DB_BILLING = "BillingBackup";
    public static int ROW_PER_PAGE = 20;
    public static String RELEASING = "releasing";
    public static String REJECTED = "rejected";
    public static String RELEASED = "released";
    public static String PARTIAL_RELEASED = "partial";
    public static String CLOSED = "closed";
    public static String PENDING = "pending";
    public static String APPROVED = "approved";
    public static String UNAVAILABLE = "unavailable";
    public static String MIRS_PATH;
    public static int OR_NUMBER;

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

    private static Teller globalTeller;

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
                amount += check.getOriginalAmount();
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

        ValueConverters converters = ValueConverters.ENGLISH_INTEGER;

        int amountInt = (int)amount;

        int rems = (int)((amount - amountInt)*100);

        StringBuffer words = new StringBuffer();
        words.append(converters.asWords(amountInt));
        words.append(" AND " + rems + "/100 pesos");

        return capitalize(words.toString());
    }

    public static String capitalize(String str) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            stringBuilder.append(Character.toUpperCase(c));
        }

        return stringBuilder.toString();
    }

    public static String formatDecimal(double val){
        if(val%1 == 0)
            return String.format("%,.0f",val);
        return String.format("%,.2f",val);
    }

    public static List<Bill> processor(List<Bill> bills, double cash, List<Check> checks, String teller) throws Exception{

        Queue<Bill> billQueue = new LinkedList<>(bills);

        Queue<Check> checkQueue = new LinkedList<>(checks);

        List<Bill> completed = new ArrayList<>();

        double checkAmount = 0;

        while (!billQueue.isEmpty()) {
            System.out.println("==================================================================================================================");
            Bill b = billQueue.peek();
            System.out.println("Cash Amount: " + cash);
            try {
                PaidBill pd = (PaidBill) b;
                pd.setTeller(teller);
                //If current user is a Teller
                if (ActiveUser.getUser().getEmployeeInfo().getDesignation().equals("Teller")) {
                    pd.setPaymentType("SUB-OFFICE/STATION");
                    //Otherwise
                } else {
                    pd.setPaymentType("SIT-IN");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Bill No: " + b.getBillNo() + " Bill balance: " + Utility.round(b.getBalance(), 2));
            PaidBill p = (PaidBill) b;
            if (p.getChecks() == null)
                p.setChecks(new ArrayList<Check>());
            while (!checkQueue.isEmpty()) {
                Check c = checkQueue.peek();
                System.out.print(" -> Check Number: " + c.getCheckNo() + " Amount: " + c.getAmount());
                System.out.print(" Deducting check amount of " + c.getAmount() + " from " + b.getBalance() + "\n");
                double balance = Utility.round(c.getAmount() - b.getBalance(), 2);
                p.getChecks().add(c);
                //If check amount greater than bill balance
                if (balance > 0) {
                    System.out.println(" -> Bill balance is 0. Set current remaining check amount of " + -balance + ". Removed bill from queue and added to completed. proceed to next check.");
                    c.setAmount(balance);
                    checkAmount = balance;
                    //Set bill check amount to bill total amount
                    p.setCheckAmount(Utility.round(p.getCheckAmount() + b.getBalance(), 2));
                    b.setBalance(0);
                    billQueue.remove();
                    completed.add(b);
                    break;
                //If check amount < bill balance
                } else if (balance < 0) {
                    System.out.println(" -> Bill balance has balance of " + -balance + ". Removed check from queue and proceed to next check.");
                    p.setCheckAmount(Utility.round(p.getCheckAmount() + c.getAmount(), 2));
                    c.setAmount(0);
                    checkAmount = 0;
                    checkQueue.remove();
                    b.setBalance(-balance);
                //If check amount = bill balance
                }else{
                    System.out.println(" -> Bill balance is 0. Removed check from queue and proceed to next bill.");
                    p.setCheckAmount(Utility.round(p.getCheckAmount() + b.getBalance(), 2));
                    b.setBalance(0);
                    c.setAmount(0);
                    checkAmount = 0;
                    billQueue.remove();
                    checkQueue.remove();
                    completed.add(b);
                    break;
                }
            }
            System.out.println("No check in queue. Processing cash amount.");
            System.out.println("Bill balance has balance of " + b.getBalance() + ". Proceed to next.");

            /*If bill has balance and cash is less than balance
            if (b.getBalance() > 0 && cash < b.getBalance()) {
                System.out.println(" -> Deducting cash amount: "+cash+" from "+ b.getBalance() +". Proceed to next.");
                b.setBalance(cash);
                cash = 0;*/
            double current_balance = Utility.round(b.getBalance(), 2);
            //If bill has balance and cash is greater than balance,
            if (current_balance >= 0 && cash >= current_balance) {
                System.out.println("Deducting bill remaining balance " + current_balance + " from cash amount: "+ cash +". Proceed to next.");
                p.setCashAmount(Utility.round(current_balance,2));
                cash = Utility.round(cash - current_balance, 2);
                b.setBalance(0);
                if (!completed.contains(b)) {
                    completed.add(b);
                    billQueue.remove();
                }
            }else{
                throw new Exception("Computation error: Bill Total: "+ Utility.round(p.getTotalAmount(),2) +", Cash Amount: "+p.getCashAmount()+", Check Amount: "+p.getCheckAmount());
            }
        }
        System.out.println("==================================================================================================================");
        System.out.println("After processing the checks and cash, remaining cash amount: "+cash+", check amount: "+checkAmount);
        System.out.println("==================================================================================================================");
        for(Bill cb: completed){
            PaidBill p = (PaidBill) cb;
            System.out.println("BILL: "+p.getBillNo()+" Bill Amount: "+ Utility.round(p.getTotalAmount(), 2) +" = "+p.getCashAmount()+"+"+p.getCheckAmount());
        }
        return completed;
    }

    public static String getAccountCodeProperty() throws Exception {
        File appData = new File("application.properties");
        InputStream is = new FileInputStream("application.properties");
        Properties props = new Properties();
        props.load(is);

        return props.getProperty("account_code");
    }

    public static LocalDate serverDate() throws SQLException, ClassNotFoundException {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                        "SELECT SYSDATETIME() as serverDate");

        if(rs.next()) {
            return rs.getDate("serverDate").toLocalDate();
        }else {
            return LocalDate.now();
        }
    }

    public static Teller getGlobalTeller() {
        return globalTeller;
    }

    public static void setGlobalTeller(Teller globalTeller) {
        Utility.globalTeller = globalTeller;
    }
}