package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
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
        CallableStatement cs = cxn.prepareCall("{call usrLogin (?,?)}");
        cs.setString(1, userName);
        cs.setString(2, Hash.hash(password));
        ResultSet rs = cs.executeQuery();
        if(!rs.next()) {
            throw new Exception("Invalid user credentials");
        }

        User user = new User(
                rs.getInt("id"),
                rs.getInt("EmployeeID"),
                rs.getString("username"),
                rs.getString("fullname")
        );

        user.setPermissions(getPermissions(user, cxn));

        ActiveUser.setUser(user);

        return user;
    }

    public static User getUserByUserName(String username, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Get_user_by_username (?)}");
        cs.setString(1, username);
        ResultSet rs = cs.executeQuery();
        if(!rs.next()) {
            throw new SQLException("The user name '" + username + "' cannot be found!");
        }

        return new User(
                rs.getInt("id"),
                rs.getInt("EmployeeID"),
                rs.getString("username"),
                rs.getString("password")
        );
    }

    public static User get(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM users WHERE id=?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;

        if(rs.next()) {
            user = new User(
                    rs.getInt("id"),
                    rs.getInt("EmployeeID"),
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
        CallableStatement cs = connection.prepareCall("{call Roles_of_a_user (?)}");
        cs.setInt(1, user.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Role> roles = new ArrayList();

        while(rs.next()) {
            roles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        return roles;
    }

    private static List<Permission> getPermissions(User user, Connection cxn) throws SQLException {
        CallableStatement cs = cxn.prepareCall("{call Permissions_of_a_user (?)}");
        cs.setInt(1, user.getId());
        ResultSet rs = cs.executeQuery();
        List<Permission> permissions = new ArrayList<Permission>();

        while(rs.next()) {
            Permission p = new Permission(rs.getInt("id"), rs.getString("permission"), rs.getString("remarks"));
            enlistPermission(permissions, p);
        }

        for(Role role: getRoles(user, cxn)) {
            for(Permission p: RoleDAO.getPermissions(role, cxn)) {
                enlistPermission(permissions, p);
            }
        }

        return permissions;
    }

    private static void enlistPermission(List<Permission> permissions, Permission p) {
        if(!permissions.contains(p)) {
            permissions.add(p);
        }
    }

    public static List<User> getAll(Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Get_all_users}");
        ResultSet rs = cs.executeQuery();
        ArrayList<User> users = new ArrayList();
        while(rs.next()) {
            users.add(new User(
                    rs.getInt("id"),
                    rs.getInt("EmployeeID"),
                    rs.getString("username"),
                    rs.getString("fullname")
            ));
        }
        return users;
    }

    public static void addUser(User user, Connection conn) throws Exception {
        String passwordHash = Hash.hash(user.getPassword());
        CallableStatement cs = conn.prepareCall("{? = call Add_user (?,?,?,?)}");
        cs.registerOutParameter(1, Types.INTEGER);
        cs.setString(2, user.getUserName());
        cs.setString(3, user.getFullName());
        cs.setString(4, passwordHash);
        cs.setInt(5, user.getEmployeeID());

        cs.executeUpdate();

        int id = cs.getInt(1);

        user.setId(id);
    }

    public static void updatePassword(User user, String newPassword, Connection conn) throws Exception {
        CallableStatement cs = conn.prepareCall("{call Change_password (?,?)}");
        cs.setInt(1, user.getId());
        cs.setString(2, Hash.hash(newPassword));
        cs.executeUpdate();
    }

    public static void deleteUser(User user, Connection conn) throws SQLException  {
        CallableStatement cs = conn.prepareCall("{call Remove_user (?)}");
        cs.setInt(1, user.getId());
        cs.executeUpdate();
    }

    public static void addPermission(User user, Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Add_permission_to_user (?,?)}");
        cs.setInt(1, user.getId());
        cs.setInt(2, permission.getId());
        cs.executeUpdate();
    }

    public static void removePermission(User user, Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Remove_user_permission (?,?)}");
        cs.setInt(1, user.getId());
        cs.setInt(2, permission.getId());
        cs.executeUpdate();
    }
}
