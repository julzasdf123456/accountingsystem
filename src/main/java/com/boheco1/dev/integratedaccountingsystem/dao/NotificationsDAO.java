package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Notifications;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {

    public static String MIRS_APROVAL = "MIRS_APPROVAL"; // TYPE
    public static String MIRS_APPROVAL_ICON = "mdi2n-notebook-check"; // TYPE

    public static String INFORMATION = "INFORMATION"; // TYPE
    public static String INFORMATION_ICON = "mdi2i-information"; // TYPE

    public static String getIconFromType(String type) {
        if (type.equals(MIRS_APROVAL)) {
            return MIRS_APPROVAL_ICON;
        } else if (type.equals(INFORMATION)) {
            return INFORMATION_ICON;
        } else {
            return INFORMATION_ICON;
        }
    }

    /**
     * Creates a new notification
     * @param notifications
     * @throws Exception
     */
    public static void create(Notifications notifications) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Notifications (id, NotificationDetails, NotificationType, FromUser, ToUser, Status, RelationId, Icon, created_at, updated_at) " +
                        "VALUES " +
                        "(?,?,?,?,?,?,?,?,GETDATE(), GETDATE())", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, Utility.generateRandomId());
        ps.setString(2, notifications.getNotificationDetails());
        ps.setString(3, notifications.getNotificationType());
        ps.setString(4, notifications.getFromUser());
        ps.setString(5, notifications.getToUser());
        ps.setString(6, "UNREAD");
        ps.setString(7, notifications.getRelationId());
        ps.setString(8, getIconFromType(notifications.getNotificationType()));

        ps.executeUpdate();
        ps.close();
    }

    /**
     * Updates notification
     * @param notifications
     * @throws Exception
     */
    public static void update(Notifications notifications) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Notifications SET " +
                        "NotificationDetails=?, NotificationType=?, FromUser=?, ToUser=?, Status=?, updated_at=GETDATE() " +
                        "WHERE id=?");
        ps.setString(1, notifications.getNotificationDetails());
        ps.setString(2, notifications.getNotificationType());
        ps.setString(3, notifications.getFromUser());
        ps.setString(4, notifications.getToUser());
        ps.setString(5, notifications.getStatus());
        ps.setString(6, notifications.getId());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Fetch notifications for a specific user, in this case, the user that's been logged in
     * @param userId
     * @return
     * @throws Exception
     */
    public static List<Notifications> getNotificationsForUser(String userId) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Notifications WHERE ToUser=? OR ToUser IS NULL ORDER BY created_at DESC");
        ps.setString(1, userId);

        ResultSet rs = ps.executeQuery();

        ArrayList<Notifications> items = new ArrayList();

        while(rs.next()) {
            items.add(new Notifications(
                    rs.getString("id"),
                    rs.getString("NotificationDetails"),
                    rs.getString("NotificationType"),
                    rs.getString("FromUser"),
                    rs.getString("ToUser"),
                    rs.getString("Status"),
                    rs.getString("created_at"),
                    rs.getString("updated_at"),
                    rs.getString("RelationId"),
                    rs.getString("Icon")
            ));
        }

        rs.close();
        ps.close();

        return items;
    }

    /**
     * Count notifs for user
     * @param userId
     * @return
     * @throws Exception
     */
    public static int getNotifCount(String userId) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT COUNT(id) as TotalCount FROM Notifications WHERE (ToUser=? OR ToUser IS NULL) AND Status='UNREAD'");
        ps.setString(1, userId);

        ResultSet rs = ps.executeQuery();

        String count = "";

        while(rs.next()) {
            count = rs.getString("TotalCount");
        }

        rs.close();
        ps.close();

        return Integer.parseInt(count);
    }
}
