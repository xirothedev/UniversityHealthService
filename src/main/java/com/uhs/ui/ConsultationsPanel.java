package com.uhs.ui;

import com.uhs.dao.ConsultationDao;
import com.uhs.dao.LookupDao;
import com.uhs.dao.UhsDao;
import com.uhs.model.Consultation;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Consultation history with prescription-line detail and full editing controls. */
public final class ConsultationsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final JTextField search = new JTextField(22);
  private final TableCard history = new TableCard();
  private final TableCard lines = new TableCard();
  private List<Consultation> consultations = List.of();

  public ConsultationsPanel(AppShell shell) {
    super("Consultations", "Symptoms, diagnosis and notes for every consultation.");
    this.shell = shell;

    JButton refresh = UiComponents.ghostButton("Refresh");
    JButton edit = UiComponents.ghostButton("Edit");
    JButton delete = UiComponents.ghostButton("Delete");
    JButton create = UiComponents.primaryButton("New consultation");
    refresh.addActionListener(event -> load());
    create.addActionListener(event -> openEditor(null));
    edit.addActionListener(event -> openEditor(selected()));
    delete.addActionListener(event -> removeSelected());
    addActions(refresh, edit, delete, create);

    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    bar.setOpaque(false);
    bar.add(UiComponents.muted("Filter by patient"));
    bar.add(search);
    search.addActionListener(event -> applyFilter());

    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card(
                "Consultation history", "Select a row to see its prescription", history),
            UiComponents.card(
                "Prescription lines", "Weak entity: line number within one prescription", lines));
    split.setResizeWeight(0.55);
    split.setBorder(null);

    body().add(UiComponents.card("Find a consultation", null, bar), BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);
    history
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
        () -> new ConsultationDao().findAll(),
        loaded -> {
          consultations = loaded;
          history.setModel(toModel(loaded));
        },
        shell::showError);
  }

  private static DefaultTableModel toModel(List<Consultation> rows) {
    Object[][] data = new Object[rows.size()][6];
    for (int index = 0; index < rows.size(); index++) {
      Consultation c = rows.get(index);
      data[index] =
          new Object[] {
            c.id(),
            c.patientName(),
            c.doctorName(),
            Dates.formatDateTime(c.consultationDate()),
            c.diagnosis(),
            c.appointmentId() == null ? "-" : c.appointmentId()
          };
    }
    return UiComponents.model(
        new String[] {"consultation_id", "Patient", "Doctor", "Date", "Diagnosis", "Appointment"},
        data);
  }

  private void applyFilter() {
    if (search.getText().isBlank()) {
      history.setModel(toModel(consultations));
      return;
    }
    Background.run(
        () -> UhsDao.patientHistory(search.getText()), history::setModel, shell::showError);
  }

  private int idColumn() {
    return history.table().convertColumnIndexToModel(
        history.table().getColumnModel().getColumnIndex("consultation_id"));
  }

  private void showLines() {
    int viewRow = history.table().getSelectedRow();
    if (viewRow < 0) {
      lines.setModel(UiComponents.model(new String[] {"Information"}, new Object[0][]));
      return;
    }
    int row = history.table().convertRowIndexToModel(viewRow);
    int id = ((Number) history.table().getValueAt(row, idColumn())).intValue();
    Background.run(() -> UhsDao.prescriptionLines(id), lines::setModel, shell::showError);
  }

  private Consultation selected() {
    int viewRow = history.table().getSelectedRow();
    if (viewRow < 0) {
      return null;
    }
    int row = history.table().convertRowIndexToModel(viewRow);
    int id = ((Number) history.table().getValueAt(row, idColumn())).intValue();
    for (Consultation c : consultations) {
      if (c.id() != null && c.id() == id) {
        return c;
      }
    }
    return null;
  }

  private void openEditor(Consultation value) {
    Background.run(
        () -> new EditorOptions(new LookupDao().patients(), new LookupDao().doctors()),
        options ->
            new ConsultationEditor(shell, value, options.patients(), options.doctors(), this::load)
                .setVisible(true),
        shell::showError);
  }

  private void removeSelected() {
    Consultation value = selected();
    if (value == null) {
      shell.showMessage("Choose a consultation first.");
      return;
    }
    Background.run(
        () -> new ConsultationDao().describeDependents(value.id()),
        dependents -> {
          String suffix = dependents.isBlank() ? "" : " It has " + dependents + ".";
          int answer =
              javax.swing.JOptionPane.showConfirmDialog(
                  this,
                  "Delete consultation #" + value.id() + "?" + suffix,
                  "Confirm delete",
                  javax.swing.JOptionPane.YES_NO_OPTION,
                  javax.swing.JOptionPane.WARNING_MESSAGE);
          if (answer == javax.swing.JOptionPane.YES_OPTION) {
            Background.run(
                () -> new ConsultationDao().delete(value.id()),
                ignored -> {
                  load();
                  shell.showMessage("Consultation deleted.");
                },
                shell::showError);
          }
        },
        shell::showError);
  }

  private record EditorOptions(List<Option> patients, List<Option> doctors) {}
}
