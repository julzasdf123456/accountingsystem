package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.SupplierInfo;

import java.sql.PreparedStatement;
import java.util.List;

public class GenerateSupplierID {
    public static void main(String[] args) {
        try {
            List<SupplierInfo> suppliers = SupplierDAO.getAll();
            PreparedStatement ps = DB.getConnection().prepareStatement(
                    "UPDATE SupplierInfo SET supplierID = ? WHERE CompanyName=?");
            for(SupplierInfo supplier: suppliers) {
                ps.setString(1, Utility.generateRandomId());
                ps.setString(2, supplier.getCompanyName());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
