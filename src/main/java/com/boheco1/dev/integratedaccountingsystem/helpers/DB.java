package com.boheco1.dev.integratedaccountingsystem.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    //Main connection: Accounting database
    private static Connection connection;

    //Other connection for other databases
    private static Connection other_connection;

    //Same server and user credentials
    public static String host="localhost";
    public static String db_user = "app_user";
    public static String db_pass = "Boheco_2021";
    public static String db_name = "Accounting";

    //Default connection for Accounting DB
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection==null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String conString = "jdbc:sqlserver://"+host+";" +
                    "Database="+db_name+";" +
                    "user="+db_user+";" +
                    "password="+db_pass+";" +
                    "encrypt=false;" +
                    "trustServerCertificate=false;" +
                    "loginTimeout=60";
            connection = DriverManager.getConnection(conString);
        }

        return connection;
    }

    //Connection for switching database and querying selected information such as in Tellering (Searching of consumer info in Billing db)
    public static Connection getConnection(String db) throws SQLException, ClassNotFoundException {
        if(other_connection==null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String conString = "jdbc:sqlserver://"+host+";" +
                    "Database="+db+";" +
                    "user="+db_user+";" +
                    "password="+db_pass+";" +
                    "encrypt=false;" +
                    "trustServerCertificate=false;" +
                    "loginTimeout=60";
            other_connection = DriverManager.getConnection(conString);
        }

        return other_connection;
    }
}