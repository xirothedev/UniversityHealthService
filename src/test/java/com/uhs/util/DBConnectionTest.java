package com.uhs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class DBConnectionTest {

  private static Properties props(String params) {
    Properties p = new Properties();
    p.setProperty("db.host", "localhost");
    p.setProperty("db.port", "3306");
    p.setProperty("db.name", "uhs_db");
    if (params != null) {
      p.setProperty("db.params", params);
    }
    return p;
  }

  @Test
  void buildsUrlFromProperties() {
    assertEquals(
        "jdbc:mysql://localhost:3306/uhs_db?useSSL=false&serverTimezone=UTC",
        DBConnection.buildUrl(props("useSSL=false&serverTimezone=UTC")));
  }

  @Test
  void defaultsToEmptyParams() {
    assertEquals("jdbc:mysql://localhost:3306/uhs_db?", DBConnection.buildUrl(props(null)));
  }
}
