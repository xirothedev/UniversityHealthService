package com.uhs.ui;

import com.uhs.dao.UhsDao;
import com.uhs.util.DbErrors;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;

/** F2: patient consultation history + doctor free slots for a date. */
public class F2Search extends JFrame {
  private record Doc(int id, String name) {
    @Override
    public String toString() {
      return id + " - " + name;
    }
  }

  private final JTextField txtPatient = new JTextField(15);
  private final JTable tblHistory = new JTable();
  private final JComboBox<Doc> cbDoctor = new JComboBox<>();
  private final JTextField txtDate = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
  private final JTable tblSlots = new JTable();

  public F2Search() {
    setTitle("F2 - Search");
    JTabbedPane tabs = new JTabbedPane();

    JPanel p1 = new JPanel(new BorderLayout());
    JPanel top1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton b1 = new JButton("Search history");
    top1.add(new JLabel("Patient name (empty = all):"));
    top1.add(txtPatient);
    top1.add(b1);
    p1.add(top1, BorderLayout.NORTH);
    p1.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
    b1.addActionListener(e -> searchHistory());

    JPanel p2 = new JPanel(new BorderLayout());
    JPanel top2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton b2 = new JButton("Find free slots");
    top2.add(new JLabel("Doctor:"));
    top2.add(cbDoctor);
    top2.add(new JLabel("Date (yyyy-MM-dd):"));
    top2.add(txtDate);
    top2.add(b2);
    p2.add(top2, BorderLayout.NORTH);
    p2.add(new JScrollPane(tblSlots), BorderLayout.CENTER);
    b2.addActionListener(e -> searchSlots());

    tabs.add("History", p1);
    tabs.add("Free slots", p2);
    add(tabs);

    loadDoctors();
    setSize(900, 520);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  private void loadDoctors() {
    try {
      var m = UhsDao.doctors();
      for (int i = 0; i < m.getRowCount(); i++) {
        cbDoctor.addItem(
            new Doc(((Number) m.getValueAt(i, 0)).intValue(), String.valueOf(m.getValueAt(i, 1))));
      }
    } catch (Exception ex) {
      Tables.error(this, DbErrors.explain(ex));
    }
  }

  private void searchHistory() {
    try {
      tblHistory.setModel(UhsDao.patientHistory(txtPatient.getText().trim()));
    } catch (Exception ex) {
      Tables.error(this, DbErrors.explain(ex));
    }
  }

  private void searchSlots() {
    try {
      Doc doc = (Doc) cbDoctor.getSelectedItem();
      if (doc == null) {
        return;
      }
      LocalDate date = LocalDate.parse(txtDate.getText().trim());
      if (date.isBefore(LocalDate.now())) {
        JOptionPane.showMessageDialog(this, "Please choose today or a future date.");
        return;
      }
      var m = UhsDao.freeSlots(doc.id(), doc.name(), date);
      tblSlots.setModel(m);
      if (m.getRowCount() == 0) {
        JOptionPane.showMessageDialog(
            this, "No free slots (doctor not working that day, or fully booked).");
      }
    } catch (DateTimeParseException ex) {
      JOptionPane.showMessageDialog(this, "Date must be yyyy-MM-dd, e.g. 2026-11-02");
    } catch (Exception ex) {
      Tables.error(this, DbErrors.explain(ex));
    }
  }
}
