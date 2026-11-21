package com.uhs.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/** A titled table surface with a live row count. */
public final class TableCard extends JPanel {
  private static final long serialVersionUID = 1L;

  private final JTable table = UiComponents.styleTable(new JTable());
  private final JLabel count = UiComponents.muted("0 rows");
  private final JLabel empty =
      UiComponents.muted("No data yet. Refresh or check the database connection.");

  public TableCard() {
    setLayout(new BorderLayout());
    setOpaque(false);
    JPanel head = new JPanel(new BorderLayout());
    head.setOpaque(false);
    head.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    head.add(UiComponents.label("Results", 14), BorderLayout.WEST);
    head.add(count, BorderLayout.EAST);
    add(head, BorderLayout.NORTH);

    JPanel stack = new JPanel();
    stack.setLayout(new OverlayLayout(stack));
    stack.setOpaque(false);
    stack.add(new JScrollPane(table));
    empty.setHorizontalAlignment(SwingConstants.CENTER);
    empty.setVisible(false);
    stack.add(empty);
    add(stack, BorderLayout.CENTER);
  }

  /** Returns the underlying table for selection handling. */
  public JTable table() {
    return table;
  }

  /** Replaces the displayed rows and updates the row count. */
  public void setModel(DefaultTableModel model) {
    table.setModel(model);
    count.setText(model.getRowCount() + " rows");
    empty.setVisible(model.getRowCount() == 0);
  }
}
