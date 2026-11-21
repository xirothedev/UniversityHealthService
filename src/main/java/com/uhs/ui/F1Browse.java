package com.uhs.ui;

import com.uhs.dao.UhsDao;
import com.uhs.util.DbErrors;
import java.awt.*;
import javax.swing.*;

/** F1: browse any table. Buttons open F2 and F3. */
public class F1Browse extends JFrame {
  private final JComboBox<String> cbTable = new JComboBox<>(UhsDao.TABLES.toArray(new String[0]));
  private final JTable table = new JTable();
  private final JLabel lblCount = new JLabel(" ");

  public F1Browse() {
    setTitle("F1 - Browse tables");
    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(new JLabel("Table:"));
    top.add(cbTable);
    top.add(lblCount);
    add(top, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);

    JButton bF2 = new JButton("Search (F2)");
    JButton bF3 = new JButton("Consultation / Prescription (F3)");
    bF2.addActionListener(e -> new F2Search().setVisible(true));
    bF3.addActionListener(e -> new F3MasterDetail().setVisible(true));
    JPanel bottom = new JPanel();
    bottom.add(bF2);
    bottom.add(bF3);
    add(bottom, BorderLayout.SOUTH);

    cbTable.addActionListener(e -> load());
    load();

    setSize(950, 560);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  private void load() {
    try {
      var m = UhsDao.browse((String) cbTable.getSelectedItem());
      table.setModel(m);
      lblCount.setText(m.getRowCount() + " rows");
    } catch (Exception ex) {
      Tables.error(this, DbErrors.explain(ex));
    }
  }
}
