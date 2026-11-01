package com.uhs.dao;

import com.uhs.model.DiagnosisCount;
import com.uhs.model.TopDiagnosesReport;
import com.uhs.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Aggregate query behind form F5 (Top-5 diagnoses). */
public final class ReportDao {

  /** The statement without the optional date filters; shown to the user on the form. */
  public static final String TOP5_SQL =
      "SELECT diagnosis, COUNT(*) AS visits\n"
          + "FROM Consultation\n"
          + "WHERE diagnosis IS NOT NULL\n"
          + "  [AND consultation_date >= ?]  -- optional: from date\n"
          + "  [AND consultation_date < ?]   -- optional: day after the to date\n"
          + "GROUP BY diagnosis\n"
          + "HAVING COUNT(*) >= ?\n"
          + "ORDER BY visits DESC, diagnosis ASC\n"
          + "LIMIT 5";

  /**
   * Top-5 most frequent diagnoses.
   *
   * @param from first day to include, or null for no lower bound
   * @param to last day to include, or null for no upper bound
   * @param minVisits a diagnosis needs at least this many consultations to be listed
   */
  public TopDiagnosesReport topDiagnoses(LocalDate from, LocalDate to, int minVisits)
      throws Exception {
    StringBuilder where = new StringBuilder(" WHERE diagnosis IS NOT NULL");
    List<Object> params = new ArrayList<>();
    if (from != null) {
      where.append(" AND consultation_date >= ?");
      params.add(Date.valueOf(from));
    }
    if (to != null) {
      where.append(" AND consultation_date < ?");
      params.add(Date.valueOf(to.plusDays(1)));
    }
    String top =
        "SELECT diagnosis, COUNT(*) AS visits FROM Consultation"
            + where
            + " GROUP BY diagnosis HAVING COUNT(*) >= ?"
            + " ORDER BY visits DESC, diagnosis ASC LIMIT 5";
    String total = "SELECT COUNT(*) FROM Consultation" + where;

    List<DiagnosisCount> rows = new ArrayList<>();
    int totalCount;
    try (Connection con = DBConnection.open()) {
      try (PreparedStatement ps = con.prepareStatement(top)) {
        int i = 1;
        for (Object p : params) {
          ps.setObject(i++, p);
        }
        ps.setInt(i, minVisits);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            rows.add(new DiagnosisCount(rs.getString(1), rs.getInt(2)));
          }
        }
      }
      try (PreparedStatement ps = con.prepareStatement(total)) {
        int i = 1;
        for (Object p : params) {
          ps.setObject(i++, p);
        }
        try (ResultSet rs = ps.executeQuery()) {
          rs.next();
          totalCount = rs.getInt(1);
        }
      }
    }
    return new TopDiagnosesReport(rows, totalCount);
  }
}
