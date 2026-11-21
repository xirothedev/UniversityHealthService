package com.uhs.dao;

import com.uhs.model.Consultation;
import com.uhs.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Insert, read, update and delete for the Consultation table (form F4). */
public final class ConsultationDao {

  private static final String SELECT =
      "SELECT c.consultation_id, c.patient_id, c.med_staff_id, c.appointment_id,"
          + " c.consultation_date, c.symptoms, c.diagnosis, c.notes,"
          + " p.full_name, m.full_name"
          + " FROM Consultation AS c"
          + " INNER JOIN Patient AS p ON p.patient_id = c.patient_id"
          + " INNER JOIN MedicalStaff AS m ON m.med_staff_id = c.med_staff_id"
          + " ORDER BY c.consultation_date DESC, c.consultation_id DESC";

  public List<Consultation> findAll() throws Exception {
    List<Consultation> out = new ArrayList<>();
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(SELECT);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        int appt = rs.getInt(4);
        Integer apptId = rs.wasNull() ? null : appt;
        out.add(
            new Consultation(
                rs.getInt(1),
                rs.getInt(2),
                rs.getInt(3),
                apptId,
                rs.getTimestamp(5).toLocalDateTime(),
                rs.getString(6),
                rs.getString(7),
                rs.getString(8),
                rs.getString(9),
                rs.getString(10)));
      }
    }
    return out;
  }

  /** Inserts a row and returns the generated consultation_id. */
  public int insert(Consultation c) throws Exception {
    String sql =
        "INSERT INTO Consultation (patient_id, med_staff_id, appointment_id,"
            + " consultation_date, symptoms, diagnosis, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      bind(ps, c);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        return keys.next() ? keys.getInt(1) : -1;
      }
    }
  }

  /** Updates the row with the given id; returns false when no such row exists any more. */
  public boolean update(Consultation c) throws Exception {
    String sql =
        "UPDATE Consultation SET patient_id = ?, med_staff_id = ?, appointment_id = ?,"
            + " consultation_date = ?, symptoms = ?, diagnosis = ?, notes = ?"
            + " WHERE consultation_id = ?";
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql)) {
      bind(ps, c);
      ps.setInt(8, c.id());
      return ps.executeUpdate() == 1;
    }
  }

  /** Deletes the row; returns false when it was already gone. */
  public boolean delete(int consultationId) throws Exception {
    String sql = "DELETE FROM Consultation WHERE consultation_id = ?";
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, consultationId);
      return ps.executeUpdate() == 1;
    }
  }

  private static void bind(PreparedStatement ps, Consultation c) throws Exception {
    ps.setInt(1, c.patientId());
    ps.setInt(2, c.medStaffId());
    if (c.appointmentId() == null) {
      ps.setNull(3, Types.INTEGER);
    } else {
      ps.setInt(3, c.appointmentId());
    }
    ps.setTimestamp(4, Timestamp.valueOf(c.consultationDate()));
    ps.setString(5, c.symptoms());
    ps.setString(6, c.diagnosis());
    ps.setString(7, c.notes());
  }
}
