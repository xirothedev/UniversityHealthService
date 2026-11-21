package com.uhs.ui;

import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/** Generic browse form: any table from the schema, with a live row count. */
public final class BrowsePanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final JComboBox<String> tables = new JComboBox<>();
  private final TableCard grid = new TableCard();

  public BrowsePanel() {
    super("Browse", "Load any table from the schema into a grid.");
    for (String table : UhsDao.TABLES) {
      tables.addItem(table);
    }
    grid.setRetry(this::load);

    JButton load = UiComponents.primaryButton("Load table");
    load.addActionListener(event -> load());
    tables.addActionListener(event -> load());
    JPanel bar =
        UiComponents.controlBar(
            UiComponents.field("Table", tables), UiComponents.field("Action", load));

    body().add(UiComponents.card("Choose a table", null, bar), BorderLayout.NORTH);
    body()
        .add(
            UiComponents.card("Rows", "Every row in the selected table", grid),
            BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
    String table = (String) tables.getSelectedItem();
    if (table == null) {
      return;
    }
    grid.showLoading();
    Background.run(() -> UhsDao.browse(table), grid::setModel, grid::showError);
  }
}
