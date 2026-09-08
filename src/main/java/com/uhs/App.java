package com.uhs;

import java.io.FileInputStream;
import java.util.Properties;

// ponytail: khung chung, R6 viết tiếp UI. Không hard-code DB ở đây.
public class App {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Chua co db.properties. Copy tu db.properties.example roi sua lai.");
            return;
        }
        String url = "jdbc:mysql://" + p.getProperty("db.host") + ":"
                + p.getProperty("db.port") + "/" + p.getProperty("db.name")
                + "?" + p.getProperty("db.params", "");
        System.out.println("DB target: " + url + " user=" + p.getProperty("db.user"));
        System.out.println("Khung chay OK. R6 viet tiep F0-F5 o com.uhs.ui.");
    }
}
