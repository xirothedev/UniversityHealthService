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
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Modal editor for one consultation. The dialog opens before its lists finish loading. */
public final class ConsultationEditor extends JDialog {
  private static final long serialVersionUID = 1L;

  private static volatile List<Option> cachedPatients;
  private static volatile List<Option> cachedDoctors;

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

  private ConsultationEditor(AppShell shell, Consultation original, Runnable onSaved) {
    super(shell, "Consultation", true);
    this.shell = shell;
    this.original = original;
    this.onSaved = onSaved;

    build();
    patient.addItem(Option.none("Select a patient"));
    medicalStaff.addItem(Option.none("Select a medical staff member"));
    appointment.addItem(Option.none("No appointment"));
    if (original == null) {
      date.setText(Dates.formatDateTime(LocalDateTime.now()));
    } else {
      date.setText(Dates.formatDateTime(original.consultationDate()));
      diagnosis.setText(original.diagnosis());
      symptoms.setText(original.symptoms());
      notes.setText(original.notes());
    }
    setSize(620, 640);
    setLocationRelativeTo(shell);
  }

  /** Fills the lookup cache in the background so the first editor opens without a wait. */
  public static void prefetch(Consumer<Exception> onError) {
    if (cachedPatients == null) {
      Background.run(() -> new LookupDao().patients(), loaded -> cachedPatients = loaded, onError);
    }
    if (cachedDoctors == null) {
      Background.run(() -> new LookupDao().doctors(), loaded -> cachedDoctors = loaded, onError);
    }
  }

  /** Opens the editor at once, then fills its lists when the cached data is ready. */
  public static void open(AppShell shell, Consultation value, Runnable onSaved) {
    ConsultationEditor editor = new ConsultationEditor(shell, value, onSaved);
    editor.loadLookups();
    editor.setVisible(true);
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
    body.add(
        UiComponents.label(original == null ? "New consultation" : "Edit consultation", 18),
        BorderLayout.NORTH);
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

  private void loadLookups() {
    patient.setEnabled(false);
    medicalStaff.setEnabled(false);
    if (cachedPatients != null && cachedDoctors != null) {
      fill(patient, cachedPatients, "Select a patient");
      fill(medicalStaff, cachedDoctors, "Select a medical staff member");
      finishLookups();
      return;
    }
    if (cachedPatients == null) {
      Background.run(
          () -> new LookupDao().patients(),
          loaded -> {
            cachedPatients = loaded;
            fill(patient, loaded, "Select a patient");
            finishIfReady();
          },
          this::lookupFailed);
    } else {
      fill(patient, cachedPatients, "Select a patient");
      finishIfReady();
    }
    if (cachedDoctors == null) {
      Background.run(
          () -> new LookupDao().doctors(),
          loaded -> {
            cachedDoctors = loaded;
            fill(medicalStaff, loaded, "Select a medical staff member");
            finishIfReady();
          },
          this::lookupFailed);
    } else {
      fill(medicalStaff, cachedDoctors, "Select a medical staff member");
      finishIfReady();
    }
  }

  private void finishIfReady() {
    if (cachedPatients != null && cachedDoctors != null) {
      finishLookups();
    }
  }

  private void finishLookups() {
    patient.setEnabled(true);
    medicalStaff.setEnabled(true);
    if (original != null) {
      select(patient, original.patientId());
      select(medicalStaff, original.medStaffId());
      loadAppointments(original.patientId(), original.appointmentId());
    }
    patient.addActionListener(event -> loadAppointments(selectedId(patient), null));
  }

  private void lookupFailed(Exception error) {
    patient.setEnabled(true);
    medicalStaff.setEnabled(true);
    shell.showError(error);
  }

  private static void fill(JComboBox<Option> box, List<Option> options, String placeholder) {
    box.removeAllItems();
    box.addItem(Option.none(placeholder));
    for (Option option : options) {
      box.addItem(option);
    }
  }

  private void loadAppointments(Integer patientId, Integer keepId) {
    if (patientId == null) {
      appointment.removeAllItems();
      appointment.addItem(Option.none("No appointment"));
      return;
    }
    appointment.setEnabled(false);
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
          appointment.setEnabled(true);
        },
        error -> {
          appointment.setEnabled(true);
          shell.showError(error);
        });
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
    int answer =
        javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Delete this consultation?",
            "Confirm delete",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE);
    if (answer != javax.swing.JOptionPane.YES_OPTION) {
      return;
    }
    Background.run(
        () -> new ConsultationDao().delete(original.id()),
        ignored -> {
          onSaved.run();
          dispose();
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
