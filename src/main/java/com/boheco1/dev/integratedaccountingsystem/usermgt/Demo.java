package com.boheco1.dev.integratedaccountingsystem.usermgt;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

//        System.out.println("Enter user name:");
//        String user1 = sc.nextLine();
//
//        System.out.println("Enter password:");
//        String pass1 = sc.nextLine();

        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            String conString = "jdbc:sqlserver://DESKTOP-JKEV4AP;Database=Accounting;IntegratedSecurity=true";
//            Connection connection = DriverManager.getConnection(conString);

            Connection connection = DB.getConnection();

//            EmployeeInfo emp = new EmployeeInfo(1, "Benjie","Basio","Lenteria","Tubigon, Bohol","","Programmer",5);
//            EmployeeDAO.addEmployee(emp, connection);
//
//            User u = new User(-1,1,"lentrix","Benjie B. Lenteria");
//            u.setPassword("password");
//
//            UserDAO.addUser(u, connection);

//            EmployeeInfo emp = new EmployeeInfo(3, "Julio","","Lopez","Tubigon, Bohol","","Programmer",5);
//            EmployeeDAO.addEmployee(emp, connection);

//            User user = UserDAO.login(user1, pass1, connection);
//            String[] perms = {"create-user","update-user","view-own-profile","view-other-profile","delete-user"};
//
//            for(String p: perms) {
//                System.out.println(ActiveUser.getUser().getUserName() + " can " + p + "? " + ActiveUser.getUser().can(p));
//            }

//            System.out.println("Add role Purchaser");
//            Role purchaser = new Role(-1,"Purchaser","The purchasing officer");
//            RoleDAO.add(purchaser, connection);
//
//            System.out.println("Show all roles:");
//            for(Role role: RoleDAO.getAll(connection)) {
//                System.out.println(role.getRole());
//            }
//            List<User> users = UserDAO.getAll(connection);
//            for(User user: users) {
//                UserDAO.updatePassword(user, "password123",connection);
//            }

        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
