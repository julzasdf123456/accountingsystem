package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConfigDAO {

    public static Config get(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Config WHERE key=?");
        ps.setString(1, key);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return new Config(key, rs.getString("value"), rs.getString("parse"));
        }
        return null;
    }
}
