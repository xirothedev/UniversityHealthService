package com.uhs.ui;

import com.uhs.dao.TopicQueries;
import com.uhs.dao.UhsDao;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;

/** Vaccination view: course records and patients who received every required dose. */
public final class VaccinationsPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final TableCard complete = new TableCard();
  private final TableCard records = new TableCard();
  private final TableCard courses = new TableCard();

  public VaccinationsPanel(AppShell shell) {
    super("Vaccinations", "Courses, doses and completion across the student population.");
    complete.setRetry(this::load);
    records.setRetry(this::load);
    courses.setRetry(this::load);

    JButton refresh = UiComponents.ghostButton("Refresh");
    refresh.addActionListener(event -> load());
    addActions(refresh);

    JSplitPane lower =
        UiComponents.splitHorizontal(
            UiComponents.card("All dose records", "One row per dose given", records),
            UiComponents.card("Courses", "Required doses per course", courses),
            0.6);
    JSplitPane split =
        UiComponents.split(
            UiComponents.card("Fully vaccinated", "Patients with every dose of a course", complete),
            lower,
            0.45);
    body().add(split, BorderLayout.CENTER);
  }

  @Override
  protected void onShow() {
    load();
  }

  private void load() {
    complete.showLoading();
    records.showLoading();
    courses.showLoading();
    Background.run(
        () -> Tables.fromResult(TopicQueries.run(TopicQueries.FULL_DOSE.sql())),
        model -> {
          complete.setModel(model);
          UiComponents.columns(
              complete.table(),
              new String[] {"ID", "Patient", "Course", "Doses", "Completed"},
              new int[] {60, 160, 240, 70, 120});
        },
        complete::showError);
    Background.run(
        () -> UhsDao.browse("VaccinationRecord"),
        model -> {
          records.setModel(model);
          UiComponents.columns(
              records.table(),
              new String[] {"ID", "Patient", "Course", "Dose", "Given"},
              new int[] {60, 80, 80, 70, 120});
        },
        records::showError);
    Background.run(
        () -> UhsDao.browse("VaccinationCourse"),
        model -> {
          courses.setModel(model);
          UiComponents.columns(
              courses.table(),
              new String[] {"ID", "Course", "Disease", "Doses"},
              new int[] {60, 220, 180, 70});
        },
        courses::showError);
  }
}
