package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.Role;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    public static List<Role> getAll(Connection cxn) throws SQLException {
        PreparedStatement cs = cxn.prepareStatement("SELECT * FROM roles ORDER BY role");
        ResultSet rs = cs.executeQuery();
        ArrayList<Role> roles = new ArrayList<>();

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

    public static List<Permission> getPermissions(Role role, Connection cxn) throws SQLException {
        PreparedStatement cs = cxn.prepareStatement("SELECT * FROM permissions WHERE id IN (SELECT permission_id FROM role_permissions WHERE role_id=?)");
        cs.setString(1, role.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Permission> permissions = new ArrayList();
        while(rs.next()) {
            permissions.add(new Permission(
                    rs.getString("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        return permissions;
    }

    public static void add(Role role, Connection cxn) throws SQLException {
        PreparedStatement cs = cxn.prepareStatement(
                "INSERT INTO roles (role, description, id) VALUES (?,?,?)");

        role.setId(Utility.generateRandomId());

        cs.setString(1, role.getRole());
        cs.setString(2, role.getDescription());
        cs.setString(3, role.getId());

        cs.executeUpdate();

        cs.close();
    }

    public static List<Role> rolesOfUser(User user, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM roles WHERE id IN (SELECT role_id FROM user_roles WHERE user_id=?)");
        cs.setString(1, user.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Role> userRoles = new ArrayList();

        while(rs.next()) {
            userRoles.add(new Role(
                    rs.getString("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        rs.close();
        cs.close();

        return userRoles;
    }

    public static List<Role> userAvailableRoles(User user, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM roles WHERE id NOT IN (SELECT role_id FROM user_roles WHERE user_id=?)");
        cs.setString(1, user.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Role> availableRoles = new ArrayList();
        while(rs.next()) {
            availableRoles.add(new Role(
                    rs.getString("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        rs.close();
        cs.close();

        return availableRoles;
    }

    public static void deleteRole(Role role, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM roles WHERE id=?");
        cs.setString(1, role.getId());
        cs.executeUpdate();

        cs.close();
    }

    public static List<Permission> availablePermissions(Role role, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM permissions WHERE id NOT IN (SELECT permission_id FROM role_permissions WHERE role_id=?)");
        cs.setString(1, role.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Permission> availablePermissions = new ArrayList();
        while(rs.next()) {
            availablePermissions.add(new Permission(
                    rs.getString("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        rs.close();
        cs.close();

        return availablePermissions;
    }

    public static void updateRole(Role role, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("UPDATE roles SET role=?, description=? WHERE id=?");
        cs.setString(3, role.getId());
        cs.setString(1, role.getRole());
        cs.setString(2, role.getDescription());
        cs.executeUpdate();

        cs.close();
    }
}
