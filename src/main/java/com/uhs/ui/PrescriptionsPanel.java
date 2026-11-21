package com.uhs.ui;

import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.table.DefaultTableModel;

/** Prescription browser: consultation master rows and their prescription lines. */
public final class PrescriptionsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final TableCard masters = new TableCard();
  private final TableCard lines = new TableCard();

  public PrescriptionsPanel(AppShell shell) {
    super("Prescriptions", "Medicines, dose, frequency and duration for each consultation.");
    this.shell = shell;

    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card(
                "Consultations with a prescription", "Choose a consultation", masters),
            UiComponents.card(
                "Prescription lines", "Line numbers are local to one prescription", lines));
    split.setResizeWeight(0.5);
    split.setBorder(null);
    body().add(split, BorderLayout.CENTER);

    javax.swing.JButton refresh = UiComponents.ghostButton("Refresh");
    refresh.addActionListener(event -> load());
    addActions(refresh);

    masters
        .table()
        .getSelectionModel()
        .addListSelectionListener(
            event -> {
              if (!event.getValueIsAdjusting()) {
                showLines();
              }
            });
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
    Background.run(
        () -> {
          DefaultTableModel rows = UhsDao.consultations();
          rows.setColumnIdentifiers(new String[] {"ID", "Date", "Patient", "Doctor", "Diagnosis"});
          return rows;
        },
        masters::setModel,
        shell::showError);
  }

  private void showLines() {
    int viewRow = masters.table().getSelectedRow();
    if (viewRow < 0) {
      lines.setModel(UiComponents.model(new String[] {"Information"}, new Object[0][]));
      return;
    }
    int row = masters.table().convertRowIndexToModel(viewRow);
    int id = ((Number) masters.table().getValueAt(row, 0)).intValue();
    Background.run(() -> UhsDao.prescriptionLines(id), lines::setModel, shell::showError);
  }
}
