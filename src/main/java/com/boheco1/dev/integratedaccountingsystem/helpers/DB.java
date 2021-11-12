package com.boheco1.dev.integratedaccountingsystem.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static Connection connection;

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection==null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String conString = "jdbc:sqlserver://DESKTOP-JKEV4AP;" +
                    "Database=Accounting;" +
                    "user=app_user;" +
                    "password=App_Access2021!;" +
                    "encrypt=false;" +
                    "trustServerCertificate=false;" +
                    "loginTimeout=60";
            connection = DriverManager.getConnection(conString);
        }

        return connection;
    }
}