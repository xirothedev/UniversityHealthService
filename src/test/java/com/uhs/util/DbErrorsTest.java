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
}
