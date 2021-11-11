package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.Role;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    public static List<Role> getAll(Connection cxn) throws SQLException {
        CallableStatement cs = cxn.prepareCall("{call Get_all_roles}");
        ResultSet rs = cs.executeQuery();
        ArrayList<Role> roles = new ArrayList<Role>();

        while(rs.next()) {
            roles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        return roles;
    }

    public static List<Permission> getPermissions(Role role, Connection cxn) throws SQLException {
        CallableStatement cs = cxn.prepareCall("{call Permissions_of_a_role (?)}");
        cs.setInt(1, role.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Permission> permissions = new ArrayList();
        while(rs.next()) {
            permissions.add(new Permission(
                    rs.getInt("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        return permissions;
    }

    public static void add(Role role, Connection cxn) throws SQLException {
        CallableStatement cs = cxn.prepareCall("{? = call Add_role (?,?)}");
        cs.registerOutParameter(1, Types.INTEGER);
        cs.setString(2, role.getRole());
        cs.setString(3, role.getDescription());

        cs.executeUpdate();

        int id = cs.getInt(1);

        role.setId(id);
    }

    public static List<Role> rolesOfUser(User user, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Roles_of_a_user (?)}");
        cs.setInt(1, user.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Role> userRoles = new ArrayList();

        while(rs.next()) {
            userRoles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        return userRoles;
    }

    public static List<Role> userAvailableRoles(User user, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call User_available_roles (?)}");
        cs.setInt(1, user.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Role> availableRoles = new ArrayList();
        while(rs.next()) {
            availableRoles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("role"),
                    rs.getString("description")
            ));
        }

        return availableRoles;
    }

    public static void deleteRole(Role role, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Remove_role (?)}");
        cs.setInt(1, role.getId());
        cs.executeUpdate();
    }

    public static List<Permission> availablePermissions(Role role, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Role_available_permissions (?)}");
        cs.setInt(1, role.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Permission> availablePermissions = new ArrayList();
        while(rs.next()) {
            availablePermissions.add(new Permission(
                    rs.getInt("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }
        return availablePermissions;
    }

    public static void updateRole(Role role, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Update_role (?,?,?)}");
        cs.setInt(1, role.getId());
        cs.setString(2, role.getRole());
        cs.setString(3, role.getDescription());
        cs.executeUpdate();
    }
}
