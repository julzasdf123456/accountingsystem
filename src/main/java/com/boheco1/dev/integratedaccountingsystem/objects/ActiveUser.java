package com.boheco1.dev.integratedaccountingsystem.objects;

public class ActiveUser  {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ActiveUser.user = user;
    }
}