package com.uhs.ui;

import com.uhs.dao.LookupDao;
import com.uhs.dao.UhsDao;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

/** Appointment desk: weekly free slots for one MedicalStaff member and all appointments. */
public final class AppointmentsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final JComboBox<Option> medicalStaffOptions = new JComboBox<>();
  private final JTextField date = new JTextField(Dates.formatDate(LocalDate.now().plusDays(1)), 12);
  private final TableCard slots = new TableCard();
  private final TableCard appointments = new TableCard();

  public AppointmentsPanel(AppShell shell) {
    super("Appointments", "Find an open consultation slot in the coming week.");
    this.shell = shell;

    JButton find = UiComponents.primaryButton("Find free slots");
    find.addActionListener(event -> findSlots());
    date.addActionListener(event -> findSlots());

    JPanel controls = new JPanel();
    controls.setOpaque(false);
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    row.setOpaque(false);
    row.add(UiComponents.muted("Doctor"));
    row.add(medicalStaffOptions);
    row.add(UiComponents.muted("Date"));
    row.add(date);
    row.add(find);
    controls.add(row);

    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card("Open slots", "Split from the weekly consulting template", slots),
            UiComponents.card(
                "Booked appointments", "All appointments in the database", appointments));
    split.setResizeWeight(0.55);
    split.setBorder(null);

    body().add(UiComponents.card("Find a slot", null, controls), BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    Background.run(
        () -> new LookupDao().doctors(),
        loaded -> {
          medicalStaffOptions.removeAllItems();
          for (Option medicalStaff : loaded) {
            medicalStaffOptions.addItem(medicalStaff);
          }
          if (!loaded.isEmpty()) {
            findSlots();
          }
        },
        shell::showError);
    Background.run(() -> UhsDao.browse("Appointment"), appointments::setModel, shell::showError);
  }

  private void findSlots() {
    Option medicalStaff = (Option) medicalStaffOptions.getSelectedItem();
    if (medicalStaff == null || medicalStaff.id() == null) {
      shell.showMessage("Choose a medical staff member first.");
      return;
    }
    Background.run(
        () -> {
          LocalDate chosen = Dates.parseDate(date.getText(), "Date");
          if (chosen.isBefore(LocalDate.now())) {
            throw new com.uhs.util.ValidationException("Date must be today or later.");
          }
          return UhsDao.freeSlots(medicalStaff.id(), medicalStaff.toString(), chosen);
        },
        slots::setModel,
        shell::showError);
  }
}
