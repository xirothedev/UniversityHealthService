package com.uhs.ui;

import com.uhs.dao.ConsultationDao;
import com.uhs.dao.LookupDao;
import com.uhs.model.Consultation;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import com.uhs.util.DbErrors;
import com.uhs.util.ValidationException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Form F4: editing screen. Inserts, updates and deletes Consultation rows. */
public final class ConsultationForm extends JFrame {
  private static final long serialVersionUID = 1L;
  private static final String NO_APPOINTMENT = "(none - walk-in)";

  private final transient ConsultationDao dao = new ConsultationDao();
  private final transient LookupDao lookup = new LookupDao();

  private final DefaultTableModel model =
      Tables.readOnlyModel("ID", "Patient", "Doctor", "Date", "Diagnosis", "Appointment");
  private final JTable table = new JTable(model);
  private final JLabel countLabel = new JLabel(" ");
  private final JLabel statusLabel = new JLabel(" ");

  private final JComboBox<Option> patientBox = new JComboBox<>();
  private final JComboBox<Option> doctorBox = new JComboBox<>();
  private final JComboBox<Option> appointmentBox = new JComboBox<>();
  private final JTextField dateField = new JTextField(16);
  private final JTextField diagnosisField = new JTextField(30);
  private final JTextArea symptomsArea = new JTextArea(3, 30);
  private final JTextArea notesArea = new JTextArea(3, 30);
  private final JLabel idLabel = new JLabel("(new)");

  private final JButton newButton = new JButton("New");
  private final JButton saveButton = new JButton("Save");
  private final JButton deleteButton = new JButton("Delete");
  private final JButton refreshButton = new JButton("Refresh");

  private transient List<Consultation> rows = new ArrayList<>();
  private Integer editingId;
  private boolean updating;

  public ConsultationForm() {
    super("F4 - Edit consultations");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    Tables.style(table);
    table.setAutoCreateRowSorter(false);
    table
        .getSelectionModel()
        .addListSelectionListener(
            e -> {
              if (!e.getValueIsAdjusting()) {
                onRowSelected();
              }
            });
    patientBox.addActionListener(
        e -> {
          if (!updating) {
            loadAppointments(selectedId(patientBox), null, null);
          }
        });
    symptomsArea.setLineWrap(true);
    symptomsArea.setWrapStyleWord(true);
    notesArea.setLineWrap(true);
    notesArea.setWrapStyleWord(true);

    newButton.addActionListener(e -> startNew());
    saveButton.addActionListener(e -> save());
    deleteButton.addActionListener(e -> confirmAndDelete());
    refreshButton.addActionListener(e -> reload(editingId));

    JPanel top = new JPanel(new BorderLayout());
    top.add(new JScrollPane(table), BorderLayout.CENTER);
    JPanel countBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    countBar.add(countLabel);
    top.add(countBar, BorderLayout.SOUTH);

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, buildFormPanel());
    split.setResizeWeight(0.45);
    JPanel south = new JPanel(new BorderLayout());
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttons.add(newButton);
    buttons.add(saveButton);
    buttons.add(deleteButton);
    buttons.add(refreshButton);
    south.add(buttons, BorderLayout.WEST);
    south.add(statusLabel, BorderLayout.CENTER);
    south.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

