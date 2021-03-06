package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.Role;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ManagerDAO {

    public static void addRoleToUser(User user, Role role, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Add_role_to_user (?,?)}");
        cs.setString(1, user.getId());
        cs.setString(2, role.getId());
        cs.executeUpdate();

        //insert user permissions based on the permissions of the newly added role
        List<Permission> permissions = RoleDAO.getPermissions(role, conn);
        for(Permission p: permissions) {
            addPermissionToUser(user, p, conn);
        }
    }

    public static void addPermissionToRole(Role role, Permission permission, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Add_permission_to_role (?,?)}");
        cs.setString(1, role.getId());
        cs.setString(2, permission.getId());
        cs.executeUpdate();
    }

    public static void addPermissionToUser(User user, Permission permission, Connection conn) throws SQLException {
//        CallableStatement cs = conn.prepareCall("{call Add_permission_to_user (?,?)}");
        PreparedStatement cs = conn.prepareStatement("INSERT INTO user_permissions (user_id, permission_id) " +
                "VALUES (?,?)");

        cs.setString(1, user.getId());
        cs.setString(2, permission.getId());
        cs.executeUpdate();

        cs.close();
    }

    public static void removeRolePermission(Role role, Permission permission, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM role_permissions WHERE role_id=? AND permission_id=?");

        cs.setString(1, role.getId());
        cs.setString(2, permission.getId());
        cs.executeUpdate();

        cs.close();
    }

    public static void removeUserPermission(User user, Permission permission, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM user_permissions WHERE user_id=? AND permission_id=?");
        cs.setString(1, user.getId());
        cs.setString(2, permission.getId());
        cs.executeUpdate();
        cs.close();
    }

    public static void removeUserRole(User user, Role role, Connection conn) throws SQLException {
        PreparedStatement cs = conn.prepareStatement("DELETE FROM user_roles WHERE user_id=? AND role_id=?");
        cs.setString(1, user.getId());
        cs.setString(2, role.getId());
        cs.executeUpdate();
        cs.close();
    }
}
