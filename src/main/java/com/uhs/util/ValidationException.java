package com.uhs.util;

/** Thrown when user input is rejected before it reaches the database. */
public class ValidationException extends Exception {
  private static final long serialVersionUID = 1L;

  public ValidationException(String message) {
    super(message);
  }
}
