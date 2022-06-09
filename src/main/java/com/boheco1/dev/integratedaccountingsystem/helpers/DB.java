package com.boheco1.dev.integratedaccountingsystem.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static Connection connection;
    public static String host="localhost";
    public static String db_user = "sa";
    public static String db_pass = "lntrx2022";
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection==null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String conString = "jdbc:sqlserver://"+host+";" +
                    "Database=Accounting;" +
                    "user="+db_user+";" +
                    "password="+db_pass+";" +
                    "encrypt=false;" +
                    "trustServerCertificate=false;" +
                    "loginTimeout=60";
            connection = DriverManager.getConnection(conString);
        }

        return connection;
    }
}