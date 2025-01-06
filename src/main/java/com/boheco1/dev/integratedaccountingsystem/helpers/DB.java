package com.boheco1.dev.integratedaccountingsystem.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DB {
    //Main connection: Accounting database
    private static Connection connection;

    //Other connection for other databases
    private static Connection other_connection;

    private static Connection connection2;

    //Same server and user credentials
   public static String host="localhost";

    public static String db_user = "app_user";
    public static String db_pass = "Boheco_2021";
    public static String db_name = "Accounting";
    public static String db_billing = "BillingBackup";

    public static String db_accounting2="B1Accounting";

    public static Connection con;
    public static Statement stmt;
    ResultSet rs;
    Object info[][];
    private java.sql.ResultSetMetaData metaData;
    int ctr;
    String data;
    String info1[];

    //Default connection for Accounting DB
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection==null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            String conString = "jdbc:sqlserver://"+host+";" +
//                    "Database="+db_name+";" +
//                    "user="+db_user+";" +
//                    "password="+db_pass+";" +
//                    "encrypt=false;" +
//                    "trustServerCertificate=false;" +
//                    "loginTimeout=60";
            String conString = "jdbc:sqlserver://"+host+";" +
                    "Database="+db_name+";" +
                    "user="+db_user+";" +
                    "password="+db_pass+";" +
                    "encrypt=false;" +
                    "trustServerCertificate=false;";
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



    public static Connection getConnection2() throws SQLException, ClassNotFoundException {


        if(connection2==null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
            String conString = "jdbc:sqlserver://"+host+";" +
                    "Database="+db_accounting2+";" +
                    "user="+db_user+";" +
                    "password="+db_pass+";" +
                    "encrypt=false;" +
                    "trustServerCertificate=false;" +
                    "loginTimeout=60";
            connection2 = DriverManager.getConnection(conString);


        }

        return connection2;
    }

    public static void setConnection(String db)throws SQLException, ClassNotFoundException
    {
        try
        {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://"+host+";" +
                    "databaseName="+db+";user="+db_user+";password="+db_pass+";");

            stmt=con.createStatement();

        }
        catch(Exception e)
        {

            e.printStackTrace();
        }

    }

    public Object[][] getAllRecord(String query) throws SQLException
    {
    //System.out.println(query);
        info=new Object[0][0];
        try
        {
            rs=stmt.executeQuery(query);
            metaData=rs.getMetaData();
            ctr=0;
            while (rs.next()) {
                ctr++;
            }
            info=new Object[ctr][metaData.getColumnCount()];
            ctr=0;
            rs=stmt.executeQuery(query);
            metaData=rs.getMetaData();
            while(rs.next())
            {
                for(int col=0; col<metaData.getColumnCount(); col++)
                {

                    info[ctr][col]=rs.getString(col+1);

                }
                ctr++;
            }


        }catch(Exception e)
        {
            e.printStackTrace();

        }
        return info;
    }

    public String[] getSpecificRow(String query)
    {
        //javax.swing.JOptionPane.showMessageDialog(null, query);
        info1=new String[0];
        try
        {

            rs=stmt.executeQuery(query);
            metaData=rs.getMetaData();
            info1=new String[metaData.getColumnCount()];
            // javax.swing.JOptionPane.showMessageDialog(null, info.length);
            while (rs.next()) {
                for(ctr=0; ctr<metaData.getColumnCount(); ctr++)
                {
                    info1[ctr]=rs.getString(ctr+1);
                    //javax.swing.JOptionPane.showMessageDialog(null, info1[ctr]);

                }
            }




        }catch(Exception e)
        {

            e.printStackTrace();

        }

        return info1;
    }

    public String getSpecificData(String query)
    {
        data=null;
        try
        {
            rs=stmt.executeQuery(query);

            while(rs.next())
            {
                data=rs.getString(1);
            }
        }catch(Exception e)
        {

            e.printStackTrace();

        }
        return data;
    }


}