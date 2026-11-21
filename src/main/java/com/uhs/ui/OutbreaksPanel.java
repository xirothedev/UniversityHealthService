package com.uhs.ui;

import com.uhs.dao.TopicQueries;
import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;

/** Outbreak desk: same disease in one building within fourteen days. */
public final class OutbreaksPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final TableCard clusters = new TableCard();
  private final TableCard cases = new TableCard();
  private final TableCard traces = new TableCard();

  public OutbreaksPanel(AppShell shell) {
    super("Outbreaks", "Notifiable cases and the contacts traced around them.");
    this.shell = shell;

    JButton refresh = UiComponents.ghostButton("Refresh");
    refresh.addActionListener(event -> load());
    addActions(refresh);

    JSplitPane lower =
        new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            UiComponents.card("Notifiable cases", "Disease, report date and building", cases),
            UiComponents.card("Contact traces", "Source patient to contact, with case", traces));
    lower.setResizeWeight(0.5);
    lower.setBorder(null);
    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card(
                "Fourteen-day clusters", "Same disease, same building (Q-T5)", clusters),
            lower);
    split.setResizeWeight(0.45);
    split.setBorder(null);
    body().add(split, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
    Background.run(
        () -> Tables.fromResult(TopicQueries.run(TopicQueries.OUTBREAK.sql())),
        clusters::setModel,
        shell::showError);
    Background.run(() -> UhsDao.browse("NotifiableCase"), cases::setModel, shell::showError);
    Background.run(() -> UhsDao.browse("ContactTrace"), traces::setModel, shell::showError);
  }
}
