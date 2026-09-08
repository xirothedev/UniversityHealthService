package com.uhs;

import com.uhs.util.DBConnection;
import java.util.Properties;

// Shared skeleton: no hard-coded DB here. R6 continues with the UI in com.uhs.ui.
public class App {
  public static void main(String[] args) throws Exception {
    Properties p;
    try {
      p = DBConnection.loadSettings();
    } catch (Exception e) {
      System.out.println("Missing DB config. Copy .env.example to .env and edit it.");
      return;
    }
    if (p.getProperty("db.user") == null) {
      System.out.println("Missing db.user/DB_USERNAME. Copy .env.example to .env and edit it.");
      return;
    }
    String url = DBConnection.buildUrl(p);
    System.out.println("DB target: " + url + " user=" + p.getProperty("db.user"));
    System.out.println("Skeleton runs OK. R6 continues with F0-F5 in com.uhs.ui.");
  }
}
