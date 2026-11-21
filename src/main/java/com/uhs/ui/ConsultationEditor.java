package com.uhs.ui;

import com.uhs.dao.ConsultationDao;
import com.uhs.dao.LookupDao;
import com.uhs.model.Consultation;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import com.uhs.util.ValidationException;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Modal editor for one consultation, with insert, update and guarded delete. */
public final class ConsultationEditor extends JDialog {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final Runnable onSaved;
  private final Consultation original;
  private final JComboBox<Option> patient = new JComboBox<>();
  private final JComboBox<Option> medicalStaff = new JComboBox<>();
  private final JComboBox<Option> appointment = new JComboBox<>();
  private final JTextField date = new JTextField(20);
  private final JTextField diagnosis = new JTextField(28);
  private final JTextArea symptoms = new JTextArea(3, 28);
  private final JTextArea notes = new JTextArea(3, 28);
  private final JLabel heading;
  private final List<Option> patients;
  private final List<Option> medicalStaffOptions;

  public ConsultationEditor(
      AppShell shell,
      Consultation original,
      List<Option> patients,
      List<Option> medicalStaffOptions,
      Runnable onSaved) {
    super(shell, "Consultation", true);
    this.shell = shell;
    this.original = original;
    this.patients = patients;
    this.medicalStaffOptions = medicalStaffOptions;
    this.onSaved = onSaved;
    this.heading =
        UiComponents.label(original == null ? "New consultation" : "Edit consultation", 18);

    build();
    fill(patients, medicalStaffOptions);
    if (original != null) {
      date.setText(Dates.formatDateTime(original.consultationDate()));
      diagnosis.setText(original.diagnosis());
      symptoms.setText(original.symptoms());
      notes.setText(original.notes());
      select(patient, original.patientId());
      select(medicalStaff, original.medStaffId());
      loadAppointments(original.patientId(), original.appointmentId());
    } else {
      date.setText(Dates.formatDateTime(LocalDateTime.now()));
    }
    patient.addActionListener(event -> loadAppointments(selectedId(patient), null));
    setSize(620, 640);
    setLocationRelativeTo(shell);
  }

  private void build() {
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(7, 8, 7, 8);
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;

    field(form, c, 0, "Patient", patient);
    field(form, c, 1, "Doctor", medicalStaff);
    field(form, c, 2, "Appointment (optional)", appointment);
    field(form, c, 3, "Date and time", date);
    field(form, c, 4, "Diagnosis", diagnosis);
    field(form, c, 5, "Symptoms", new JScrollPane(symptoms));
    field(form, c, 6, "Notes", new JScrollPane(notes));

    JButton save = UiComponents.primaryButton("Save consultation");
    JButton cancel = UiComponents.ghostButton("Cancel");
    JButton delete = UiComponents.ghostButton("Delete");
    save.addActionListener(event -> save());
    cancel.addActionListener(event -> dispose());
    delete.addActionListener(event -> delete());
    delete.setEnabled(original != null);

    JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
    buttons.setOpaque(false);
    buttons.add(delete);
    buttons.add(cancel);
    buttons.add(save);

    JPanel body = new JPanel(new BorderLayout());
    body.setOpaque(false);
    body.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
    body.add(heading, BorderLayout.NORTH);
    body.add(form, BorderLayout.CENTER);
    body.add(buttons, BorderLayout.SOUTH);
    setContentPane(body);
    getRootPane().setDefaultButton(save);
  }

  private void field(
      JPanel form, GridBagConstraints c, int row, String label, java.awt.Component control) {
    c.gridx = 0;
    c.gridy = row;
    c.weightx = 0;
    JLabel caption = UiComponents.muted(label);
    if (control instanceof javax.swing.JComponent component) {
      caption.setLabelFor(component);
    }
    form.add(caption, c);
    c.gridx = 1;
    c.weightx = 1;
    form.add(control, c);
  }

  private void fill(List<Option> patientOptions, List<Option> medicalStaffOptions) {
    patient.addItem(Option.none("Select a patient"));
    medicalStaff.addItem(Option.none("Select a medical staff member"));
    appointment.addItem(Option.none("No appointment"));
    for (Option option : patientOptions) {
      patient.addItem(option);
    }
    for (Option option : medicalStaffOptions) {
      medicalStaff.addItem(option);
    }
  }

  private void loadAppointments(Integer patientId, Integer keepId) {
    if (patientId == null) {
      appointment.removeAllItems();
      appointment.addItem(Option.none("No appointment"));
      return;
    }
    Background.run(
        () -> new LookupDao().freeAppointments(patientId, keepId),
        loaded -> {
          Object current = appointment.getSelectedItem();
          appointment.removeAllItems();
          appointment.addItem(Option.none("No appointment"));
          for (Option option : loaded) {
            appointment.addItem(option);
          }
          if (current != null) {
            appointment.setSelectedItem(current);
          }
        },
        shell::showError);
  }

  private void save() {
    try {
      Integer patientId = selectedId(patient);
      Integer medicalStaffId = selectedId(medicalStaff);
      if (patientId == null || medicalStaffId == null) {
        throw new ValidationException("Choose a patient and a medical staff member.");
      }
      LocalDateTime when = Dates.parseDateTime(date.getText(), "Date and time");
      String code = diagnosis.getText().trim();
      if (code.isEmpty()) {
        throw new ValidationException("Diagnosis is required.");
      }
      if (code.length() > 255) {
        throw new ValidationException("Diagnosis must be at most 255 characters.");
      }
      Consultation value =
          new Consultation(
              original == null ? null : original.id(),
              patientId,
              medicalStaffId,
              selectedId(appointment),
              when,
              symptoms.getText().trim(),
              code,
              notes.getText().trim(),
              null,
              null);
      Background.run(
          () -> {
            ConsultationDao dao = new ConsultationDao();
            if (original == null) {
              dao.insert(value);
            } else {
              dao.update(value);
            }
            return null;
          },
          ignored -> {
            onSaved.run();
            dispose();
            shell.showMessage("Consultation saved.");
          },
          shell::showError);
    } catch (Exception error) {
      shell.showError(error);
    }
  }

  private void delete() {
    if (original == null || original.id() == null) {
      return;
    }
    Background.run(
        () -> new ConsultationDao().describeDependents(original.id()),
        dependents -> {
          String suffix = dependents.isBlank() ? "" : " It has " + dependents + ".";
          int answer =
              JOptionPane.showConfirmDialog(
                  this,
                  "Delete this consultation?" + suffix,
                  "Confirm delete",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.WARNING_MESSAGE);
          if (answer == JOptionPane.YES_OPTION) {
            Background.run(
                () -> new ConsultationDao().delete(original.id()),
                ignored -> {
                  onSaved.run();
                  dispose();
                },
                shell::showError);
          }
        },
        shell::showError);
  }

  private static Integer selectedId(JComboBox<Option> box) {
    Option option = (Option) box.getSelectedItem();
    return option == null ? null : option.id();
  }

  private static void select(JComboBox<Option> box, Integer id) {
    if (id == null) {
      return;
    }
    for (int index = 0; index < box.getItemCount(); index++) {
      Option option = box.getItemAt(index);
      if (id.equals(option.id())) {
        box.setSelectedIndex(index);
        return;
      }
    }
  }
}
