package com.uhs.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// Externalized connection (ADR-07): real ENV > .env file > db.properties > defaults.
// Never hard-code user/pass/URL. Every statement taking user input must be a
// PreparedStatement with try-with-resources in the DAO layer.
public final class DBConnection {
  private DBConnection() {}

  static final String DEFAULT_HOST = "localhost";
  static final String DEFAULT_PORT = "3306";
  static final String DEFAULT_NAME = "uhs_db";
  static final String DEFAULT_PARAMS =
      "useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

  public static String buildUrl(Properties p) {
    String params = p.getProperty("db.params", "");
    String base =
        "jdbc:mysql://"
            + p.getProperty("db.host", DEFAULT_HOST)
            + ":"
            + p.getProperty("db.port", DEFAULT_PORT)
            + "/"
            + p.getProperty("db.name", DEFAULT_NAME);
    if (params == null || params.isBlank()) {
      return base;
    }
    return base + "?" + params;
  }

  /** Minimal KEY=VALUE .env reader, no extra dependency needed. */
  static Map<String, String> parseDotEnv(List<String> lines) {
    Map<String, String> out = new LinkedHashMap<>();
    for (String raw : lines) {
      String line = raw.strip();
      if (line.isEmpty() || line.startsWith("#")) {
        continue;
      }
      if (line.startsWith("export ")) {
        line = line.substring(7).stripLeading();
      }
      int eq = line.indexOf('=');
      if (eq <= 0) {
        continue;
      }
      String key = line.substring(0, eq).strip();
      String value = line.substring(eq + 1).strip();
      if (value.length() >= 2
          && ((value.startsWith("\"") && value.endsWith("\""))
              || (value.startsWith("'") && value.endsWith("'")))) {
        value = value.substring(1, value.length() - 1);
      }
      int comment = value.indexOf(" #");
      if (comment >= 0) {
        value = value.substring(0, comment).stripTrailing();
      }
      out.put(key, value);
    }
    return out;
  }

  static void overlayEnv(Properties p, Map<String, String> env) {
    if (env.containsKey("DB_HOST")) {
      p.setProperty("db.host", env.get("DB_HOST"));
    }
    if (env.containsKey("DB_PORT")) {
      p.setProperty("db.port", env.get("DB_PORT"));
    }
    if (env.containsKey("DB_DATABASE")) {
      p.setProperty("db.name", env.get("DB_DATABASE"));
    }
    if (env.containsKey("DB_USERNAME")) {
      p.setProperty("db.user", env.get("DB_USERNAME"));
    }
    if (env.containsKey("DB_PASSWORD")) {
      p.setProperty("db.password", env.get("DB_PASSWORD"));
    }
    if (env.containsKey("DB_PARAMS")) {
      p.setProperty("db.params", env.get("DB_PARAMS"));
    }
  }

  /** Twelve-factor order: real ENV overrides the .env file, which overrides db.properties. */
  public static Properties loadSettings() throws IOException {
    Properties p = new Properties();
    Path propsPath = Path.of("db.properties");
    if (Files.exists(propsPath)) {
      try (FileInputStream in = new FileInputStream(propsPath.toFile())) {
        p.load(in);
      }
    }
    Path dotenvPath = Path.of(".env");
    if (Files.exists(dotenvPath)) {
      overlayEnv(p, parseDotEnv(Files.readAllLines(dotenvPath)));
    }
    overlayEnv(p, System.getenv());
    p.putIfAbsent("db.host", DEFAULT_HOST);
    p.putIfAbsent("db.port", DEFAULT_PORT);
    p.putIfAbsent("db.name", DEFAULT_NAME);
    p.putIfAbsent("db.params", DEFAULT_PARAMS);
    return p;
  }

  public static Connection open() throws Exception {
    Properties p = loadSettings();
    return DriverManager.getConnection(
        buildUrl(p), p.getProperty("db.user"), p.getProperty("db.password"));
  }
}
