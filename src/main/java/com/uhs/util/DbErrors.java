package com.uhs.util;

import java.sql.SQLException;

/** Turns JDBC errors into plain-language messages (no stack traces for the user). */
public final class DbErrors {
  private DbErrors() {}

  /** Returns a short sentence a non-programmer can act on. */
  public static String explain(SQLException e) {
    String text = e.getMessage() == null ? "" : e.getMessage();
    String state = e.getSQLState() == null ? "" : e.getSQLState();
    switch (e.getErrorCode()) {
      case 1451:
        return "This record cannot be deleted or changed because other records still depend on"
            + " it. Remove or reassign those records first.";
      case 1452:
        return "A selected patient, doctor or appointment no longer exists. Refresh the lists"
            + " and try again.";
      case 1062:
        if (text.contains("uq_consultation_appointment")) {
          return "That appointment already has a consultation. Pick another appointment or"
              + " leave it empty for a walk-in.";
        }
        return "A record with the same unique value already exists.";
      case 3819:
        return "One of the values breaks a data rule (for example a negative number or an"
            + " unknown status). Check the fields and try again.";
      case 1048:
        return "A required value is missing.";
      case 1406:
        return "One of the texts is too long for its column. Please shorten it.";
      case 1292:
      case 1366:
      case 1265:
        return "One of the values has the wrong format (check dates and numbers).";
      case 1045:
        return "The database refused the user name or password. Check DB_USERNAME and"
            + " DB_PASSWORD in .env.";
      case 1049:
        return "The database name was not found. Run sql/01_schema.sql first or check"
            + " DB_DATABASE in .env.";
      case 1146:
        return "A table is missing. Run sql/01_schema.sql and sql/02_data.sql first.";
      default:
        break;
    }
    if (state.startsWith("08")) {
      return "Cannot reach the database. Check that the server is running and that DB_HOST,"
          + " DB_PORT and DB_PARAMS in .env are correct.";
    }
    if (state.startsWith("23")) {
      return "The database rejected the change because it breaks an integrity rule.";
    }
    return "Database error (code " + e.getErrorCode() + "): " + text;
  }

  /** Same as {@link #explain(SQLException)} but accepts any exception from the DAO layer. */
  public static String explain(Exception e) {
    if (e instanceof ValidationException) {
      return e.getMessage();
    }
    if (e instanceof SQLException sql) {
      return explain(sql);
    }
    Throwable cause = e.getCause();
    if (cause instanceof SQLException sql) {
      return explain(sql);
    }
    String text = e.getMessage();
    return text == null || text.isBlank()
        ? "Unexpected error: " + e.getClass().getSimpleName()
        : text;
  }
}
