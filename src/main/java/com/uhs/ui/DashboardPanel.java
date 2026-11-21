package com.uhs.ui;

import com.uhs.dao.ReportDao;
import com.uhs.dao.UhsDao;
import com.uhs.model.DiagnosisCount;
import com.uhs.model.TopDiagnosesReport;
import com.uhs.util.DbErrors;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/** Dashboard home: headline counts, shortcuts and a live view of recent activity. */
public final class DashboardPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final JPanel tiles = new JPanel(new GridLayout(1, 4, 16, 0));
  private final JLabel hint = UiComponents.muted("");
  private final TableCard recent = new TableCard();
  private final TableCard diagnoses = new TableCard();
  private final BarChart chart = new BarChart();

  private record DashboardData(
      int patients,
      int consultations,
      int appointments,
      int cases,
      DefaultTableModel recentRows,
      DefaultTableModel topRows,
      java.util.List<DiagnosisCount> topDiagnoses) {}

  public DashboardPanel(AppShell shell) {
    super("Overview", "A live view of the University Health Service.");
    tiles.setOpaque(false);
    recent.setRetry(this::refresh);
    diagnoses.setRetry(this::refresh);
    chart.setRetry(this::refresh);

    JButton refreshButton = UiComponents.ghostButton("Refresh");
    refreshButton.addActionListener(event -> refresh());
    addActions(refreshButton);

    JPanel strip = new JPanel(new BorderLayout());
    strip.setOpaque(false);
    strip.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
    strip.add(tiles, BorderLayout.CENTER);
    strip.add(hint, BorderLayout.SOUTH);

    JPanel lower = new JPanel(new GridLayout(1, 3, 18, 0));
    lower.setOpaque(false);
    lower.add(
        UiComponents.card("Recent consultations", "Newest first, across all clinics", recent));
    lower.add(UiComponents.card("Top diagnoses", "Most frequent diagnoses in the data", diagnoses));
    lower.add(UiComponents.card("Visit mix", "Consultations per diagnosis", chart));

    body().add(strip, BorderLayout.NORTH);
    body().add(lower, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    refresh();
  }

  private void refresh() {
    showTiles("…", "…", "…", "…");
    hint.setForeground(UiTheme.TEXT_MUTED);
    hint.setText("Loading latest activity…");
    recent.showLoading();
    diagnoses.showLoading();
    chart.showLoading();
    Background.run(
        () -> {
          int patients = count("Patient");
          int appointments = count("Appointment");
          int cases = count("NotifiableCase");
          DefaultTableModel consultations = UhsDao.consultations();
          TopDiagnosesReport report = new ReportDao().topDiagnoses(null, null, 1);
          DefaultTableModel top =
              UiComponents.model(
                  new String[] {"Diagnosis", "Visits"},
                  report.rows().stream()
                      .map(row -> new Object[] {row.diagnosis(), row.visits()})
                      .toArray(Object[][]::new));
          return new DashboardData(
              patients,
              consultations.getRowCount(),
              appointments,
              cases,
              consultations,
              top,
              report.rows());
        },
        this::render,
        this::showProblem);
  }

  private static int count(String table) throws Exception {
    DefaultTableModel model = UhsDao.query("SELECT COUNT(*) FROM " + table);
    return ((Number) model.getValueAt(0, 0)).intValue();
  }

  private void showTiles(String patients, String consultations, String appointments, String cases) {
    tiles.removeAll();
    tiles.add(UiComponents.statTile("Patients", patients, UiTheme.ACCENT));
    tiles.add(UiComponents.statTile("Consultations", consultations, UiTheme.SUCCESS));
    tiles.add(UiComponents.statTile("Appointments", appointments, UiTheme.WARNING));
    tiles.add(UiComponents.statTile("Notifiable cases", cases, UiTheme.DANGER));
    tiles.revalidate();
    tiles.repaint();
  }

  private void render(DashboardData data) {
    hint.setText("");
    showTiles(
        String.valueOf(data.patients()),
        String.valueOf(data.consultations()),
        String.valueOf(data.appointments()),
        String.valueOf(data.cases()));
    DefaultTableModel source = data.recentRows();
    Object[][] recentData = new Object[source.getRowCount()][3];
    for (int row = 0; row < source.getRowCount(); row++) {
      recentData[row] =
          new Object[] {
            String.valueOf(source.getValueAt(row, 1)).substring(0, 10),
            source.getValueAt(row, 2),
            source.getValueAt(row, 4)
          };
    }
    DefaultTableModel recentRows =
        UiComponents.model(new String[] {"Date", "Patient", "Diagnosis"}, recentData);
    recent.setModel(recentRows);
    recent.table().getColumnModel().getColumn(0).setPreferredWidth(100);
    recent.table().getColumnModel().getColumn(1).setPreferredWidth(140);
    recent.table().getColumnModel().getColumn(2).setPreferredWidth(140);
    diagnoses.setModel(data.topRows());
    diagnoses.table().getColumnModel().getColumn(0).setPreferredWidth(210);
    diagnoses.table().getColumnModel().getColumn(1).setPreferredWidth(80);
    chart.setRows(data.topDiagnoses());
  }

  private void showProblem(Exception error) {
    showTiles("—", "—", "—", "—");
    recent.showError(error);
    diagnoses.showError(error);
    chart.showError(error);
    hint.setForeground(UiTheme.WARNING);
    hint.setText(DbErrors.explain(error));
  }
}
