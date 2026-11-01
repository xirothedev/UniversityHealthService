package com.uhs.model;

/** One entry of a lookup combo box: a database id plus the text the user sees. */
public record Option(Integer id, String label) {

  /** Placeholder entry that means "nothing chosen" (id is null). */
  public static Option none(String label) {
    return new Option(null, label);
  }

  @Override
  public String toString() {
    return label;
  }
}
