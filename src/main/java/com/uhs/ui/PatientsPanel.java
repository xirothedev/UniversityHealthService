package com.uhs.ui;

import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

  private final AppShell shell;
  private final JTextField search = new JTextField(22);
  private final TableCard directory = new TableCard();
  private final TableCard allergies = new TableCard();
  private final JLabel identity = UiComponents.label("Select a patient", 16);
  private final JLabel subtype = UiComponents.muted("Subtype and record details appear here.");

  private record PatientData(
      DefaultTableModel people,
      DefaultTableModel allergyLinks,
      DefaultTableModel students,
      DefaultTableModel staff) {}

  private PatientData data;

  public PatientsPanel(AppShell shell) {
    super("Patients", "Registered students and staff, with their allergy records.");
    this.shell = shell;

    JButton searchButton = UiComponents.ghostButton("Search");
    searchButton.addActionListener(event -> applyFilter());
    search.addActionListener(event -> applyFilter());
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    bar.setOpaque(false);
    JLabel searchLabel = UiComponents.muted("Search by name");
    searchLabel.setLabelFor(search);
    bar.add(searchLabel);
    bar.add(search);
    bar.add(searchButton);

    JPanel detail = new JPanel();
    detail.setOpaque(false);
    detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
    detail.add(identity);
    detail.add(Box.createVerticalStrut(6));
    detail.add(subtype);
    detail.add(Box.createVerticalStrut(12));
    detail.add(allergies);

    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card("Directory", "Choose a row to see its detail", directory),
            UiComponents.card("Detail", "Allergy links carry severity and onset date", detail));
    split.setResizeWeight(0.6);
    split.setBorder(null);

    body().add(UiComponents.card("Find a patient", null, bar), BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);
    directory.table().getSelectionModel().addListSelectionListener(this::selected);
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
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
        },
        shell::showError);
  }

  private void applyFilter() {
    if (data == null) {
      return;
    }
    if (search.getText().isBlank()) {
      directory.setModel(data.people());
      return;
    }
    String term =
        "%"
            + search.getText().trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
            + "%";
    Background.run(
        () ->
            UhsDao.query(
                "SELECT * FROM Patient WHERE full_name LIKE ? ESCAPE '\\' ORDER BY full_name",
                term),
        directory::setModel,
        shell::showError);
  }

  private void selected(ListSelectionEvent event) {
    if (event.getValueIsAdjusting() || data == null) {
      return;
    }
    int viewRow = directory.table().getSelectedRow();
    if (viewRow < 0) {
      identity.setText("Select a patient");
      subtype.setText("Subtype and record details appear here.");
      allergies.setModel(UiComponents.model(new String[] {"Information"}, new Object[0][]));
      return;
    }
    int row = directory.table().convertRowIndexToModel(viewRow);
    Object id = data.people().getValueAt(row, data.people().findColumn("patient_id"));
    String name =
        String.valueOf(data.people().getValueAt(row, data.people().findColumn("full_name")));
    identity.setText(name);
    boolean student = UiComponents.filter(data.students(), "patient_id", id).getRowCount() > 0;
    boolean staff = UiComponents.filter(data.staff(), "patient_id", id).getRowCount() > 0;
    subtype.setText(
        "Patient #" + id + "  ·  " + (student ? "Student" : staff ? "Staff" : "Unclassified"));
    allergies.setModel(UiComponents.filter(data.allergyLinks(), "patient_id", id));
  }
}
