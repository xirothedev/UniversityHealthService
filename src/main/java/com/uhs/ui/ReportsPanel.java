package com.uhs.ui;

import com.uhs.dao.ReportDao;
import com.uhs.model.DiagnosisCount;
import com.uhs.model.TopDiagnosesReport;
import com.uhs.util.Dates;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** Top-five diagnoses report with a period filter and a simple horizontal bar chart. */
public final class ReportsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final JTextField from = new JTextField(Dates.DATE_HINT, 12);
  private final JTextField to = new JTextField(Dates.DATE_HINT, 12);
  private final JSpinner minimum = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
  private final JLabel summary = UiComponents.muted("Run the report to see the period total.");
  private final TableCard table = new TableCard();
  private final BarChart chart = new BarChart();

  public ReportsPanel(AppShell shell) {
    super("Reports", "The five most frequent diagnoses, filtered by date.");
    this.shell = shell;

    JButton run = UiComponents.primaryButton("Run report");
    run.addActionListener(event -> runReport());

    JPanel controls = new JPanel();
    controls.setOpaque(false);
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    row.setOpaque(false);
    row.add(UiComponents.muted("From"));
    row.add(from);
    row.add(UiComponents.muted("To"));
    row.add(to);
    row.add(UiComponents.muted("Minimum visits"));
    row.add(minimum);
    row.add(run);
    controls.add(row);
    controls.add(summary);

    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card("Ranked diagnoses", "Count grouped by diagnosis", table),
            UiComponents.card("Share of the period", "Bar length scales with visit count", chart));
    split.setResizeWeight(0.5);
    split.setBorder(null);

    body()
        .add(
            UiComponents.card("Report period", "Leave a date blank for no bound", controls),
            BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    runReport();
  }

  private void runReport() {
    Background.run(
        () -> {
          LocalDate start =
              from.getText().isBlank() ? null : Dates.parseDate(from.getText(), "From date");
          LocalDate end = to.getText().isBlank() ? null : Dates.parseDate(to.getText(), "To date");
          if (start != null && end != null && start.isAfter(end)) {
            throw new com.uhs.util.ValidationException("From date must be on or before To date.");
          }
          return new ReportDao().topDiagnoses(start, end, (Integer) minimum.getValue());
        },
        this::render,
        shell::showError);
  }

  private void render(TopDiagnosesReport report) {
    Object[][] rows = new Object[report.rows().size()][4];
    int max = Math.max(1, report.totalConsultations());
    for (int index = 0; index < report.rows().size(); index++) {
      DiagnosisCount item = report.rows().get(index);
      rows[index] =
          new Object[] {
            index + 1,
            item.diagnosis(),
            item.visits(),
            String.format("%.1f %%", 100.0 * item.visits() / max)
          };
    }
    table.setModel(
        UiComponents.model(
            new String[] {"Rank", "Diagnosis", "Consultations", "Share of period"}, rows));
    summary.setText("Total consultations in period: " + report.totalConsultations());
    chart.setRows(report.rows());
  }
}
