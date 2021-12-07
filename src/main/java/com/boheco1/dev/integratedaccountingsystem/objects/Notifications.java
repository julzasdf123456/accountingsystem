package com.boheco1.dev.integratedaccountingsystem.objects;

public class Notifications {
    private String id, NotificationDetails, NotificationType, FromUser, ToUser, Status, created_at, updated_at, RelationId, Icon;

    public Notifications() {
    }

    public Notifications(String notificationDetails, String notificationType, String fromUser, String toUser, String relationId) {
        NotificationDetails = notificationDetails;
        NotificationType = notificationType;
        FromUser = fromUser;
        ToUser = toUser;
        RelationId = relationId;
    }

    public Notifications(String id, String notificationDetails, String notificationType, String fromUser, String toUser, String status, String created_at, String updated_at, String relationId, String icon) {
        this.id = id;
        NotificationDetails = notificationDetails;
        NotificationType = notificationType;
        FromUser = fromUser;
        ToUser = toUser;
        Status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        RelationId = relationId;
        Icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationDetails() {
        return NotificationDetails;
    }

    public void setNotificationDetails(String notificationDetails) {
        NotificationDetails = notificationDetails;
    }

    public String getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(String notificationType) {
        NotificationType = notificationType;
    }

    public String getFromUser() {
        return FromUser;
    }

    public void setFromUser(String fromUser) {
        FromUser = fromUser;
    }

    public String getToUser() {
        return ToUser;
    }

    public void setToUser(String toUser) {
        ToUser = toUser;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getRelationId() {
        return RelationId;
    }

    public void setRelationId(String relationId) {
        RelationId = relationId;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }
}
