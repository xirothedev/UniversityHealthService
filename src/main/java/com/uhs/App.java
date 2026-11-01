package com.uhs;

import com.uhs.ui.MainFrame;
import com.uhs.util.DBConnection;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point. Reads the connection settings from outside the code, then opens the main menu. */
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
    String info = MainFrame.describeConnection(p);
    System.out.println("DB target: " + info);
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // keep the default look and feel
    }
    SwingUtilities.invokeLater(() -> new MainFrame(info).setVisible(true));
  }
}