    getContentPane().add(split, BorderLayout.CENTER);
    getContentPane().add(south, BorderLayout.SOUTH);
    setSize(980, 700);
    setLocationRelativeTo(null);
    loadLookups();
  }

  private JPanel buildFormPanel() {
    JPanel p = new JPanel(new GridBagLayout());
    p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(3, 4, 3, 4);
    c.anchor = GridBagConstraints.WEST;
    int row = 0;
    addRow(p, c, row++, "Consultation ID", idLabel);
    addRow(p, c, row++, "Patient *", patientBox);
    addRow(p, c, row++, "Doctor *", doctorBox);
    addRow(p, c, row++, "Appointment", appointmentBox);
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    datePanel.add(dateField);
    datePanel.add(new JLabel("   format " + Dates.DATE_TIME_HINT));
    addRow(p, c, row++, "Date and time *", datePanel);
    addRow(p, c, row++, "Diagnosis *", diagnosisField);
    addRow(p, c, row++, "Symptoms", new JScrollPane(symptomsArea));
    addRow(p, c, row, "Notes", new JScrollPane(notesArea));
    return p;
  }

  private static void addRow(
      JPanel p, GridBagConstraints c, int row, String label, java.awt.Component field) {
    c.gridx = 0;
    c.gridy = row;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    p.add(new JLabel(label), c);
    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    p.add(field, c);
  }

  // ---- loading --------------------------------------------------------------------------

  private void loadLookups() {
    setBusy(true, "Loading...");
    Background.run(
        () -> new List<?>[] {lookup.patients(), lookup.doctors()},
        lists -> {
          @SuppressWarnings("unchecked")
          List<Option> patients = (List<Option>) lists[0];
          @SuppressWarnings("unchecked")
          List<Option> doctors = (List<Option>) lists[1];
          updating = true;
          patientBox.setModel(new DefaultComboBoxModel<>(patients.toArray(new Option[0])));
          doctorBox.setModel(new DefaultComboBoxModel<>(doctors.toArray(new Option[0])));
          updating = false;
          reload(null);
        },
        e -> {
          setBusy(false, " ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  /** Reloads the grid and, when possible, selects the row with the given id. */
  private void reload(Integer selectId) {
    setBusy(true, "Loading...");
    Background.run(
        dao::findAll,
        list -> {
          rows = list;
          model.setRowCount(0);
          for (Consultation c : list) {
            model.addRow(
                new Object[] {
                  c.id(),
                  c.patientName(),
                  c.doctorName(),
                  Dates.formatDateTime(c.consultationDate()),
                  c.diagnosis(),
                  c.appointmentId() == null ? "" : "#" + c.appointmentId()
                });
          }
          countLabel.setText(list.size() + " row(s)");
          setBusy(false, " ");
          int index = indexOf(selectId);
          if (index >= 0) {
            table.setRowSelectionInterval(index, index);
            table.scrollRectToVisible(table.getCellRect(index, 0, true));
          } else {
            table.clearSelection();
            startNew();
          }
        },
        e -> {
          setBusy(false, " ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  private int indexOf(Integer id) {
    if (id == null) {
      return -1;
    }
    for (int i = 0; i < rows.size(); i++) {
      if (id.equals(rows.get(i).id())) {
        return i;
      }
    }
    return -1;
  }

  private void loadAppointments(Integer patientId, Integer keepId, Integer selectId) {
    if (patientId == null) {
      setAppointmentOptions(List.of(), null);
      return;
    }
    Background.run(
        () -> lookup.freeAppointments(patientId, keepId),
        list -> setAppointmentOptions(list, selectId),
        e -> Tables.error(this, DbErrors.explain(e)));
  }

  private void setAppointmentOptions(List<Option> list, Integer selectId) {
    List<Option> all = new ArrayList<>();
    all.add(Option.none(NO_APPOINTMENT));
    all.addAll(list);
    DefaultComboBoxModel<Option> m = new DefaultComboBoxModel<>(all.toArray(new Option[0]));
    appointmentBox.setModel(m);
    selectById(appointmentBox, selectId);
  }

  // ---- form state -----------------------------------------------------------------------

  private void onRowSelected() {
    int viewRow = table.getSelectedRow();
    if (viewRow < 0 || viewRow >= rows.size()) {
      return;
    }
    Consultation c = rows.get(viewRow);
    editingId = c.id();
    idLabel.setText(String.valueOf(c.id()));
    updating = true;
    selectById(patientBox, c.patientId());
    selectById(doctorBox, c.medStaffId());
    updating = false;
    dateField.setText(Dates.formatDateTime(c.consultationDate()));
    diagnosisField.setText(c.diagnosis() == null ? "" : c.diagnosis());
    symptomsArea.setText(c.symptoms() == null ? "" : c.symptoms());
    notesArea.setText(c.notes() == null ? "" : c.notes());
    deleteButton.setEnabled(true);
    loadAppointments(c.patientId(), c.appointmentId(), c.appointmentId());
    setStatus("Editing consultation #" + c.id());
  }

  private void startNew() {
    editingId = null;
    table.clearSelection();
    idLabel.setText("(new)");
    updating = true;
    if (patientBox.getItemCount() > 0) {
      patientBox.setSelectedIndex(0);
    }
    if (doctorBox.getItemCount() > 0) {
      doctorBox.setSelectedIndex(0);
    }
    updating = false;
    dateField.setText(Dates.formatDateTime(LocalDateTime.now().withSecond(0).withNano(0)));
    diagnosisField.setText("");
    symptomsArea.setText("");
    notesArea.setText("");
    deleteButton.setEnabled(false);
    loadAppointments(selectedId(patientBox), null, null);
    setStatus("Fill in the form and press Save to add a new consultation.");
  }

  private static void selectById(JComboBox<Option> box, Integer id) {
    for (int i = 0; i < box.getItemCount(); i++) {
      Option o = box.getItemAt(i);
      if (id == null ? o.id() == null : id.equals(o.id())) {
        box.setSelectedIndex(i);
        return;
      }
    }
    if (box.getItemCount() > 0) {
      box.setSelectedIndex(0);
    }
  }

  private static Integer selectedId(JComboBox<Option> box) {
    Option o = (Option) box.getSelectedItem();
    return o == null ? null : o.id();
  }

  // ---- actions --------------------------------------------------------------------------

  /** Validates the form and builds the row to save. Nothing reaches the database if it fails. */
  private Consultation readForm() throws ValidationException {
    Integer patientId = selectedId(patientBox);
    if (patientId == null) {
      throw new ValidationException("Choose a patient.");
    }
    Integer doctorId = selectedId(doctorBox);
    if (doctorId == null) {
      throw new ValidationException("Choose a doctor.");
    }
    LocalDateTime when = Dates.parseDateTime(dateField.getText(), "Date and time");
    String diagnosis = diagnosisField.getText().strip();
    if (diagnosis.isEmpty()) {
      throw new ValidationException("Diagnosis is required.");
    }
    if (diagnosis.length() > 255) {
      throw new ValidationException("Diagnosis can have at most 255 characters.");
    }
    String symptoms = blankToNull(symptomsArea.getText());
    String notes = blankToNull(notesArea.getText());
    return new Consultation(
        editingId,
        patientId,
        doctorId,
        selectedId(appointmentBox),
        when,
        symptoms,
        diagnosis,
        notes,
        null,
        null);
  }

  private static String blankToNull(String s) {
    String t = s == null ? "" : s.strip();
    return t.isEmpty() ? null : t;
  }

  private void save() {
    Consultation c;
    try {
      c = readForm();
    } catch (ValidationException e) {
      Tables.warn(this, e.getMessage());
      return;
    }
    boolean isNew = c.id() == null;
    setBusy(true, "Saving...");
    Background.run(
        () -> {
          if (isNew) {
            return dao.insert(c);
          }
          if (!dao.update(c)) {
            throw new ValidationException(
                "This consultation no longer exists. Press Refresh to see the current data.");
          }
          return c.id();
        },
        id -> {
          setStatus(isNew ? "Added consultation #" + id : "Saved consultation #" + id);
          reload(id);
        },
        e -> {
          setBusy(false, " ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  private void confirmAndDelete() {
    Integer id = editingId;
    if (id == null) {
      Tables.warn(this, "Select a consultation in the grid first.");
      return;
    }
    setBusy(true, "Checking...");
    Background.run(
        () -> dao.describeDependents(id),
        dependents -> {
          setBusy(false, " ");
          String message = "Delete consultation #" + id + "? This cannot be undone.";
          if (!dependents.isEmpty()) {
            message += "\nIt still has " + dependents + ", so the database will probably refuse.";
          }
          int answer =
              JOptionPane.showConfirmDialog(
                  this,
                  message,
                  "Confirm delete",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.WARNING_MESSAGE);
          if (answer == JOptionPane.YES_OPTION) {
            delete(id);
          }
        },
        e -> {
          setBusy(false, " ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  private void delete(int id) {
    setBusy(true, "Deleting...");
    Background.run(
        () -> dao.delete(id),
        deleted -> {
          setStatus(deleted ? "Deleted consultation #" + id : "It was already deleted.");
          reload(null);
        },
        e -> {
          setBusy(false, " ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  private void setBusy(boolean busy, String message) {
    saveButton.setEnabled(!busy);
    newButton.setEnabled(!busy);
    refreshButton.setEnabled(!busy);
    deleteButton.setEnabled(!busy && editingId != null);
    if (busy) {
      setStatus(message);
    }
  }

  private void setStatus(String text) {
    statusLabel.setText(text);
  }
}
