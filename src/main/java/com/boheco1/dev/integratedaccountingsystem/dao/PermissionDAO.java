package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.Role;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO {

    public static List<Permission> getAll(Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Get_all_permissions}");
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

    public static void add(Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{? = call Add_permission (?,?)}");
        cs.registerOutParameter(1, Types.INTEGER);
        cs.setString(2, permission.getPermission());
        cs.setString(3, permission.getRemarks());
        cs.executeUpdate();
        int id = cs.getInt(1);
        permission.setId(id);
    }

    public static List<Permission> permissionsOfUser(User user, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Permissions_of_a_user (?)}");
        cs.setInt(1, user.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Permission> userPermissions = new ArrayList();
        while(rs.next()) {
            userPermissions.add(new Permission(
                    rs.getInt("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        return userPermissions;
    }

    public static List<Permission> userAvailablePermissions(User user, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call User_available_permissions (?)}");
        cs.setInt(1, user.getId());
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

    public static void updatePermission(Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Update_permission  (?,?,?)}");
        cs.setInt(1, permission.getId());
        cs.setString(2, permission.getPermission());
        cs.setString(3, permission.getRemarks());
        cs.executeUpdate();
    }

    public static void removePermission(Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Remove_permission (?)}");
        cs.setInt(1, permission.getId());
        cs.executeUpdate();
    }

    public static List<Permission> permissionsOfRole(Role role, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Permissions_of_a_role (?)}");
        cs.setInt(1, role.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Permission> list = new ArrayList();
        while(rs.next()) {
            list.add(new Permission(
                    rs.getInt("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }
        return list;
    }
}
