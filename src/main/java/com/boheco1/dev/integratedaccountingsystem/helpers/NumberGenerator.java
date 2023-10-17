package com.boheco1.dev.integratedaccountingsystem.helpers;

import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;

public class NumberGenerator {
    public static String mrNumber() {
        try {
            int year = Utility.serverDate().getYear();

            ResultSet rs = DB.getConnection().createStatement().executeQuery(
                    "SELECT id FROM MR WHERE id LIKE '" + year + "%' ORDER BY id DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY;");

            if(!rs.next()) {
                return year + "-00001";
            }

            String lastId = rs.getString("id");

            return year + "-" + incrementStringID(lastId);

        }catch(Exception ex) {
            ex.printStackTrace();
            return Utility.CURRENT_YEAR() + "-";
        }
    }

    public static String mirsNumber() {
        try {
            int year = Utility.serverDate().getYear();

            ResultSet rs = DB.getConnection().createStatement().executeQuery(
                    "SELECT id FROM MIRS WHERE id LIKE '%" + year + "%' ORDER BY id DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY;");

            if(!rs.next()) {
                return year + "-00001";
            }

            String lastId = rs.getString("id");

            return year + "-" + incrementStringID(lastId);

        }catch(Exception ex) {
            ex.printStackTrace();
            return Utility.CURRENT_YEAR() + "-";
        }
    }

    public static String mrtNumber() {
        try {
            int year = Utility.serverDate().getYear();

            ResultSet rs = DB.getConnection().createStatement().executeQuery(
                    "SELECT id FROM MRT WHERE id LIKE '%" + year + "%' ORDER BY id DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY;");

            if(!rs.next()) {
                return year + "-0001";
            }

            String lastId = rs.getString("id");

            return year + "-" + incrementStringID(lastId);

        }catch(Exception ex) {
            ex.printStackTrace();
            return Utility.CURRENT_YEAR() + "-";
        }
    }

    public static String mctNumber() {
        try {
            int year = Utility.serverDate().getYear();

            ResultSet rs = DB.getConnection().createStatement().executeQuery(
                    "SELECT mct_no FROM MCT WHERE mct_no LIKE '" + year + "-%' ORDER BY createdAt DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY;");

            if(!rs.next()) {
                return year +  "-00001";
            }

            String lastId = rs.getString("mct_no");

            return year + "-" + incrementStringID(lastId);

        }catch(Exception ex) {
            ex.printStackTrace();
            return Utility.CURRENT_YEAR() + "-";
        }
    }

    public static String cvNumber() {
        try {
            int year = Utility.serverDate().getYear();

            ResultSet rs = DB.getConnection().createStatement().executeQuery(
                    "SELECT TransactionNumber FROM TransactionHeader WHERE TransactionCode = 'CV' AND TransactionNumber LIKE '" + year + "-%' ORDER BY createdAt DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY;");

            if(!rs.next()) {
                return year +  "-00001";
            }

            String lastId = rs.getString("mct_no");

            return year + "-" + incrementStringID(lastId);

        }catch(Exception ex) {
            ex.printStackTrace();
            return Utility.CURRENT_YEAR() + "-";
        }
    }



    private static String incrementStringID(String id) throws NumberFormatException {
        int serial = Integer.parseInt(id.substring(id.lastIndexOf("-")+1));
        serial++;
        return StringUtils.leftPad(String.valueOf(serial), 5, "0");
    }
}
