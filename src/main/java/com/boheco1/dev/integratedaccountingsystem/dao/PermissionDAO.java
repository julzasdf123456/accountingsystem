package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.Role;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO {

    public static List<Permission> getAll(Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM permissions ORDER BY permission");
        ResultSet rs = cs.executeQuery();
        ArrayList<Permission> permissions = new ArrayList();
        while(rs.next()) {
            permissions.add(new Permission(
                    rs.getString("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        cs.close();

        return permissions;
    }

    public static void add(Permission permission, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement(
                "INSERT INTO permissions (permission, remarks, id) VALUES (?,?,?)");

        permission.setId(Utility.generateRandomId());

        cs.setString(1, permission.getPermission());
        cs.setString(2, permission.getRemarks());
        cs.setString(3, permission.getId());
        cs.executeUpdate();

        cs.close();
    }

    public static List<Permission> permissionsOfUser(User user, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM permissions WHERE id IN (SELECT permission_id FROM user_permissions WHERE user_id=?)");
        cs.setString(1, user.getId());
        ResultSet rs = cs.executeQuery();

        ArrayList<Permission> userPermissions = new ArrayList();
        while(rs.next()) {
            userPermissions.add(new Permission(
                    rs.getString("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        cs.close();

        return userPermissions;
    }

    public static List<Permission> userAvailablePermissions(User user, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM permissions WHERE id NOT IN (SELECT permission_id from user_permissions WHERE user_id=?)");
        cs.setString(1, user.getId());
        ResultSet rs = cs.executeQuery();
        ArrayList<Permission> availablePermissions = new ArrayList();
        while(rs.next()) {
            availablePermissions.add(new Permission(
                    rs.getString("id"),
                    rs.getString("permission"),
                    rs.getString("remarks")
            ));
        }

        return availablePermissions;
    }

    public static void updatePermission(Permission permission, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("UPDATE permissions SET permission=?, remakrs=? WHERE id=?");
        cs.setString(3, permission.getId());
        cs.setString(1, permission.getPermission());
        cs.setString(2, permission.getRemarks());
        cs.executeUpdate();

        cs.close();
    }

    public static void removePermission(Permission permission, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM permissions WHERE id=?");
        cs.setString(1, permission.getId());
        cs.executeUpdate();

        cs.close();
    }
}
