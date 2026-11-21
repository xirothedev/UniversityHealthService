package com.uhs.ui;

import com.uhs.dao.LookupDao;
import com.uhs.dao.UhsDao;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import com.uhs.util.ValidationException;
import java.awt.BorderLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Appointment desk: weekly free slots for one MedicalStaff member and all appointments. */
public final class AppointmentsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final JComboBox<Option> medicalStaffOptions = new JComboBox<>();
  private final JTextField date = new JTextField(Dates.formatDate(LocalDate.now().plusDays(1)), 12);
  private final TableCard slots = new TableCard();
  private final TableCard appointments = new TableCard();
  private Map<Integer, Set<Integer>> templateDays = Map.of();

  public AppointmentsPanel(AppShell shell) {
    super("Appointments", "Find an open consultation slot in the coming week.");
    this.shell = shell;
    slots.setRetry(this::findSlots);
    appointments.setRetry(this::loadAppointments);

    JButton find = UiComponents.primaryButton("Find free slots");
    find.addActionListener(event -> findSlots());
    date.addActionListener(event -> findSlots());
    medicalStaffOptions.addActionListener(event -> staffChanged());

    JPanel controls =
        UiComponents.controlBar(
            UiComponents.field("Staff", medicalStaffOptions),
            UiComponents.field("Date", date),
            UiComponents.field("Action", find));

    JSplitPane split =
        UiComponents.split(
            UiComponents.card("Open slots", "Free units from the weekly template", slots),
            UiComponents.card("Booked appointments", "All appointments on record", appointments),
            0.55);

    body().add(UiComponents.card("Find a slot", null, controls), BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    medicalStaffOptions.setEnabled(false);
    Background.run(
        () -> {
          List<Option> staff = new LookupDao().doctors();
          DefaultTableModel template =
              UhsDao.query("SELECT med_staff_id, weekday FROM ConsultSlot");
          Map<Integer, Set<Integer>> days = new HashMap<>();
          for (int i = 0; i < template.getRowCount(); i++) {
            int id = ((Number) template.getValueAt(i, 0)).intValue();
            int weekday = ((Number) template.getValueAt(i, 1)).intValue();
            days.computeIfAbsent(id, key -> new TreeSet<>()).add(weekday);
          }
          return Map.entry(staff, days);
        },
        loaded -> {
          templateDays = loaded.getValue();
          medicalStaffOptions.removeAllItems();
          for (Option staff : loaded.getKey()) {
            medicalStaffOptions.addItem(staff);
          }
          medicalStaffOptions.setEnabled(true);
          Option selected = (Option) medicalStaffOptions.getSelectedItem();
          if (selected != null && selected.id() != null) {
            date.setText(Dates.formatDate(nextWorkingDate(selected)));
            findSlots();
          }
        },
        error -> {
          medicalStaffOptions.setEnabled(true);
          shell.showError(error);
        });
    loadAppointments();
  }

  private void staffChanged() {
    if (!medicalStaffOptions.isEnabled()) {
      return;
    }
    Option selected = (Option) medicalStaffOptions.getSelectedItem();
    if (selected == null || selected.id() == null) {
      return;
    }
    date.setText(Dates.formatDate(nextWorkingDate(selected)));
    findSlots();
  }

  /** First date from today that the selected staff member has in the weekly template. */
  private LocalDate nextWorkingDate(Option staff) {
    Set<Integer> days = templateDays.getOrDefault(staff.id(), Set.of());
    LocalDate today = LocalDate.now();
    for (int offset = 0; offset < 14; offset++) {
      LocalDate candidate = today.plusDays(offset);
      if (days.contains(candidate.getDayOfWeek().getValue())) {
        return candidate;
      }
    }
    return today;
  }

  private void loadAppointments() {
    appointments.showLoading();
    Background.run(
        () -> UhsDao.browse("Appointment"),
        model -> {
          appointments.setModel(model);
          UiComponents.columns(
              appointments.table(),
              new String[] {"ID", "Patient", "Staff", "When", "Status"},
              new int[] {60, 80, 90, 170, 100});
        },
        appointments::showError);
  }

  private void findSlots() {
    Option staff = (Option) medicalStaffOptions.getSelectedItem();
    if (staff == null || staff.id() == null) {
      shell.showMessage("Choose a medical staff member first.");
      return;
    }
    LocalDate chosen;
    try {
      chosen = Dates.parseDate(date.getText(), "Date");
      if (chosen.isBefore(LocalDate.now())) {
        throw new ValidationException("Date must be today or later.");
      }
    } catch (Exception error) {
      slots.showError(error);
      return;
    }
    slots.setEmptyMessage(noSlotsMessage(staff, chosen));
    slots.showLoading();
    Background.run(
        () -> UhsDao.freeSlots(staff.id(), staff.toString(), chosen),
        model -> {
          slots.setModel(model);
          UiComponents.columns(
              slots.table(),
              new String[] {"Date", "Doctor", "Start", "End"},
              new int[] {120, 220, 90, 90});
        },
        slots::showError);
  }

  /** Explains an empty result with the weekdays this staff member actually works. */
  private String noSlotsMessage(Option staff, LocalDate chosen) {
    Set<Integer> days = templateDays.getOrDefault(staff.id(), Set.of());
    if (days.isEmpty()) {
      return "No consulting schedule is set up for this staff member.";
    }
    String working =
        days.stream()
            .map(day -> DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
            .collect(Collectors.joining(", "));
    return "No free slots on "
        + chosen.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        + ". This doctor works on "
        + working
        + ". Pick one of those days.";
  }
}
