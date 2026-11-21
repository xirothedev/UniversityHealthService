package com.uhs;

import com.uhs.ui.AppShell;
import com.uhs.ui.UiTheme;
import com.uhs.util.DBConnection;
import java.util.Properties;
import javax.swing.SwingUtilities;

/** Entry point. Reads the connection settings from outside the code, then opens the shell. */
public class App {
  public static void main(String[] args) {
    Properties settings;
    try {
      settings = DBConnection.loadSettings();
    } catch (Exception error) {
      System.out.println("Missing DB config. Copy .env.example to .env and edit it.");
      return;
    }
    if (settings.getProperty("db.user") == null) {
      System.out.println("Missing db.user/DB_USERNAME. Copy .env.example to .env and edit it.");
      return;
    }
    String info = AppShell.describeConnection(settings);
    System.out.println("DB target: " + info);
    SwingUtilities.invokeLater(
        () -> {
          UiTheme.install();
          new AppShell(info).setVisible(true);
        });
  }
}
