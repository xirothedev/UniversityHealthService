package com.uhs.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

// Khung chung: mở DB từ file ngoài code. Mọi chỗ dùng try-with-resources.
public final class DBConnection {
    private DBConnection() {}

    public static Connection open() throws Exception {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        }
        String url = "jdbc:mysql://" + p.getProperty("db.host") + ":"
                + p.getProperty("db.port") + "/" + p.getProperty("db.name")
                + "?" + p.getProperty("db.params", "");
        return DriverManager.getConnection(url, p.getProperty("db.user"), p.getProperty("db.password"));
    }
}
