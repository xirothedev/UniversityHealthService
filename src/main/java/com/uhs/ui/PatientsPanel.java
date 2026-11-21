package com.uhs.ui;

import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/** Patient directory with subtype and allergy detail. */
public final class PatientsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final JTextField search = new JTextField(24);
  private final TableCard directory = new TableCard();
  private final TableCard allergies = new TableCard();
  private final JLabel identity = UiComponents.label("Select a patient", 20);
  private final JLabel subtype = UiComponents.muted("Choose a row above to see the full record.");

  private record PatientData(
      DefaultTableModel people,
      DefaultTableModel allergyLinks,
      DefaultTableModel students,
      DefaultTableModel staff) {}

  private PatientData data;

  public PatientsPanel(AppShell shell) {
    super("Patients", "Registered students and staff, with their allergy records.");
    directory.setRetry(this::load);
    allergies.setRetry(this::load);

    JButton searchButton = UiComponents.ghostButton("Search");
    searchButton.addActionListener(event -> applyFilter());
    search.addActionListener(event -> applyFilter());
    JPanel bar =
        UiComponents.controlBar(
            UiComponents.field("Patient name", search), UiComponents.field("Action", searchButton));

    JPanel detail = new JPanel(new BorderLayout());
    detail.setOpaque(false);
    JPanel heading = new JPanel();
    heading.setOpaque(false);
    heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
    heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
    heading.add(identity);
    heading.add(Box.createVerticalStrut(6));
    heading.add(subtype);
    detail.add(heading, BorderLayout.NORTH);
    detail.add(allergies, BorderLayout.CENTER);

    JSplitPane split =
        UiComponents.split(
            UiComponents.card("Directory", "Choose a row to see its detail", directory),
            UiComponents.card("Patient record", "Allergies carry severity and onset date", detail),
            0.55);

    body().add(UiComponents.card("Find a patient", null, bar), BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);
    directory.table().getSelectionModel().addListSelectionListener(this::selected);
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
    directory.showLoading();
    allergies.showLoading();
    Background.run(
        () ->
            new PatientData(
                UhsDao.browse("Patient"),
                UhsDao.browse("PatientAllergy"),
                UhsDao.browse("Student"),
                UhsDao.browse("Staff")),
        loaded -> {
          data = loaded;
          directory.setModel(loaded.people());
          formatDirectory();
          clearDetail();
        },
        error -> {
          directory.showError(error);
          allergies.showError(error);
        });
  }

  private void formatDirectory() {
    UiComponents.columns(
        directory.table(),
        new String[] {"ID", "Name", "Date of birth", "Gender", "Phone"},
        new int[] {60, 180, 120, 80, 140});
  }

  private void clearDetail() {
    identity.setText("Select a patient");
    subtype.setText("Choose a row above to see the full record.");
    allergies.setModel(UiComponents.model(new String[] {"Information"}, new Object[0][]));
  }

  private void applyFilter() {
    if (data == null) {
      return;
    }
    if (search.getText().isBlank()) {
      directory.setModel(data.people());
      formatDirectory();
      return;
    }
    String term =
        "%"
            + search.getText().trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
            + "%";
    directory.showLoading();
    Background.run(
        () ->
            UhsDao.query(
                "SELECT * FROM Patient WHERE full_name LIKE ? ESCAPE '\\' ORDER BY full_name",
                term),
        model -> {
          directory.setModel(model);
          formatDirectory();
        },
        directory::showError);
  }

  private void selected(ListSelectionEvent event) {
    if (event.getValueIsAdjusting() || data == null) {
      return;
    }
    int viewRow = directory.table().getSelectedRow();
    if (viewRow < 0) {
      clearDetail();
      return;
    }
    int row = directory.table().convertRowIndexToModel(viewRow);
    Object id = directory.table().getValueAt(row, 0);
    String name = String.valueOf(directory.table().getValueAt(row, 1));
    identity.setText(name);
    boolean student = UiComponents.filter(data.students(), "patient_id", id).getRowCount() > 0;
    boolean staff = UiComponents.filter(data.staff(), "patient_id", id).getRowCount() > 0;
    subtype.setText(
        "Patient #" + id + "  ·  " + (student ? "Student" : staff ? "Staff" : "Unclassified"));
    allergies.setModel(UiComponents.filter(data.allergyLinks(), "patient_id", id));
    UiComponents.columns(
        allergies.table(),
        new String[] {"Patient", "Allergy", "Severity", "Onset", "Note"},
        new int[] {70, 90, 100, 120, 200});
  }
}
