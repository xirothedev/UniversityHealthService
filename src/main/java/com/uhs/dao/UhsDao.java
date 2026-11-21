package com.uhs.dao;

import com.uhs.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/**
 * Read-only queries for forms F1-F3. Every statement is a PreparedStatement in try-with-resources.
 */
public final class UhsDao {
  private UhsDao() {}

  public static final List<String> TABLES =
      List.of(
          "Allergy",
          "Appointment",
          "Building",
          "Consultation",
          "ConsultSlot",
          "ContactTrace",
          "MedicalStaff",
          "Medicine",
          "NotifiableCase",
          "Patient",
          "PatientAllergy",
          "Prescription",
          "PrescriptionLine",
          "Staff",
          "Student",
          "VaccinationCourse",
          "VaccinationRecord");

  /** Generic query -> table model. */
  public static DefaultTableModel query(String sql, Object... params) throws Exception {
    try (Connection c = DBConnection.open();
        PreparedStatement ps = c.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        ResultSetMetaData md = rs.getMetaData();
        int n = md.getColumnCount();
        DefaultTableModel m =
            new DefaultTableModel() {
              @Override
              public boolean isCellEditable(int r, int col) {
                return false;
              }
            };
        for (int i = 1; i <= n; i++) {
          m.addColumn(md.getColumnLabel(i));
        }
        while (rs.next()) {
          Object[] row = new Object[n];
          for (int i = 1; i <= n; i++) {
            row[i - 1] = rs.getObject(i);
          }
          m.addRow(row);
        }
        return m;
      }
    }
  }

  // ---------- F1 ----------
  public static DefaultTableModel browse(String table) throws Exception {
    String sql =
        switch (table) {
          case "Allergy" -> "SELECT * FROM Allergy";
          case "Appointment" -> "SELECT * FROM Appointment";
          case "Building" -> "SELECT * FROM Building";
          case "Consultation" -> "SELECT * FROM Consultation";
          case "ConsultSlot" -> "SELECT * FROM ConsultSlot";
          case "ContactTrace" -> "SELECT * FROM ContactTrace";
          case "MedicalStaff" -> "SELECT * FROM MedicalStaff";
          case "Medicine" -> "SELECT * FROM Medicine";
          case "NotifiableCase" -> "SELECT * FROM NotifiableCase";
          case "Patient" -> "SELECT * FROM Patient";
          case "PatientAllergy" -> "SELECT * FROM PatientAllergy";
          case "Prescription" -> "SELECT * FROM Prescription";
          case "PrescriptionLine" -> "SELECT * FROM PrescriptionLine";
          case "Staff" -> "SELECT * FROM Staff";
          case "Student" -> "SELECT * FROM Student";
          case "VaccinationCourse" -> "SELECT * FROM VaccinationCourse";
          case "VaccinationRecord" -> "SELECT * FROM VaccinationRecord";
          default -> throw new IllegalArgumentException("Unknown table: " + table);
        };
    return query(sql);
  }

  // ---------- F2 ----------
  public static DefaultTableModel patientHistory(String nameLike) throws Exception {
    // escape LIKE wildcards so '%' and '_' typed by the user are matched literally
    String escaped = nameLike.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    return query(
        "SELECT p.patient_id, p.full_name, c.consultation_id, c.consultation_date, "
            + "m.full_name AS doctor, c.symptoms, c.diagnosis, c.notes "
            + "FROM Patient p "
            + "JOIN Consultation c ON c.patient_id = p.patient_id "
            + "JOIN MedicalStaff m ON m.med_staff_id = c.med_staff_id "
            + "WHERE p.full_name LIKE ? ESCAPE '\\\\' "
            + "ORDER BY p.full_name, c.consultation_date DESC",
        "%" + escaped + "%");
  }

  /** ConsultSlot is a weekly schedule: split it into booking units, drop those already booked. */
  public static DefaultTableModel freeSlots(int doctorId, String doctorName, LocalDate date)
      throws Exception {
    int weekday = date.getDayOfWeek().getValue(); // 1 = Monday, same as ConsultSlot.weekday

    DefaultTableModel schedule =
        query(
            "SELECT start_time, end_time, booking_unit_min FROM ConsultSlot "
                + "WHERE med_staff_id = ? AND weekday = ? ORDER BY start_time",
            doctorId,
            weekday);

    DefaultTableModel booked =
        query(
            "SELECT TIME(appointment_time) FROM Appointment "
                + "WHERE med_staff_id = ? AND DATE(appointment_time) = ?",
            doctorId,
            java.sql.Date.valueOf(date));
    Set<LocalTime> taken = new HashSet<>();
    for (int i = 0; i < booked.getRowCount(); i++) {
      taken.add(LocalTime.parse(String.valueOf(booked.getValueAt(i, 0))));
    }

    DefaultTableModel out =
        new DefaultTableModel(new Object[] {"Date", "Doctor", "Start", "End"}, 0) {
          @Override
          public boolean isCellEditable(int r, int col) {
            return false;
          }
        };
    for (int i = 0; i < schedule.getRowCount(); i++) {
      LocalTime start = LocalTime.parse(String.valueOf(schedule.getValueAt(i, 0)));
      LocalTime end = LocalTime.parse(String.valueOf(schedule.getValueAt(i, 1)));
      int unit = ((Number) schedule.getValueAt(i, 2)).intValue();
      for (LocalTime t = start; !t.plusMinutes(unit).isAfter(end); t = t.plusMinutes(unit)) {
        if (!taken.contains(t)) {
          out.addRow(new Object[] {date, doctorName, t, t.plusMinutes(unit)});
        }
      }
    }
    return out;
  }

  // ---------- F3 ----------
  public static DefaultTableModel consultations() throws Exception {
    return query(
        "SELECT c.consultation_id, c.consultation_date, p.full_name AS patient, "
            + "m.full_name AS doctor, c.diagnosis "
            + "FROM Consultation c "
            + "JOIN Patient p ON p.patient_id = c.patient_id "
            + "JOIN MedicalStaff m ON m.med_staff_id = c.med_staff_id "
            + "ORDER BY c.consultation_date DESC");
  }

  public static DefaultTableModel prescriptionLines(int consultationId) throws Exception {
    return query(
        "SELECT pl.line_no, md.medicine_name, pl.dosage, pl.frequency, "
            + "pl.duration_days, pl.quantity, pl.unit_price, "
            + "(pl.quantity * pl.unit_price) AS line_total "
            + "FROM Prescription pr "
            + "JOIN PrescriptionLine pl ON pl.prescription_id = pr.prescription_id "
            + "JOIN Medicine md ON md.medicine_id = pl.medicine_id "
            + "WHERE pr.consultation_id = ? ORDER BY pl.line_no",
        consultationId);
  }
}
