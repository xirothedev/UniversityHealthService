package com.uhs.dao;

/**
 * One of the five topic queries on the menu. The SQL is shown to the user next to the result, so
 * every screen can be traced back to the statement behind it.
 */
public record TopicQuery(String code, String title, String question, String sql, Input input) {

  /** What the user has to supply before the query can run. */
  public enum Input {
    NONE,
    PATIENT,
    DATE
  }
}
