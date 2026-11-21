package com.uhs.ui;

import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;

/** Prescription browser: consultation master rows and their prescription lines. */
public final class PrescriptionsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final TableCard masters = new TableCard();
  private final TableCard lines = new TableCard();

  public PrescriptionsPanel(AppShell shell) {
    super("Prescriptions", "Medicines, dose, frequency and duration for each consultation.");
    masters.setRetry(this::load);
    lines.setRetry(this::showLines);

    JSplitPane split =
        UiComponents.split(
            UiComponents.card("Consultations", "Choose a consultation", masters),
            UiComponents.card(
                "Prescription lines", "Line numbers are local to one prescription", lines),
            0.5);
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
    masters.showLoading();
    Background.run(
        UhsDao::consultations,
        model -> {
          masters.setModel(model);
          UiComponents.columns(
              masters.table(),
              new String[] {"ID", "Date", "Patient", "Doctor", "Diagnosis"},
              new int[] {60, 150, 160, 160, 180});
        },
        masters::showError);
  }

  private void showLines() {
    int viewRow = masters.table().getSelectedRow();
    if (viewRow < 0) {
      lines.setModel(UiComponents.model(new String[] {"Information"}, new Object[0][]));
      return;
    }
    int row = masters.table().convertRowIndexToModel(viewRow);
    int id = ((Number) masters.table().getValueAt(row, 0)).intValue();
    lines.showLoading();
    Background.run(
        () -> UhsDao.prescriptionLines(id),
        model -> {
          lines.setModel(model);
          UiComponents.formatPrescriptionLines(lines.table());
        },
        lines::showError);
  }
}
