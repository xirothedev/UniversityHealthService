package com.uhs;

import java.io.FileInputStream;
import java.util.Properties;

// ponytail: shared skeleton, R6 continues with UI. No hard-coded DB here.
public class App {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Missing db.properties. Copy db.properties.example and edit it.");
            return;
        }
        String url = "jdbc:mysql://" + p.getProperty("db.host") + ":"
                + p.getProperty("db.port") + "/" + p.getProperty("db.name")
                + "?" + p.getProperty("db.params", "");
        System.out.println("DB target: " + url + " user=" + p.getProperty("db.user"));
        System.out.println("Skeleton runs OK. R6 continues with F0-F5 in com.uhs.ui.");
    }
}
