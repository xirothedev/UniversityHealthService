package com.uhs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
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
  void omitsQuestionMarkWhenParamsEmpty() {
    assertEquals("jdbc:mysql://localhost:3306/uhs_db", DBConnection.buildUrl(props(null)));
    assertEquals("jdbc:mysql://localhost:3306/uhs_db", DBConnection.buildUrl(props("  ")));
  }

  @Test
  void buildsTidbTlsUrl() {
    Properties p = props("sslMode=VERIFY_IDENTITY&serverTimezone=UTC");
    p.setProperty("db.host", "gateway01.ap-southeast-1.prod.aws.tidbcloud.com");
    p.setProperty("db.port", "4000");
    p.setProperty("db.name", "test");
    assertEquals(
        "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/test"
            + "?sslMode=VERIFY_IDENTITY&serverTimezone=UTC",
        DBConnection.buildUrl(p));
  }

  @Test
  void parsesDotEnvAndStripsQuotes() {
    Map<String, String> env =
        DBConnection.parseDotEnv(
            List.of(
                "# comment",
                "DB_HOST=gateway01.ap-southeast-1.prod.aws.tidbcloud.com",
                "DB_PORT=4000",
                "export DB_DATABASE=test",
                "DB_USERNAME='39QG81so169ZVAt.root'",
                "DB_PASSWORD=\"secret\""));
    assertEquals("gateway01.ap-southeast-1.prod.aws.tidbcloud.com", env.get("DB_HOST"));
    assertEquals("4000", env.get("DB_PORT"));
    assertEquals("test", env.get("DB_DATABASE"));
    assertEquals("39QG81so169ZVAt.root", env.get("DB_USERNAME"));
    assertEquals("secret", env.get("DB_PASSWORD"));
  }

  @Test
  void envOverlayMapsDbKeys() {
    Properties p = props("useSSL=false");
    DBConnection.overlayEnv(
        p,
        Map.of(
            "DB_HOST", "tidb.example",
            "DB_PORT", "4000",
            "DB_DATABASE", "test",
            "DB_USERNAME", "root",
            "DB_PASSWORD", "pw",
            "DB_PARAMS", "sslMode=VERIFY_IDENTITY"));
    assertEquals(
        "jdbc:mysql://tidb.example:4000/test?sslMode=VERIFY_IDENTITY", DBConnection.buildUrl(p));
    assertEquals("root", p.getProperty("db.user"));
  }
}
