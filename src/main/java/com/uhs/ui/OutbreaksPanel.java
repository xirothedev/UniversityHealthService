package com.uhs.ui;

import com.uhs.dao.TopicQueries;
import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;

/** Outbreak desk: same disease in one building within fourteen days. */
public final class OutbreaksPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final TableCard clusters = new TableCard();
  private final TableCard cases = new TableCard();
  private final TableCard traces = new TableCard();

  public OutbreaksPanel(AppShell shell) {
    super("Outbreaks", "Notifiable cases and the contacts traced around them.");
    clusters.setRetry(this::load);
    cases.setRetry(this::load);
    traces.setRetry(this::load);

    JButton refresh = UiComponents.ghostButton("Refresh");
    refresh.addActionListener(event -> load());
    addActions(refresh);

    JSplitPane lower =
        UiComponents.splitHorizontal(
            UiComponents.card("Notifiable cases", "Disease, report date and building", cases),
            UiComponents.card("Contact traces", "Source patient to contact, with case", traces),
            0.5);
    JSplitPane split =
        UiComponents.split(
            UiComponents.card(
                "Fourteen-day clusters", "Same disease in one building within 14 days", clusters),
            lower,
            0.45);
    body().add(split, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
    clusters.showLoading();
    cases.showLoading();
    traces.showLoading();
    Background.run(
        () -> Tables.fromResult(TopicQueries.run(TopicQueries.OUTBREAK.sql())),
        model -> {
          clusters.setModel(model);
          UiComponents.columns(
              clusters.table(),
              new String[] {"Disease", "Building", "Cases", "First report", "Last report"},
              new int[] {200, 180, 70, 120, 120});
        },
        clusters::showError);
    Background.run(
        () -> UhsDao.browse("NotifiableCase"),
        model -> {
          cases.setModel(model);
          UiComponents.columns(
              cases.table(),
              new String[] {"ID", "Consult", "Disease", "Reported", "Building"},
              new int[] {60, 80, 200, 120, 80});
        },
        cases::showError);
    Background.run(
        () -> UhsDao.browse("ContactTrace"),
        model -> {
          traces.setModel(model);
          UiComponents.columns(
              traces.table(),
              new String[] {"ID", "Source", "Contact", "Date", "Case"},
              new int[] {60, 80, 80, 120, 70});
        },
        traces::showError);
  }
}
