package com.uhs.ui;

import com.uhs.dao.TopicQueries;
import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;

/** Vaccination view: course records and patients who received every required dose. */
public final class VaccinationsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final TableCard complete = new TableCard();
  private final TableCard records = new TableCard();
  private final TableCard courses = new TableCard();

  public VaccinationsPanel(AppShell shell) {
    super("Vaccinations", "Courses, doses and completion across the student population.");
    this.shell = shell;

    JButton refresh = UiComponents.ghostButton("Refresh");
    refresh.addActionListener(event -> load());
    addActions(refresh);

    JSplitPane lower =
        new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            UiComponents.card("All dose records", "One row per dose given", records),
            UiComponents.card("Courses", "Required doses per course", courses));
    lower.setResizeWeight(0.6);
    lower.setBorder(null);
    JSplitPane split =
        new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            UiComponents.card(
                "Fully vaccinated", "Patients with every dose of a course (Q-T4)", complete),
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
        () -> Tables.fromResult(TopicQueries.run(TopicQueries.FULL_DOSE.sql())),
        complete::setModel,
        shell::showError);
    Background.run(() -> UhsDao.browse("VaccinationRecord"), records::setModel, shell::showError);
    Background.run(() -> UhsDao.browse("VaccinationCourse"), courses::setModel, shell::showError);
  }
}
