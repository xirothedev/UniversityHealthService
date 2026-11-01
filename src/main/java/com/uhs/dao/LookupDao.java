package com.uhs.dao;

import com.uhs.model.Option;
import com.uhs.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Read-only lists that fill the combo boxes of the forms. */
public final class LookupDao {

  public List<Option> patients() throws Exception {
    String sql = "SELECT patient_id, full_name FROM Patient ORDER BY full_name, patient_id";
    List<Option> out = new ArrayList<>();
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        out.add(new Option(rs.getInt(1), rs.getString(2) + " (#" + rs.getInt(1) + ")"));
      }
    }
    return out;
  }

  public List<Option> doctors() throws Exception {
    String sql =
        "SELECT med_staff_id, full_name, specialty FROM MedicalStaff"
            + " ORDER BY full_name, med_staff_id";
    List<Option> out = new ArrayList<>();
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        String specialty = rs.getString(3);
        String label = rs.getString(2) + (specialty == null ? "" : " - " + specialty);
        out.add(new Option(rs.getInt(1), label));
      }
    }
    return out;
  }

  /**
   * Appointments of one patient that can still be linked to a consultation: the ones with no
   * consultation yet (an appointment can have at most one) plus the one the edited consultation
   * already uses.
   */
  public List<Option> freeAppointments(int patientId, Integer keepAppointmentId) throws Exception {
    String sql =
        "SELECT a.appointment_id, a.appointment_time, a.status FROM Appointment AS a"
            + " WHERE a.patient_id = ?"
            + " AND (NOT EXISTS (SELECT 1 FROM Consultation AS c"
            + " WHERE c.appointment_id = a.appointment_id) OR a.appointment_id = ?)"
            + " ORDER BY a.appointment_time DESC";
    List<Option> out = new ArrayList<>();
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, patientId);
      if (keepAppointmentId == null) {
        ps.setNull(2, Types.INTEGER);
      } else {
        ps.setInt(2, keepAppointmentId);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String when = String.valueOf(rs.getTimestamp(2).toLocalDateTime()).replace('T', ' ');
          out.add(
              new Option(
                  rs.getInt(1), "#" + rs.getInt(1) + "  " + when + "  [" + rs.getString(3) + "]"));
        }
      }
    }
    return out;
  }
}
