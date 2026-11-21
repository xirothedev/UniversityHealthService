package com.uhs.ui;

import com.uhs.util.DbErrors;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** A titled table surface with loading, empty, error and data states. */
public final class TableCard extends JPanel {
  private static final long serialVersionUID = 1L;

  private static final String EMPTY_MESSAGE =
      "No data yet. Refresh or check the database connection.";

  private final JTable table = UiComponents.styleTable(new JTable());
  private final JLabel count = UiComponents.muted("0 rows");
  private final StatePanel views = new StatePanel(new JScrollPane(table));
  private Runnable retry;

  public TableCard() {
    setLayout(new BorderLayout());
    setOpaque(false);
    JPanel head = new JPanel(new BorderLayout());
    head.setOpaque(false);
    head.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    head.add(count, BorderLayout.EAST);
    add(head, BorderLayout.NORTH);
    add(views, BorderLayout.CENTER);
    views.showEmpty(EMPTY_MESSAGE, null);
  }

  /** Sets the action used by the empty and error states. */
  public void setRetry(Runnable action) {
    retry = action;
  }

  /** Returns the underlying table for selection handling. */
  public JTable table() {
    return table;
  }

  /** Shows the loading state while the query runs. */
  public void showLoading() {
    count.setText("");
    views.showLoading();
  }

  /** Shows an inline error with a retry action. */
  public void showError(Exception error) {
    count.setText("Error");
    views.showError(DbErrors.explain(error), retry);
  }

  /** Replaces the displayed rows and updates the row count. */
  public void setModel(DefaultTableModel model) {
    table.setModel(model);
    count.setText(model.getRowCount() + " rows");
    if (model.getRowCount() == 0) {
      views.showEmpty(EMPTY_MESSAGE, retry);
    } else {
      views.showContent();
    }
  }
}
