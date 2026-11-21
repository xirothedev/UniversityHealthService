package com.uhs.ui;

import com.uhs.util.DBConnection;
import com.uhs.util.DbErrors;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/** Single application window: a navigation rail, a card area and a status bar. */
public final class AppShell extends JFrame {
  private static final long serialVersionUID = 1L;

  private final JPanel pages = new JPanel(new CardLayout());
  private final Map<String, ScreenPanel> screens = new LinkedHashMap<>();
  private final Map<String, JButton> navigation = new LinkedHashMap<>();
  private final JLabel status;

  public AppShell(String connectionInfo) {
    super("University Health Service");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(1100, 720));
    setSize(1440, 900);
    setLocationRelativeTo(null);

    add(buildRail(), BorderLayout.WEST);
    add(pages, BorderLayout.CENTER);

    status = new JLabel("  " + connectionInfo);
    status.setForeground(UiTheme.TEXT_MUTED);
    status.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    add(status, BorderLayout.NORTH);

    register("overview", new DashboardPanel(this));
    register("patients", new PatientsPanel(this));
    register("appointments", new AppointmentsPanel(this));
    register("consultations", new ConsultationsPanel(this));
    register("prescriptions", new PrescriptionsPanel(this));
    register("vaccinations", new VaccinationsPanel(this));
    register("outbreaks", new OutbreaksPanel(this));
    register("reports", new ReportsPanel(this));
    register("queries", new QueriesPanel(this));
    register("settings", new SettingsPanel(this, connectionInfo));

    navigate("overview");
  }

  /** Returns the connection description without a password. */
  public static String describeConnection(Properties properties) {
    return DBConnection.buildUrl(properties) + "  user=" + properties.getProperty("db.user");
  }

  /** Shows the named screen and highlights its rail button. */
  public void navigate(String key) {
    ScreenPanel screen = screens.get(key);
    if (screen == null) {
      return;
    }
    screen.ensureLoaded();
    CardLayout layout = (CardLayout) pages.getLayout();
    layout.show(pages, key);
    for (Map.Entry<String, JButton> entry : navigation.entrySet()) {
      boolean selected = entry.getKey().equals(key);
      entry.getValue().setBackground(selected ? UiTheme.SURFACE_RAISED : UiTheme.SURFACE);
      entry.getValue().setForeground(selected ? UiTheme.TEXT : UiTheme.TEXT_MUTED);
    }
  }

  /** Shows a plain-language database or validation error. */
  public void showError(Exception error) {
    JOptionPane.showMessageDialog(
        this, DbErrors.explain(error), "Cannot complete the action", JOptionPane.ERROR_MESSAGE);
  }

  /** Shows a short confirmation or result message. */
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(
        this, message, "University Health Service", JOptionPane.INFORMATION_MESSAGE);
  }

  private void register(String key, ScreenPanel panel) {
    screens.put(key, panel);
    pages.add(panel, key);
  }

  private JPanel buildRail() {
    JPanel rail = new JPanel();
    rail.setBackground(UiTheme.SURFACE);
    rail.setPreferredSize(new Dimension(248, 0));
    rail.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiTheme.BORDER));
    rail.setLayout(new BoxLayout(rail, BoxLayout.Y_AXIS));

    JPanel brand = new JPanel();
    brand.setOpaque(false);
    brand.setLayout(new BoxLayout(brand, BoxLayout.X_AXIS));
    brand.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));
    JLabel mark = new JLabel("U+");
    mark.setOpaque(true);
    mark.setBackground(UiTheme.ACCENT_DARK);
    mark.setForeground(UiTheme.TEXT);
    mark.setHorizontalAlignment(SwingConstants.CENTER);
    mark.setFont(UiTheme.bold(mark.getFont(), 16f));
    mark.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    brand.add(mark);
    brand.add(Box.createHorizontalStrut(12));
    JPanel names = new JPanel();
    names.setOpaque(false);
    names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
    names.add(UiComponents.label("Health Service", 15));
    names.add(UiComponents.muted("University clinic"));
    brand.add(names);
    rail.add(brand);

    rail.add(groupLabel("WORKSPACE"));
    rail.add(navButton("overview", "Overview", "O", UiTheme.ACCENT));

    rail.add(groupLabel("CLINICAL CARE"));
    rail.add(navButton("patients", "Patients", "P", UiTheme.SUCCESS));
    rail.add(navButton("appointments", "Appointments", "A", UiTheme.WARNING));
    rail.add(navButton("consultations", "Consultations", "C", UiTheme.ACCENT));
    rail.add(navButton("prescriptions", "Prescriptions", "R", UiTheme.SUCCESS));
    rail.add(navButton("vaccinations", "Vaccinations", "V", UiTheme.WARNING));

    rail.add(groupLabel("INSIGHT"));
    rail.add(navButton("outbreaks", "Outbreaks", "!", UiTheme.DANGER));
    rail.add(navButton("reports", "Reports", "T", UiTheme.ACCENT));
    rail.add(navButton("queries", "Topic queries", "Q", UiTheme.SUCCESS));

    rail.add(Box.createVerticalGlue());
    rail.add(groupLabel("SYSTEM"));
    rail.add(navButton("settings", "Connection", "S", UiTheme.TEXT_MUTED));
    rail.add(Box.createVerticalStrut(18));
    return rail;
  }

  private JLabel groupLabel(String text) {
    JLabel label = UiComponents.muted(text);
    label.setFont(UiTheme.bold(label.getFont(), 10f));
    label.setBorder(BorderFactory.createEmptyBorder(10, 22, 8, 0));
    return label;
  }

  private JButton navButton(String key, String text, String glyph, java.awt.Color color) {
    JButton button = new JButton(text, UiComponents.glyphIcon(glyph, color));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    button.setIconTextGap(12);
    button.setFocusPainted(false);
    button.setOpaque(true);
    button.setBackground(UiTheme.SURFACE);
    button.setForeground(UiTheme.TEXT_MUTED);
    button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    button.addActionListener(event -> navigate(key));
    navigation.put(key, button);
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    return button;
  }
}
