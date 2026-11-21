package com.uhs.ui;

import com.uhs.model.DiagnosisCount;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Small horizontal bar chart for diagnosis counts, shared by the dashboard and reports. */
public final class BarChart extends JPanel {
  private static final long serialVersionUID = 1L;

  private final JLabel empty = UiComponents.muted("No data for this period.");
  private List<DiagnosisCount> rows = List.of();

  public BarChart() {
    setLayout(new BorderLayout());
    setOpaque(false);
    empty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    add(empty, BorderLayout.CENTER);
    empty.setVisible(false);
  }

  /** Replaces the displayed values. */
  public void setRows(List<DiagnosisCount> values) {
    rows = values == null ? List.of() : values;
    empty.setVisible(rows.isEmpty());
    repaint();
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
      graphics.setColor(UiTheme.ACCENT_DARK);
      graphics.fillRoundRect(160, y, Math.max(4, barWidth), 20, 8, 8);
      graphics.setColor(UiTheme.TEXT);
      graphics.drawString(String.valueOf(row.visits()), 168 + Math.max(4, barWidth), y + 15);
      y += 34;
    }
  }
}
