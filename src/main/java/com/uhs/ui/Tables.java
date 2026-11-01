package com.uhs.ui;

import com.uhs.model.ResultTable;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Small helpers shared by the forms. */
final class Tables {
  private Tables() {}

  /** A table model whose cells cannot be edited by typing in the grid. */
  static DefaultTableModel readOnlyModel(String... columns) {
    return new DefaultTableModel(columns, 0) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  static DefaultTableModel fromResult(ResultTable result) {
    Vector<String> cols = new Vector<>(result.columns());
    DefaultTableModel model =
        new DefaultTableModel(cols, 0) {
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    for (Object[] row : result.rows()) {
      model.addRow(row);
    }
    return model;
  }

  static void style(JTable table) {
    table.setAutoCreateRowSorter(true);
    table.setFillsViewportHeight(true);
    table.setRowHeight(table.getRowHeight() + 4);
    table.getTableHeader().setReorderingAllowed(false);
  }

  static void warn(Component parent, String message) {
    JOptionPane.showMessageDialog(
        parent, message, "Please check the input", JOptionPane.WARNING_MESSAGE);
  }

  static void error(Component parent, String message) {
    JOptionPane.showMessageDialog(
        parent, message, "Cannot complete the action", JOptionPane.ERROR_MESSAGE);
  }
}
