package com.uhs.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

/** Small reusable widgets that give every screen the same visual rhythm. */
public final class UiComponents {
  private UiComponents() {}

  /** A raised surface with rounded corners and a subtle outline. */
  public static JPanel card(JComponent content) {
    JPanel card = new CardPanel();
    card.setLayout(new BorderLayout());
    card.add(content, BorderLayout.CENTER);
    return card;
  }

  /** A card with a title, a muted subtitle and a content area below them. */
  public static JPanel card(String title, String subtitle, JComponent content) {
    JPanel head = new JPanel();
    head.setOpaque(false);
    head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
    head.add(label(title, 16));
    if (subtitle != null && !subtitle.isBlank()) {
      head.add(Box.createVerticalStrut(4));
      head.add(muted(subtitle));
    }
    JPanel body = new JPanel(new BorderLayout());
    body.setOpaque(false);
    body.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
    body.add(content, BorderLayout.CENTER);
    JPanel all = new JPanel(new BorderLayout());
    all.setOpaque(false);
    all.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
    all.add(head, BorderLayout.NORTH);
    all.add(body, BorderLayout.CENTER);
    return card(all);
  }

  /** A large value with a small caption, used on the dashboard. */
  public static JPanel statTile(String caption, String value, java.awt.Color accent) {
    JPanel tile = new JPanel();
    tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
    tile.setOpaque(false);
    JLabel captionLabel = muted(caption.toUpperCase());
    JLabel valueLabel = label(value, 28);
    valueLabel.setForeground(accent);
    tile.add(captionLabel);
    tile.add(Box.createVerticalStrut(8));
    tile.add(valueLabel);
    return card(tile);
  }

  /** A bold label at the requested size. */
  public static JLabel label(String text, int size) {
    JLabel result = new JLabel(text);
    Font font = result.getFont().deriveFont(Font.BOLD, (float) size);
    result.setFont(font);
    result.setForeground(UiTheme.TEXT);
    return result;
  }

  /** A small, low-contrast label used for hints and captions. */
  public static JLabel muted(String text) {
    JLabel result = new JLabel(text);
    result.setForeground(UiTheme.TEXT_MUTED);
    return result;
  }

  /** A date field with an empty value and a format hint shown as a placeholder. */
  public static JTextField dateField(String placeholder) {
    JTextField field = new JTextField(12);
    field.putClientProperty("JTextField.placeholderText", placeholder);
    field.setToolTipText("Format: " + placeholder);
    return field;
  }

  /** A filled button for the main action on a screen. */
  public static JButton primaryButton(String text) {
    JButton button = new JButton(text);
    button.setBackground(UiTheme.ACCENT);
    button.setForeground(UiTheme.ON_ACCENT);
    button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
    return button;
  }

  /** A quiet button for secondary actions. */
  public static JButton ghostButton(String text) {
    JButton button = new JButton(text);
    button.setBackground(UiTheme.SURFACE_RAISED);
    button.setForeground(UiTheme.TEXT);
    button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
    return button;
  }

  /** Returns the rows of a model whose named column equals the given value. */
  public static DefaultTableModel filter(DefaultTableModel source, String column, Object value) {
    return subset(
        source,
        row -> java.util.Objects.equals(source.getValueAt(row, source.findColumn(column)), value));
  }

  private static DefaultTableModel subset(
      DefaultTableModel source, java.util.function.IntPredicate keep) {
    java.util.List<Object[]> rows = new java.util.ArrayList<>();
    for (int row = 0; row < source.getRowCount(); row++) {
      if (keep.test(row)) {
        Object[] copy = new Object[source.getColumnCount()];
        for (int col = 0; col < copy.length; col++) {
          copy[col] = source.getValueAt(row, col);
        }
        rows.add(copy);
      }
    }
    String[] columns = new String[source.getColumnCount()];
    for (int col = 0; col < columns.length; col++) {
      columns[col] = source.getColumnName(col);
    }
    return model(columns, rows.toArray(new Object[0][]));
  }

  /** A small rounded icon with a single letter, used by the navigation rail. */
  public static javax.swing.Icon glyphIcon(String letter, java.awt.Color color) {
    return new javax.swing.Icon() {
      @Override
      public void paintIcon(Component c, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        g.fillRoundRect(x, y, 24, 24, 8, 8);
        g.setColor(color);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
        java.awt.FontMetrics fm = g.getFontMetrics();
        g.drawString(letter, x + (24 - fm.stringWidth(letter)) / 2, y + 17);
        g.dispose();
      }

      @Override
      public int getIconWidth() {
        return 24;
      }

      @Override
      public int getIconHeight() {
        return 24;
      }
    };
  }

  /** Applies the shared table look to a JTable. */
  public static JTable styleTable(JTable table) {
    table.setRowHeight(30);
    table.setShowGrid(true);
    table.setGridColor(UiTheme.BORDER);
    table.setIntercellSpacing(new java.awt.Dimension(1, 1));
    table.setFont(table.getFont().deriveFont(13f));
    table.setFillsViewportHeight(true);
    table.getTableHeader().setReorderingAllowed(false);
    return table;
  }

  /** Builds a read-only table model from column names and rows. */
  public static DefaultTableModel model(String[] columns, Object[][] rows) {
    return new DefaultTableModel(rows, columns) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  private static final class CardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    CardPanel() {
      setOpaque(false);
      setBorder(new RoundedBorder());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(UiTheme.SURFACE);
      g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
      g.dispose();
      super.paintComponent(graphics);
    }
  }

  private static final class RoundedBorder implements Border {
    @Override
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(UiTheme.BORDER);
      g.drawRoundRect(x, y, width - 1, height - 1, 16, 16);
      g.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(18, 18, 18, 18);
    }

    @Override
    public boolean isBorderOpaque() {
      return false;
    }
  }
}
