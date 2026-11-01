package com.uhs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DatesTest {

  @Test
  void parsesDate() throws Exception {
    assertEquals(LocalDate.of(2026, 10, 31), Dates.parseDate(" 2026-10-31 ", "Date"));
  }

  @Test
  void parsesDateTimeWithAndWithoutSeconds() throws Exception {
    LocalDateTime expected = LocalDateTime.of(2026, 10, 1, 8, 30);
    assertEquals(expected, Dates.parseDateTime("2026-10-01 08:30", "When"));
    assertEquals(expected, Dates.parseDateTime("2026-10-01 08:30:00", "When"));
  }

  @Test
  void rejectsImpossibleAndMalformedDates() {
    assertThrows(ValidationException.class, () -> Dates.parseDate("2026-02-30", "Date"));
    assertThrows(ValidationException.class, () -> Dates.parseDate("31/10/2026", "Date"));
    assertThrows(ValidationException.class, () -> Dates.parseDateTime("2026-10-01 25:00", "When"));
    assertThrows(ValidationException.class, () -> Dates.parseDate("", "Date"));
  }

  @Test
  void rejectsOutOfRangeYears() {
    assertThrows(ValidationException.class, () -> Dates.parseDate("1999-12-31", "Date"));
    assertThrows(ValidationException.class, () -> Dates.parseDate("2101-01-01", "Date"));
  }

  @Test
  void formatsRoundTrip() throws Exception {
    LocalDateTime v = Dates.parseDateTime("2026-10-07 13:30", "When");
    assertEquals("2026-10-07 13:30", Dates.formatDateTime(v));
  }
}
