package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.SupplierInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public static void add(SupplierInfo supplier) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO SupplierInfo " +
                        "(SupplierID, AccountID, CompanyName, CompanyAddress," +
                        "TINNo, ContactPerson, ZipCode, PhoneNo, MobileNo, EmailAddress," +
                        "FaxNo, TaxType, SupplierNature, Notes, Status)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        supplier.setSupplierID(Utility.generateRandomId());

        ps.setString(1, supplier.getSupplierID());
        ps.setString(2, supplier.getAccountID());
        ps.setString(3, supplier.getCompanyName());
        ps.setString(4, supplier.getCompanyAddress());
        ps.setString(5, supplier.getTINNo());
        ps.setString(6, supplier.getContactPerson());
        ps.setString(7, supplier.getZipCode());
        ps.setString(8, supplier.getPhoneNo());
        ps.setString(9, supplier.getMobileNo());
        ps.setString(10, supplier.getEmailAddress());
        ps.setString(11, supplier.getFaxNo());
        ps.setString(12, supplier.getTaxType());
        ps.setString(13, supplier.getSupplierNature());
        ps.setString(14, supplier.getNotes());
        ps.setString(15, supplier.getStatus());

        ps.executeUpdate();

        ps.close();
    }
    /**
     * Update supplier record
     * @param supplier the supplier object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void edit(SupplierInfo supplier) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Update SupplierInfo SET CompanyName = ?, CompanyAddress = ?, " +
                        "TINNo = ?, ContactPerson = ?, ZipCode = ?, PhoneNo = ?, MobileNo = ?, EmailAddress = ?, " +
                        "FaxNo = ?, TaxType = ?, SupplierNature = ?, Notes = ?, Status = ? " +
                        "WHERE SupplierID = ?");

        ps.setString(1, supplier.getCompanyName());
        ps.setString(2, supplier.getCompanyAddress());
        ps.setString(3, supplier.getTINNo());
        ps.setString(4, supplier.getContactPerson());
        ps.setString(5, supplier.getZipCode());
        ps.setString(6, supplier.getPhoneNo());
        ps.setString(7, supplier.getMobileNo());
        ps.setString(8, supplier.getEmailAddress());
        ps.setString(9, supplier.getFaxNo());
        ps.setString(10, supplier.getTaxType());
        ps.setString(11, supplier.getSupplierNature());
        ps.setString(12, supplier.getNotes());
        ps.setString(13, supplier.getStatus());
        ps.setString(14, supplier.getSupplierID());

        ps.executeUpdate();

        ps.close();
    }

    public static SupplierInfo get(String supplierID) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM SupplierInfo WHERE SupplierID=?");
        ps.setString(1, supplierID);
        ResultSet rs = ps.executeQuery();

        SupplierInfo supplierInfo = null;

        if(rs.next()) {
            supplierInfo = new SupplierInfo(
                    rs.getString("SupplierID"),
                    rs.getString("AccountID"),
                    rs.getString("CompanyName"),
                    rs.getString("CompanyAddress"),
                    rs.getString("TINNo"),
                    rs.getString("ContactPerson"),
                    rs.getString("ZipCode"),
                    rs.getString("PhoneNo"),
                    rs.getString("MobileNo"),
                    rs.getString("EmailAddress"),
                    rs.getString("FaxNo"),
                    rs.getString("TaxType"),
                    rs.getString("SupplierNature"),
                    rs.getString("Notes"),
                    rs.getString("Status")
            );
        }

        rs.close();
        ps.close();

        return supplierInfo;
    }

    public static List<SupplierInfo> getAll() throws Exception {
        Statement st = DB.getConnection().createStatement();
        ResultSet rs = st.executeQuery(
                "SELECT * FROM SupplierInfo ORDER BY CompanyName");

        ArrayList<SupplierInfo> supplierInfos = new ArrayList<>();

        while(rs.next()) {
            supplierInfos.add(new SupplierInfo(
                    rs.getString("SupplierID"),
                    rs.getString("AccountID"),
                    rs.getString("CompanyName"),
                    rs.getString("CompanyAddress"),
                    rs.getString("TINNo"),
                    rs.getString("ContactPerson"),
                    rs.getString("ZipCode"),
                    rs.getString("PhoneNo"),
                    rs.getString("MobileNo"),
                    rs.getString("EmailAddress"),
                    rs.getString("FaxNo"),
                    rs.getString("TaxType"),
                    rs.getString("SupplierNature"),
                    rs.getString("Notes"),
                    rs.getString("Status")
            ));
        }
        rs.close();
        st.close();

        return supplierInfos;
    }

    public static List<SupplierInfo> search(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM SupplierInfo si " +
                        "WHERE si.CompanyName LIKE ? " +
                        "ORDER BY CompanyName OFFSET 0 ROWS " +
                        "FETCH NEXT 10 ROWS ONLY");

        ps.setString(1, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        ArrayList<SupplierInfo> supplierInfos = new ArrayList<>();

        while(rs.next()) {
            supplierInfos.add(new SupplierInfo(
                    rs.getString("SupplierID"),
                    rs.getString("AccountID"),
                    rs.getString("CompanyName"),
                    rs.getString("CompanyAddress"),
                    rs.getString("TINNo"),
                    rs.getString("ContactPerson"),
                    rs.getString("ZipCode"),
                    rs.getString("PhoneNo"),
                    rs.getString("MobileNo"),
                    rs.getString("EmailAddress"),
                    rs.getString("FaxNo"),
                    rs.getString("TaxType"),
                    rs.getString("SupplierNature"),
                    rs.getString("Notes"),
                    rs.getString("Status")
            ));
        }
        rs.close();
        ps.close();

        return supplierInfos;
    }
    /**
     * Search supplier record based on key and status
     * @param key the supplier key (name or address)
     * @param status the supplier status
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SupplierInfo> search(String key, String status) throws Exception {

        if (!status.equals("Trashed"))
            status = "";

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM SupplierInfo si " +
                        "WHERE si.CompanyName LIKE ? AND si.Status = ? " +
                        "ORDER BY CompanyName OFFSET 0 ROWS " +
                        "FETCH NEXT 10 ROWS ONLY");

        ps.setString(1, "%" + key + "%");
        ps.setString(2, status);

        ResultSet rs = ps.executeQuery();

        ArrayList<SupplierInfo> supplierInfos = new ArrayList<>();

        while(rs.next()) {
            supplierInfos.add(new SupplierInfo(
                    rs.getString("SupplierID"),
                    rs.getString("AccountID"),
                    rs.getString("CompanyName"),
                    rs.getString("CompanyAddress"),
                    rs.getString("TINNo"),
                    rs.getString("ContactPerson"),
                    rs.getString("ZipCode"),
                    rs.getString("PhoneNo"),
                    rs.getString("MobileNo"),
                    rs.getString("EmailAddress"),
                    rs.getString("FaxNo"),
                    rs.getString("TaxType"),
                    rs.getString("SupplierNature"),
                    rs.getString("Notes"),
                    rs.getString("Status")
            ));
        }
        rs.close();
        ps.close();

        return supplierInfos;
    }

    /**
     * Tags a supplier as trashed
     * @param supplier the supplier to be trashed
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void trash(SupplierInfo supplier) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE SupplierInfo SET Status=? WHERE SupplierId=?");
        ps.setString(1, "Trashed");
        ps.setString(2, supplier.getSupplierID());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Activate a supplier record
     * @param supplier the supplier to recover
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void recover(SupplierInfo supplier) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE SupplierInfo SET Status=? WHERE SupplierId=?");
        ps.setString(1, "");
        ps.setString(2, supplier.getSupplierID());
        ps.executeUpdate();
        ps.close();
    }
}
