package com.uhs.ui;

import com.uhs.dao.UhsDao;
import java.awt.*;
import javax.swing.*;

/** F3: master = Consultation, detail = PrescriptionLine of the selected consultation. */
public class F3MasterDetail extends JFrame {
  private final JTable tblMaster = new JTable();
  private final JTable tblDetail = new JTable();

  public F3MasterDetail() {
    setTitle("F3 - Consultation -> Prescription lines");
    tblMaster.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JPanel top = new JPanel(new BorderLayout());
    top.add(new JLabel(" Consultations (select a row)"), BorderLayout.NORTH);
    top.add(new JScrollPane(tblMaster), BorderLayout.CENTER);
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.add(new JLabel(" Prescription lines of the selected consultation"), BorderLayout.NORTH);
    bottom.add(new JScrollPane(tblDetail), BorderLayout.CENTER);

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
    split.setDividerLocation(260);
    add(split);

    try {
      tblMaster.setModel(UhsDao.consultations());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage());
    }

    tblMaster
        .getSelectionModel()
        .addListSelectionListener(
            e -> {
              if (e.getValueIsAdjusting()) {
                return;
              }
              int row = tblMaster.getSelectedRow();
              if (row < 0) {
                return;
              }
              int id =
                  ((Number) tblMaster.getValueAt(row, 0)).intValue(); // column 0 = consultation_id
              try {
                tblDetail.setModel(UhsDao.prescriptionLines(id));
              } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
              }
            });

    setSize(1000, 640);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }
}
