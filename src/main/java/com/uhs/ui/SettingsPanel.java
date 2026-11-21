package com.uhs.ui;

import com.uhs.util.DBConnection;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Read-only connection details and a live connection test. */
public final class SettingsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final JLabel outcome = UiComponents.muted("No connection test run yet.");

  public SettingsPanel(AppShell shell, String connectionInfo) {
    super("Connection", "Where the application reads its data. No password is shown.");

    JTextArea details =
        new JTextArea(
            "Database\n"
                + connectionInfo
                + "\n\nSettings source\nEnvironment variables, then .env, "
                + "then db.properties, then built-in defaults.\n\nStack\n"
                + "JDK 25 LTS · MySQL 8.4 LTS · Connector/J 8.4 · Java Swing");
    details.setEditable(false);
    details.setLineWrap(true);
    details.setWrapStyleWord(true);
    details.setBackground(UiTheme.SURFACE_RAISED);
    details.setForeground(UiTheme.TEXT);
    details.setFont(details.getFont().deriveFont(13f));

    JButton test = UiComponents.primaryButton("Test connection");
    test.addActionListener(event -> testConnection());

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    actions.setOpaque(false);
    actions.add(test);
    actions.add(outcome);

    JPanel top = new JPanel();
    top.setOpaque(false);
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
    top.add(actions);

    body().add(UiComponents.card("Connection details", null, top), BorderLayout.NORTH);
    body()
        .add(
            UiComponents.card("Environment", "Read from DBConnection", new JScrollPane(details)),
            BorderLayout.CENTER);
  }

  private void testConnection() {
    outcome.setForeground(UiTheme.TEXT_MUTED);
    outcome.setText("Testing connection…");
    Background.run(
        () -> DBConnection.open().getCatalog(),
        catalog -> {
          outcome.setForeground(UiTheme.SUCCESS);
          outcome.setText("Connected to " + catalog + ".");
        },
        error -> {
          outcome.setForeground(UiTheme.DANGER);
          outcome.setText("Not connected. " + com.uhs.util.DbErrors.explain(error));
        });
  }
}
