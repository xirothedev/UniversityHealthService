package com.uhs.ui;

import com.uhs.dao.ReportDao;
import com.uhs.model.DiagnosisCount;
import com.uhs.model.TopDiagnosesReport;
import com.uhs.util.Dates;
import com.uhs.util.DbErrors;
import com.uhs.util.ValidationException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/** Form F5: aggregate report. The five most frequent diagnoses, optionally within a period. */
public final class Top5ReportForm extends JFrame {
  private static final long serialVersionUID = 1L;

  private final transient ReportDao dao = new ReportDao();
  private final JTextField fromField = new JTextField(10);
  private final JTextField toField = new JTextField(10);
  private final JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
  private final JButton runButton = new JButton("Run report");
  private final DefaultTableModel model =
      Tables.readOnlyModel("Rank", "Diagnosis", "Consultations", "Share of period");
  private final JTable table = new JTable(model);
  private final BarChart chart = new BarChart();
  private final JLabel summaryLabel = new JLabel(" ");

  public Top5ReportForm() {
    super("F5 - Report: Top-5 diagnoses");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    Tables.style(table);
    table.setAutoCreateRowSorter(false);

    JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
    filters.add(new JLabel("From (" + Dates.DATE_HINT + "):"));
    filters.add(fromField);
    filters.add(new JLabel("To:"));
    filters.add(toField);
    filters.add(new JLabel("Minimum consultations:"));
    filters.add(minSpinner);
    filters.add(runButton);
    runButton.addActionListener(e -> run());
    getRootPane().setDefaultButton(runButton);

    JPanel middle = new JPanel(new BorderLayout());
    middle.add(new JScrollPane(table), BorderLayout.CENTER);
    middle.add(summaryLabel, BorderLayout.SOUTH);
    middle.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));

    JSplitPane tableAndChart = new JSplitPane(JSplitPane.VERTICAL_SPLIT, middle, chart);
    tableAndChart.setResizeWeight(0.4);

    JTextArea sql = new JTextArea(ReportDao.TOP5_SQL, 9, 60);
    sql.setEditable(false);
    sql.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
    JPanel sqlPanel = new JPanel(new BorderLayout());
    sqlPanel.setBorder(BorderFactory.createTitledBorder("SQL behind this screen"));
    sqlPanel.add(new JScrollPane(sql), BorderLayout.CENTER);

    getContentPane().add(filters, BorderLayout.NORTH);
    getContentPane().add(tableAndChart, BorderLayout.CENTER);
    getContentPane().add(sqlPanel, BorderLayout.SOUTH);
    setSize(820, 720);
    setLocationRelativeTo(null);
    run();
  }

  private void run() {
    LocalDate from;
    LocalDate to;
    try {
      from = optionalDate(fromField.getText(), "From date");
      to = optionalDate(toField.getText(), "To date");
      if (from != null && to != null && from.isAfter(to)) {
        throw new ValidationException("The From date must not be after the To date.");
      }
    } catch (ValidationException e) {
      Tables.warn(this, e.getMessage());
      return;
    }
    int min = (Integer) minSpinner.getValue();
    runButton.setEnabled(false);
    summaryLabel.setText("Running...");
    Background.run(
        () -> dao.topDiagnoses(from, to, min),
        this::show,
        e -> {
          runButton.setEnabled(true);
          summaryLabel.setText(" ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  private static LocalDate optionalDate(String text, String field) throws ValidationException {
    if (text == null || text.isBlank()) {
      return null;
    }
    return Dates.parseDate(text, field);
  }

  private void show(TopDiagnosesReport report) {
    runButton.setEnabled(true);
    model.setRowCount(0);
    int rank = 1;
    int total = report.totalConsultations();
    for (DiagnosisCount d : report.rows()) {
      double share = total == 0 ? 0 : 100.0 * d.visits() / total;
      model.addRow(
          new Object[] {rank++, d.diagnosis(), d.visits(), String.format("%.1f %%", share)});
    }
    summaryLabel.setText(
        report.rows().size()
            + " diagnosis(es) listed, out of "
            + total
            + " consultation(s) in the period. Ties are listed alphabetically.");
    chart.setData(report.rows());
  }

  /** Horizontal bar chart drawn with Java2D, so no extra library is needed. */
  private static final class BarChart extends JPanel {
    private static final long serialVersionUID = 1L;
    private transient List<DiagnosisCount> data = List.of();

    BarChart() {
      setPreferredSize(new Dimension(600, 220));
      setBorder(BorderFactory.createTitledBorder("Consultations per diagnosis"));
    }

    void setData(List<DiagnosisCount> rows) {
      data = rows;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      FontMetrics fm = g2.getFontMetrics();
      int top = 28;
      int left = 12;
      int width = getWidth() - 2 * left;
      if (data.isEmpty()) {
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("No data for this period.", left, top + fm.getAscent());
        g2.dispose();
        return;
      }
      int labelWidth = 0;
      for (DiagnosisCount d : data) {
        labelWidth = Math.max(labelWidth, fm.stringWidth(d.diagnosis()));
      }
      labelWidth = Math.min(labelWidth, width / 3);
      int max = data.get(0).visits();
      int barAreaX = left + labelWidth + 10;
      int barAreaWidth = Math.max(40, width - labelWidth - 60);
      int rowHeight = Math.max(24, (getHeight() - top - 8) / Math.max(1, data.size()));
      int y = top;
      for (DiagnosisCount d : data) {
        int barWidth = Math.max(2, (int) ((double) d.visits() / max * barAreaWidth));
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString(
            clip(d.diagnosis(), fm, labelWidth), left, y + rowHeight / 2 + fm.getAscent() / 2 - 2);
        g2.setColor(new Color(0x2F7DD1));
        g2.fillRoundRect(barAreaX, y + 4, barWidth, rowHeight - 10, 6, 6);
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString(
            String.valueOf(d.visits()),
            barAreaX + barWidth + 6,
            y + rowHeight / 2 + fm.getAscent() / 2 - 2);
        y += rowHeight;
      }
      g2.dispose();
    }

    private static String clip(String text, FontMetrics fm, int maxWidth) {
      if (fm.stringWidth(text) <= maxWidth) {
        return text;
      }
      String t = text;
      while (t.length() > 1 && fm.stringWidth(t + "...") > maxWidth) {
        t = t.substring(0, t.length() - 1);
      }
      return t + "...";
    }
  }
}
