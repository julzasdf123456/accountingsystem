package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.Role;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.boheco1.dev.integratedaccountingsystem.usermgt.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static User login(String userName, String password, Connection cxn) throws Exception {
        PreparedStatement cs = cxn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
        cs.setString(1, userName);
        cs.setString(2, Hash.hash(password));
        ResultSet rs = cs.executeQuery();
        if(!rs.next()) {
            throw new Exception("Invalid user credentials");
        }

        User user = new User(
                rs.getString("id"),
                rs.getString("EmployeeID"),
                rs.getString("username"),
                rs.getString("fullname")
        );

        user.setPermissions(getPermissions(user, cxn));

        ActiveUser.setUser(user);

        rs.close();
        cs.close();

        return user;
    }

    public static User getUserByUserName(String username, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM users WHERE username=?");
        cs.setString(1, username);
        ResultSet rs = cs.executeQuery();
        if(!rs.next()) {
            throw new SQLException("The user name '" + username + "' cannot be found!");
        }

        User user =  new User(
                rs.getString("id"),
                rs.getString("EmployeeID"),
                rs.getString("username"),
                rs.getString("password")
        );

        rs.close();
        cs.close();

        return user;
    }

    public static User get(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM users WHERE id=?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;

        if(rs.next()) {
            user = new User(
                    rs.getString("id"),
                    rs.getString("EmployeeID"),
                    rs.getString("username"),
                    rs.getString("fullname"),
                    rs.getString("password")
            );
        }

        rs.close();
        ps.close();

        return user;
    }

    public static List<Role> getRoles(User user, Connection connection) throws SQLException {
        PreparedStatement cs = connection.prepareStatement("SELECT roles.* FROM roles LEFT JOIN user_roles ON user_roles.role_id=roles.id WHERE user_roles.user_id=?");
        cs.setString(1, user.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Role> roles = new ArrayList();

        while(rs.next()) {
            roles.add(new Role(
                    rs.getString("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        rs.close();
        cs.close();

        return roles;
    }

    private static List<Permission> getPermissions(User user, Connection cxn) throws SQLException {
        PreparedStatement cs = cxn.prepareStatement("SELECT permissions.* FROM permissions LEFT JOIN user_permissions ON user_permissions.permission_id=permissions.id WHERE user_permissions.user_id=?");
        cs.setString(1, user.getId());
        ResultSet rs = cs.executeQuery();
        List<Permission> permissions = new ArrayList<Permission>();

        while(rs.next()) {
            Permission p = new Permission(rs.getString("id"), rs.getString("permission"), rs.getString("remarks"));
            enlistPermission(permissions, p);
        }

        for(Role role: getRoles(user, cxn)) {
            for(Permission p: RoleDAO.getPermissions(role, cxn)) {
                enlistPermission(permissions, p);
            }
        }

        rs.close();
        cs.close();

        return permissions;
    }

    private static void enlistPermission(List<Permission> permissions, Permission p) {
        if(!permissions.contains(p)) {
            permissions.add(p);
        }
    }

    public static List<User> getAll(Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM users ORDER BY fullname");
        ResultSet rs = cs.executeQuery();
        ArrayList<User> users = new ArrayList();
        while(rs.next()) {
            users.add(new User(
                    rs.getString("id"),
                    rs.getString("EmployeeID"),
                    rs.getString("username"),
                    rs.getString("fullname")
            ));
        }
        rs.close();
        cs.close();
        return users;
    }

    public static List<User> search(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM users WHERE fullname LIKE ? ORDER BY fullname");
        ps.setString(1, "%" + key + "%");
        ResultSet rs = ps.executeQuery();
        ArrayList<User> users = new ArrayList();
        while(rs.next()) {
            users.add(new User(
                    rs.getString("id"),
                    rs.getString("EmployeeID"),
                    rs.getString("username"),
                    rs.getString("fullname")
            ));
        }
        rs.close();
        ps.close();
        return users;
    }

    public static void addUser(User user, Connection conn) throws Exception {
        String passwordHash = Hash.hash(user.getPassword());
        PreparedStatement cs = conn.prepareStatement(
                "INSERT INTO users (username, fullname, passsword, EmployeeID, id) " +
                "VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        user.setId(Utility.generateRandomId());

        cs.setString(1, user.getUserName());
        cs.setString(2, user.getFullName());
        cs.setString(3, passwordHash);
        cs.setString(4, user.getEmployeeID());
        cs.setString(5, user.getId());

        cs.executeUpdate();

        cs.close();
    }

    public static void updatePassword(User user, String newPassword, Connection conn) throws Exception {
        PreparedStatement cs = conn.prepareStatement("UPDATE users SET password=? WHERE id=?");
        cs.setString(2, user.getId());
        cs.setString(1, Hash.hash(newPassword));
        cs.executeUpdate();
        cs.close();
    }

    public static void deleteUser(User user, Connection conn) throws SQLException  {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM users WHERE id=?");
        cs.setString(1, user.getId());
        cs.executeUpdate();
        cs.close();
    }

    public static void addPermission(User user, Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("INSERT INTO user_permissions (user_id, permission_id) VALUES (?,?)");
        cs.setString(1, user.getId());
        cs.setString(2, permission.getId());
        cs.executeUpdate();
        cs.close();
    }

    public static void removePermission(User user, Permission permission, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM user_permissions WHERE user_id=? AND permission_id=?");
        cs.setString(1, user.getId());
        cs.setString(2, permission.getId());
        cs.executeUpdate();
        cs.close();
    }
}
