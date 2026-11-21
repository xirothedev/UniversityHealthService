package com.uhs.ui;

import com.uhs.model.DiagnosisCount;
import com.uhs.util.DbErrors;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JPanel;

/** Small horizontal bar chart with loading, empty, error and data states. */
public final class BarChart extends JPanel {
  private static final long serialVersionUID = 1L;

  private final Bars bars = new Bars();
  private final StatePanel views = new StatePanel(bars);
  private Runnable retry;

  public BarChart() {
    setLayout(new BorderLayout());
    setOpaque(false);
    add(views, BorderLayout.CENTER);
    views.showEmpty("No data for this period.", null);
  }

  /** Sets the action used by the empty and error states. */
  public void setRetry(Runnable action) {
    retry = action;
  }

  /** Replaces the displayed values. */
  public void setRows(List<DiagnosisCount> values) {
    bars.rows = values == null ? List.of() : values;
    bars.repaint();
    if (bars.rows.isEmpty()) {
      views.showEmpty("No data for this period.", retry);
    } else {
      views.showContent();
    }
  }

  /** Shows the loading state while the query runs. */
  public void showLoading() {
    views.showLoading();
  }

  /** Shows an inline error with a retry action. */
  public void showError(Exception error) {
    views.showError(DbErrors.explain(error), retry);
  }

  private static final class Bars extends JPanel {
    private static final long serialVersionUID = 1L;

    private List<DiagnosisCount> rows = List.of();

    Bars() {
      setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      if (rows.isEmpty()) {
        return;
      }
      int peak = rows.stream().mapToInt(DiagnosisCount::visits).max().orElse(1);
      int y = 12;
      for (DiagnosisCount row : rows) {
        graphics.setColor(UiTheme.TEXT_MUTED);
        graphics.drawString(row.diagnosis(), 16, y + 12);
        int barWidth = (int) ((getWidth() - 220) * row.visits() / Math.max(1, peak));
        graphics.setColor(UiTheme.DATA_TEAL);
        graphics.fillRoundRect(160, y, Math.max(4, barWidth), 20, 8, 8);
        graphics.setColor(UiTheme.TEXT);
        graphics.drawString(String.valueOf(row.visits()), 168 + Math.max(4, barWidth), y + 15);
        y += 34;
      }
    }
  }
}
