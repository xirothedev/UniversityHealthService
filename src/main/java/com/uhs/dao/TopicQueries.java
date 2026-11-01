package com.uhs.dao;

import com.uhs.dao.TopicQuery.Input;
import com.uhs.model.ResultTable;
import com.uhs.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** The topic queries (Q-T1, Q-T2, Q-T4, Q-T5) and a generic runner. Q-T3 is form F5. */
public final class TopicQueries {
  private TopicQueries() {}

  public static final TopicQuery HISTORY =
      new TopicQuery(
          "Q-T1",
          "Consultation history of one patient",
          "What is the full consultation history of the chosen patient, with the doctor and the"
              + " medicines prescribed at each visit?",
          """
          SELECT c.consultation_id, c.consultation_date, m.full_name AS doctor,
                 c.symptoms, c.diagnosis,
                 COALESCE(GROUP_CONCAT(md.medicine_name ORDER BY pl.line_no
                          SEPARATOR '; '), '-') AS medicines
          FROM Patient AS p
          INNER JOIN Consultation AS c ON c.patient_id = p.patient_id
          INNER JOIN MedicalStaff AS m ON m.med_staff_id = c.med_staff_id
          LEFT JOIN Prescription AS pr ON pr.consultation_id = c.consultation_id
          LEFT JOIN PrescriptionLine AS pl ON pl.prescription_id = pr.prescription_id
          LEFT JOIN Medicine AS md ON md.medicine_id = pl.medicine_id
          WHERE p.patient_id = ?
          GROUP BY c.consultation_id, c.consultation_date, m.full_name,
                   c.symptoms, c.diagnosis
          ORDER BY c.consultation_date
          """,
          Input.PATIENT);

  public static final TopicQuery FREE_SLOTS =
      new TopicQuery(
          "Q-T2",
          "Free consultation slots on a date",
          "Which doctors have a free bookable time on the chosen date? A time is free when no"
              + " scheduled or completed appointment covers it.",
          """
          WITH RECURSIVE input AS (
            SELECT CAST(? AS DATE) AS d
          ),
          day_slots AS (
            SELECT cs.slot_id, cs.med_staff_id, cs.start_time AS unit_start,
                   cs.end_time, cs.booking_unit_min
            FROM ConsultSlot AS cs
            INNER JOIN input AS i ON cs.weekday = WEEKDAY(i.d) + 1
            WHERE TIME_TO_SEC(cs.start_time) + cs.booking_unit_min * 60
                  <= TIME_TO_SEC(cs.end_time)
            UNION ALL
            SELECT slot_id, med_staff_id,
                   SEC_TO_TIME(TIME_TO_SEC(unit_start) + booking_unit_min * 60),
                   end_time, booking_unit_min
            FROM day_slots
            WHERE TIME_TO_SEC(unit_start) + 2 * booking_unit_min * 60
                  <= TIME_TO_SEC(end_time)
          )
          SELECT m.full_name AS doctor, m.specialty,
                 TIMESTAMP(i.d, ds.unit_start) AS free_from,
                 ds.booking_unit_min AS minutes
          FROM day_slots AS ds
          CROSS JOIN input AS i
          INNER JOIN MedicalStaff AS m ON m.med_staff_id = ds.med_staff_id
          LEFT JOIN Appointment AS a
            ON a.med_staff_id = ds.med_staff_id
           AND a.status IN ('scheduled', 'completed')
           AND a.appointment_time >= TIMESTAMP(i.d, ds.unit_start)
           AND a.appointment_time < TIMESTAMP(i.d, ds.unit_start)
                                    + INTERVAL ds.booking_unit_min MINUTE
          WHERE a.appointment_id IS NULL
          ORDER BY m.full_name, free_from
          """,
          Input.DATE);

  public static final TopicQuery FULL_DOSE =
      new TopicQuery(
          "Q-T4",
          "Patients who received every required dose",
          "Which patients have received every required dose of a vaccination course they"
              + " started? (There is no dose number from 1 to required_doses that is missing.)",
          """
          WITH RECURSIVE dose_no (k) AS (
            SELECT 1
            UNION ALL
            SELECT k + 1 FROM dose_no WHERE k < 100
          )
          SELECT DISTINCT p.patient_id, p.full_name, vc.course_name, vc.required_doses,
                 (SELECT MAX(d.date_given) FROM VaccinationRecord AS d
                  WHERE d.patient_id = vr.patient_id
                    AND d.course_id = vr.course_id) AS completed_on
          FROM VaccinationRecord AS vr
          INNER JOIN Patient AS p ON p.patient_id = vr.patient_id
          INNER JOIN VaccinationCourse AS vc ON vc.course_id = vr.course_id
          WHERE NOT EXISTS (
            SELECT 1 FROM dose_no
            WHERE dose_no.k <= vc.required_doses
              AND NOT EXISTS (
                SELECT 1 FROM VaccinationRecord AS d2
                WHERE d2.patient_id = vr.patient_id
                  AND d2.course_id = vr.course_id
                  AND d2.dose_number = dose_no.k))
          ORDER BY p.full_name, vc.course_name
          """,
          Input.NONE);

  public static final TopicQuery OUTBREAK =
      new TopicQuery(
          "Q-T5",
          "Same disease in one building within 14 days",
          "Where did the same notifiable disease appear at least twice in one building within"
              + " 14 days of each other?",
          """
          SELECT c1.disease_name, b.building_name,
                 COUNT(DISTINCT c1.case_id) AS cases_in_cluster,
                 MIN(c1.date_reported) AS first_report,
                 MAX(c1.date_reported) AS last_report
          FROM NotifiableCase AS c1
          INNER JOIN NotifiableCase AS c2
            ON c2.disease_name = c1.disease_name
           AND c2.building_id = c1.building_id
           AND c2.case_id <> c1.case_id
           AND ABS(DATEDIFF(c1.date_reported, c2.date_reported)) <= 14
          INNER JOIN Building AS b ON b.building_id = c1.building_id
          GROUP BY c1.disease_name, c1.building_id, b.building_name
          ORDER BY cases_in_cluster DESC, c1.disease_name
          """,
          Input.NONE);

  /** Runs any of the statements above; every user value is bound as a parameter. */
  public static ResultTable run(String sql, Object... params) throws Exception {
    try (Connection con = DBConnection.open();
        PreparedStatement ps = con.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        ResultSetMetaData md = rs.getMetaData();
        int n = md.getColumnCount();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
          columns.add(md.getColumnLabel(i));
        }
        List<Object[]> rows = new ArrayList<>();
        while (rs.next()) {
          Object[] row = new Object[n];
          for (int i = 1; i <= n; i++) {
            row[i - 1] = display(rs.getObject(i));
          }
          rows.add(row);
        }
        return new ResultTable(columns, rows);
      }
    }
  }

  private static Object display(Object v) {
    if (v instanceof Timestamp t) {
      return t.toLocalDateTime().toString().replace('T', ' ');
    }
    if (v instanceof Time t) {
      return t.toString();
    }
    return v;
  }
}
