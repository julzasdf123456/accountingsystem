package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BankAccountDAO
{
    public static List<BankAccount> getAll() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM BankAccounts ORDER BY BankDescription");
        ArrayList<BankAccount> bankAccounts = new ArrayList();

        while(rs.next()) {
            bankAccounts.add(new BankAccount(
                    rs.getString("BankID"),
                    rs.getString("BankAccountNumber"),
                    rs.getString("BankDescription"),
                    rs.getString("AccountCode")
            ));
        }

        rs.close();

        return bankAccounts;
    }

    public static BankAccount get(String id) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM BankAccounts WHERE BankID='" + id + "'");
        if(rs.next()){
            return new BankAccount(
                    rs.getString("BankID"),
                    rs.getString("BankAccountNumber"),
                    rs.getString("BankDescription"),
                    rs.getString("AccountCode")
            );
        }else{
            return null;
        }
    }

    public static void add(BankAccount bankAccount) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO BankAccounts (BankID, BankAccountNumber, BankDescription, AccountCode) " +
                        "VALUES (?,?,?,?)");
        ps.setString(1, Utility.generateRandomId().substring(0,20));
        ps.setString(2,bankAccount.getBankAccountNumber());
        ps.setString(3, bankAccount.getBankDescription());
        ps.setString(4, bankAccount.getAccountCode());
        ps.executeUpdate();
        ps.close();
    }

    public static void update(BankAccount bankAccount) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE BankAccounts SET BankAccountNumber=?, BankDescription=?, AccountCode=? " +
                        "WHERE BankID=?");
        ps.setString(1, bankAccount.getBankAccountNumber());
        ps.setString(2, bankAccount.getBankDescription());
        ps.setString(3, bankAccount.getAccountCode());
        ps.setString(4, bankAccount.getId());

        ps.executeUpdate();
        ps.close();
    }

    public static void delete(BankAccount bankAccount) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "DELETE FROM BankAccounts WHERE BankID=? ");
        ps.setString(1, bankAccount.getId());
        ps.executeUpdate();
        ps.close();
    }
}
