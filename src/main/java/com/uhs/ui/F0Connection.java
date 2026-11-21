package com.uhs.ui;

import com.uhs.util.DBConnection;
import com.uhs.util.DbErrors;
import java.awt.*;
import java.sql.Connection;
import java.util.Properties;
import javax.swing.*;

/** F0: shows the connection settings (from .env / db.properties) and tests the connection. */
public class F0Connection extends JFrame {
  public F0Connection() {
    setTitle("F0 - Database connection");
    setLayout(new BorderLayout(8, 8));

    JTextArea info = new JTextArea();
    info.setEditable(false);
    try {
      Properties p = DBConnection.loadSettings();
      info.setText("URL:  " + DBConnection.buildUrl(p) + "\nUser: " + p.getProperty("db.user"));
    } catch (Exception ex) {
      info.setText("Cannot read settings: " + DbErrors.explain(ex));
    }
    info.setMargin(new Insets(10, 10, 10, 10));
    add(info, BorderLayout.CENTER);

    JButton btn = new JButton("Connect");
    btn.addActionListener(
        e -> {
          try (Connection c = DBConnection.open()) {
            JOptionPane.showMessageDialog(this, "Connected to " + c.getCatalog());
            new F1Browse().setVisible(true);
            dispose();
          } catch (Exception ex) {
            Tables.error(this, "Connection failed. " + DbErrors.explain(ex));
          }
        });
    JPanel south = new JPanel();
    south.add(btn);
    add(south, BorderLayout.SOUTH);

    setSize(620, 200);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }
}
