package com.uhs.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/** A titled table surface with a live row count. */
public final class TableCard extends JPanel {
  private static final long serialVersionUID = 1L;

  private final JTable table = UiComponents.styleTable(new JTable());
  private final JLabel count = UiComponents.muted("0 rows");
  private final JPanel views = new JPanel(new CardLayout());
  private final JLabel empty =
      UiComponents.muted("No data yet. Refresh or check the database connection.");

  public TableCard() {
    setLayout(new BorderLayout());
    setOpaque(false);
    JPanel head = new JPanel(new BorderLayout());
    head.setOpaque(false);
    head.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    head.add(count, BorderLayout.EAST);
    add(head, BorderLayout.NORTH);

    views.setOpaque(false);
    views.add(new JScrollPane(table), "table");
    JPanel emptyView = new JPanel(new BorderLayout());
    emptyView.setOpaque(false);
    empty.setHorizontalAlignment(SwingConstants.CENTER);
    emptyView.add(empty, BorderLayout.CENTER);
    views.add(emptyView, "empty");
    ((CardLayout) views.getLayout()).show(views, "empty");
    add(views, BorderLayout.CENTER);
  }

  /** Returns the underlying table for selection handling. */
  public JTable table() {
    return table;
  }

  /** Replaces the displayed rows and updates the row count. */
  public void setModel(DefaultTableModel model) {
    table.setModel(model);
    count.setText(model.getRowCount() + " rows");
    ((CardLayout) views.getLayout()).show(views, model.getRowCount() == 0 ? "empty" : "table");
  }
}
