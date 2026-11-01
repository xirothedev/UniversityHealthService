package com.uhs.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Strict date parsing shared by the forms. All formats are year-month-day, 24-hour clock. */
public final class Dates {
  private Dates() {}

  public static final String DATE_HINT = "yyyy-MM-dd";
  public static final String DATE_TIME_HINT = "yyyy-MM-dd HH:mm";

  private static final DateTimeFormatter DATE =
      DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter DATE_TIME =
      DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter DATE_TIME_SECONDS =
      DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

  public static final LocalDate MIN_DATE = LocalDate.of(2000, 1, 1);
  public static final LocalDate MAX_DATE = LocalDate.of(2100, 12, 31);

  /** Parses a date, or throws a {@link ValidationException} that names the field. */
  public static LocalDate parseDate(String text, String fieldName) throws ValidationException {
    String t = text == null ? "" : text.strip();
    try {
      LocalDate d = LocalDate.parse(t, DATE);
      requireInRange(d, fieldName);
      return d;
    } catch (DateTimeParseException e) {
      throw new ValidationException(fieldName + " must be a real date as " + DATE_HINT + ".");
    }
  }

  /** Parses a date and time (seconds optional), or throws a {@link ValidationException}. */
  public static LocalDateTime parseDateTime(String text, String fieldName)
      throws ValidationException {
    String t = text == null ? "" : text.strip();
    LocalDateTime value;
    try {
      value = LocalDateTime.parse(t, DATE_TIME);
    } catch (DateTimeParseException first) {
      try {
        value = LocalDateTime.parse(t, DATE_TIME_SECONDS);
      } catch (DateTimeParseException second) {
        throw new ValidationException(
            fieldName + " must be a real date and time as " + DATE_TIME_HINT + ".");
      }
    }
    requireInRange(value.toLocalDate(), fieldName);
    return value;
  }

  public static String formatDateTime(LocalDateTime value) {
    return value == null ? "" : DATE_TIME.format(value);
  }

  public static String formatDate(LocalDate value) {
    return value == null ? "" : DATE.format(value);
  }

  private static void requireInRange(LocalDate d, String fieldName) throws ValidationException {
    if (d.isBefore(MIN_DATE) || d.isAfter(MAX_DATE)) {
      throw new ValidationException(
          fieldName + " must be between " + MIN_DATE + " and " + MAX_DATE + ".");
    }
  }
}
