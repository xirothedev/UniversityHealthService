package com.uhs.ui;

import com.uhs.dao.ConsultationDao;
import com.uhs.dao.LookupDao;
import com.uhs.dao.UhsDao;
import com.uhs.model.Consultation;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import java.awt.BorderLayout;
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

    search.addActionListener(event -> applyFilter());
    JPanel bar = UiComponents.controlBar(UiComponents.field("Patient name", search));

    JSplitPane split =
        UiComponents.split(
            UiComponents.card(
                "Consultation history", "Select a row to see its prescription", history),
            UiComponents.card(
                "Prescription lines", "Line number is local to one prescription", lines),
            0.55);

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
          formatHistory();
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
        new String[] {"ID", "Patient", "Doctor", "Date", "Diagnosis", "Appointment"}, data);
  }

  private void formatHistory() {
    UiComponents.columns(
        history.table(),
        new String[] {"ID", "Patient", "Doctor", "Date", "Diagnosis", "Appointment"},
        new int[] {60, 160, 160, 150, 170, 90});
  }

  private void applyFilter() {
    if (search.getText().isBlank()) {
      history.setModel(toModel(consultations));
      formatHistory();
      return;
    }
    Background.run(
        () -> {
          DefaultTableModel source = UhsDao.patientHistory(search.getText());
          Object[][] rows = new Object[source.getRowCount()][6];
          for (int row = 0; row < source.getRowCount(); row++) {
            rows[row] =
                new Object[] {
                  source.getValueAt(row, 2),
                  source.getValueAt(row, 1),
                  source.getValueAt(row, 4),
                  source.getValueAt(row, 3),
                  source.getValueAt(row, 6),
                  "-"
                };
          }
          return UiComponents.model(
              new String[] {"ID", "Patient", "Doctor", "Date", "Diagnosis", "Appointment"}, rows);
        },
        model -> {
          history.setModel(model);
          formatHistory();
        },
        shell::showError);
  }

  private int idColumn() {
    return history
        .table()
        .convertColumnIndexToModel(history.table().getColumnModel().getColumnIndex("ID"));
  }

  private void showLines() {
    int viewRow = history.table().getSelectedRow();
    if (viewRow < 0) {
      lines.setModel(UiComponents.model(new String[] {"Information"}, new Object[0][]));
      return;
    }
    int row = history.table().convertRowIndexToModel(viewRow);
    int id = ((Number) history.table().getValueAt(row, idColumn())).intValue();
    Background.run(
        () -> UhsDao.prescriptionLines(id),
        model -> {
          lines.setModel(model);
          UiComponents.columns(
              lines.table(),
              new String[] {
                "Line", "Medicine", "Dose", "Frequency", "Days", "Qty", "Price", "Total"
              },
              new int[] {55, 170, 120, 120, 70, 70, 90, 90});
        },
        shell::showError);
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
