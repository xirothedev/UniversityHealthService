package com.uhs.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class DbErrorsTest {

  @Test
  void foreignKeyOnDeleteIsExplained() {
    String text = DbErrors.explain(new SQLException("Cannot delete a parent row", "23000", 1451));
    assertTrue(text.contains("cannot be deleted"));
  }

  @Test
  void duplicateAppointmentNamesTheProblem() {
    String text =
        DbErrors.explain(
            new SQLException(
                "Duplicate entry '1' for key 'uq_consultation_appointment'", "23000", 1062));
    assertTrue(text.contains("already has a consultation"));
  }

  @Test
  void connectionFailureMentionsConfiguration() {
    String text = DbErrors.explain(new SQLException("Communications link failure", "08S01", 0));
    assertTrue(text.contains(".env"));
  }

  @Test
  void unknownErrorKeepsCodeAndNeverShowsStackTrace() {
    String text = DbErrors.explain(new SQLException("boom", "HY000", 9999));
    assertTrue(text.contains("9999"));
    assertTrue(!text.contains("at com."));
  }

  @Test
  void validationMessageIsPassedThrough() {
    assertTrue(
        DbErrors.explain(new ValidationException("Diagnosis is required."))
            .startsWith("Diagnosis"));
  }

  @Test
  void commonMysqlCodesAreExplained() {
    assertTrue(DbErrors.explain(new SQLException("x", "23000", 1452)).contains("no longer exists"));
    assertTrue(
        DbErrors.explain(new SQLException("x", "HY000", 3819)).contains("breaks a data rule"));
    assertTrue(DbErrors.explain(new SQLException("x", "23000", 1048)).contains("required value"));
    assertTrue(DbErrors.explain(new SQLException("x", "22001", 1406)).contains("too long"));
    assertTrue(
        DbErrors.explain(new SQLException("x", "28000", 1045)).contains("user name or password"));
    assertTrue(DbErrors.explain(new SQLException("x", "42000", 1049)).contains("database name"));
    assertTrue(DbErrors.explain(new SQLException("x", "42S02", 1146)).contains("table is missing"));
    assertTrue(DbErrors.explain(new SQLException("x", "23000", 9998)).contains("integrity rule"));
  }

  @Test
  void wrappedSqlExceptionIsExplained() {
    Exception wrapped = new Exception("wrapper", new SQLException("x", "23000", 1451));
    assertTrue(DbErrors.explain(wrapped).contains("cannot be deleted"));
  }
}
