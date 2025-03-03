package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.dao.PeriodDAO;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.warehouse.ViewMRController;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utility {
    public static String MANUAL_ENTRY = "manual_entry";
    public static String SYSTEM_GENERATED= "system_generated";

    public static String CANCELLED = "CANCELLED";

    public static boolean TOGGLE_SEARCH = true;
    public static String NOT_FOUND = "NOT FOUND";
    public static String ADDED = "ADDED";
    public static String OUT_OF_STOCK = "OUT OF STOCK";
    public static String INSUFFICIENT_STOCK = "INSUFFICIENT";
    public static double TAX = 12;
    public static String STATION = "main"; //main or sub
    public static String OFFICE_PREFIX = "OSD";
    public static String DB_BILLING = "BillingBackup";
    public static String db_accounting2="B1Accounting";
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

    public static String VOUCHER_TYPE;
    public static String CV = "cv";
    public static String JV = "jv";
    public static int OR_NUMBER;

    public static String ERROR_MSG="";

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

    public static String FILE_PATH = "";
    private static AnchorPane contentPane;
    private static MIRS activeMIRS;
   // private static HashMap<String, ItemizedMirsItem> itemizedMirsItems = new HashMap<>();


    private static StackPane stackPane;

    private static Object selected;

    private static HashMap dictionary;
    private static FlowPane subToolbar;

    private static ViewMRController mrController;

    private static ObjectTransaction parentController;

    private static ORContent orContent;

    public static APP selectedAPP;

    public static DeptThreshold selectedDeptThreshold;

    public static COB selectedCOB;

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

    /*public static HashMap<String, ItemizedMirsItem> getItemizedMirsItems() {
        return itemizedMirsItems;
    }*/

    public static int currentARNumber = 0;

    public static int currentInvoiceNumber = 0;

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

        int amountInt = (int)Math.floor(amount);

        int rems = (int)Math.round(((amount - amountInt))*100);

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
        return String.format("%,.2f",val);
    }

    public static String formatDecimal(BigDecimal val){
        return formatNumberTwoDecimal(val);
    }

    public static String formatQty(double qty) {
        // Check if the number has a decimal part
        if (qty == (int) qty) {
            // If the number has no decimal, return the whole number as a string
            return String.format("%d", (int) qty);
        } else {
            // If the number has a decimal, format it and return as a string
            return String.format("%.2f", qty);
            // You can adjust the format specifier "%.2f" based on your precision needs
        }
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
                pd.setPaymentType("SUB-OFFICE/STATION");
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

    public static String getTransactionCodeProperty() throws IOException {
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("station").equalsIgnoreCase("main") ? "OR" : "ORSub";
    }

    public static String getOfficeProperty() throws IOException {
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("office");
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
    public static String serverDateTime() throws SQLException, ClassNotFoundException {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT SYSDATETIME() as serverDate");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mmaa");
        if(rs.next()) {
            return dateFormat.format(rs.getTimestamp("serverDate"));
        }
        return "";
    }

    public static LocalDateTime serverDateTimeLD() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT SYSDATETIME() as serverDate");
        if(rs.next()) {
            return rs.getTimestamp("serverDate").toLocalDateTime();
        }
        return null;
    }

    public static ORContent getOrContent() {
        return orContent;
    }

    public static void setOrContent(ORContent orContent) {
        Utility.orContent = orContent;
    }

    public static void setSelectedAPP(APP app) {
        selectedAPP = app;
    }

    public static APP getSelectedAPP() {
        return selectedAPP;
    }

    public static void setSelectedDeptThreshold(DeptThreshold dt) {
        selectedDeptThreshold = dt;
    }

    public static DeptThreshold getSelectedDeptThreshold() { return selectedDeptThreshold; }
    public static void setSelectedCOB(COB cob) {
        selectedCOB = cob;
    }

    public static COB getSelectedCOB() { return selectedCOB; }

    public static String getHostProperty() throws Exception {
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("host");
    }

    public static String breakString(String str, int maxChars, int line) {
        if(str.length()<maxChars) return line==1 ? str : "";
        String maxStr = str.substring(0, maxChars);
        int indexOfLastSpace = maxStr.lastIndexOf(' ');

        return line==1 ? str.substring(0, indexOfLastSpace) : str.substring(indexOfLastSpace+1);
    }

    public static String getARPrinterName() throws Exception{
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("ar_printer");
    }

    public static void setARPrinter(String printerName) throws Exception {
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        props.setProperty("ar_printer", printerName);

        props.store(new FileOutputStream("application.properties"), LocalDate.now().toString());
    }

    public static double getTotalReceipt(String transactionCode, java.sql.Date date) throws Exception {
        String sql = "SELECT SUM(Debit) FROM TransactionDetails " +
                "WHERE Credit=? " +
                "AND TransactionCode=? " +
                "AND TransactionDate=? " +
                "AND AccountCode=?";
        PreparedStatement ps = DB.getConnection().prepareStatement(sql);

        ps.setInt(1, 0);
        ps.setString(2, transactionCode);
        ps.setDate(3, date);
        ps.setString(4, TransactionHeader.getAccountCodeProperty());

        ResultSet rs = ps.executeQuery();

        if(rs.next()) return rs.getDouble(1);

        return 0;
    }

    public static boolean checkPeriodIsLocked(LocalDate localDate, StackPane stackPane) {
        try {
            if(PeriodDAO.isLocked(localDate)) {
                AlertDialogBuilder.messgeDialog("Period Locked", "Sorry the action cannot be performed because the period related to this transaction is already closed.", stackPane, AlertDialogBuilder.DANGER_DIALOG);
                return true;
            }
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Error while checking period",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return true;
        }

        return false;
    }

    public static String formatNumberTwoDecimal(double number) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            if(number<0)
            {
                number = number * -1;
            }

            return formatter.format(number);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatNumberTwoDecimal(BigDecimal number) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            return formatter.format(number);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String COB_APPROVAL = "budget officer";
    public static String RV_APPROVAL = "general manager";
    public static String RV_RECOMMENDATION = "manager";
    public static String RV_CERTIFICATION = "budget officer";
    public static String COB_REVIEWER = "manager";
    public static String PO_APPROVAL = "general manager";


}