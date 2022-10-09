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
                    rs.getString("BankAccountNumber"),
                    rs.getString("BankDescription"),
                    rs.getString("AccountCode")
            ));
        }

        return bankAccounts;
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
    }
}
