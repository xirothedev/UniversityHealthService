package com.uhs.ui;

import com.uhs.dao.LookupDao;
import com.uhs.dao.TopicQueries;
import com.uhs.dao.TopicQuery;
import com.uhs.model.Option;
import com.uhs.util.Dates;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Runs the four topic queries from one screen and shows the SQL behind each result. */
public final class QueriesPanel extends ScreenPanel {
  private static final long serialVersionUID = 1L;

  private final AppShell shell;
  private final JComboBox<TopicQuery> queries = new JComboBox<>();
  private final JComboBox<Option> patients = new JComboBox<>();
  private final JTextField date = new JTextField(Dates.formatDate(java.time.LocalDate.now()), 12);
  private final JPanel inputs = new JPanel(new CardLayout());
  private final JLabel question = UiComponents.muted("Select a query.");
  private final TableCard results = new TableCard();
  private final JTextArea sql = new JTextArea();

  public QueriesPanel(AppShell shell) {
    super(
        "Topic queries",
        "The required database questions, with their SQL shown beside the result.");
    this.shell = shell;
    results.setRetry(this::runSelected);
    sql.setEditable(false);
    sql.setLineWrap(true);
    sql.setWrapStyleWord(true);

    JPanel none = new JPanel();
    none.setOpaque(false);
    none.add(UiComponents.muted("No input needed."));
    JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    patientPanel.setOpaque(false);
    patientPanel.add(UiComponents.muted("Patient"));
    patientPanel.add(patients);
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    datePanel.setOpaque(false);
    datePanel.add(UiComponents.muted("Date"));
    datePanel.add(date);
    inputs.add(none, "NONE");
    inputs.add(patientPanel, "PATIENT");
    inputs.add(datePanel, "DATE");

    JButton run = UiComponents.primaryButton("Run query");
    run.addActionListener(event -> runSelected());
    JButton topFive = UiComponents.ghostButton("Top-5 diagnoses report");
    topFive.addActionListener(event -> shell.navigate("reports"));

    JPanel controls = new JPanel();
    controls.setOpaque(false);
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
    controls.add(
        UiComponents.controlBar(
            UiComponents.field("Question", queries),
            UiComponents.field("Action", run),
            UiComponents.field("Aggregate query", topFive)));
    controls.add(javax.swing.Box.createVerticalStrut(12));
    controls.add(question);
    controls.add(javax.swing.Box.createVerticalStrut(10));
    controls.add(inputs);

    sql.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
    sql.setBackground(UiTheme.SURFACE_RAISED);
    sql.setForeground(UiTheme.TEXT);

    JSplitPane split =
        UiComponents.split(
            UiComponents.card("Result", "Rows returned by the query", results),
            UiComponents.card(
                "SQL behind this screen", "The exact statement that ran", new JScrollPane(sql)),
            0.6);

    body().add(UiComponents.card("Choose a question", null, controls), BorderLayout.NORTH);
    body().add(split, BorderLayout.CENTER);

    for (TopicQuery query :
        new TopicQuery[] {
          TopicQueries.HISTORY,
          TopicQueries.FREE_SLOTS,
          TopicQueries.FULL_DOSE,
          TopicQueries.OUTBREAK
        }) {
      queries.addItem(query);
    }
    queries.addActionListener(event -> showQuery());
    showQuery();
  }

  @Override
  protected void onShow() {
    patients.setEnabled(false);
    Background.run(
        () -> new LookupDao().patients(),
        loaded -> {
          patients.removeAllItems();
          patients.addItem(Option.none("Select a patient"));
          for (Option option : loaded) {
            patients.addItem(option);
          }
          patients.setEnabled(true);
        },
        error -> {
          patients.setEnabled(true);
          shell.showError(error);
        });
  }

  private void showQuery() {
    TopicQuery query = (TopicQuery) queries.getSelectedItem();
    if (query == null) {
      return;
    }
    question.setText(query.code() + "  ·  " + query.question());
    sql.setText(query.sql().strip());
    ((CardLayout) inputs.getLayout()).show(inputs, query.input().name());
  }

  private void runSelected() {
    TopicQuery query = (TopicQuery) queries.getSelectedItem();
    if (query == null) {
      return;
    }
    results.showLoading();
    Background.run(
        () -> Tables.fromResult(TopicQueries.run(query.sql(), parameters(query))),
        results::setModel,
        results::showError);
  }

  private Object[] parameters(TopicQuery query) {
    return switch (query.input()) {
      case NONE -> new Object[0];
      case PATIENT -> {
        Option option = (Option) patients.getSelectedItem();
        if (option == null || option.id() == null) {
          shell.showMessage("Choose a patient first.");
          yield new Object[] {-1};
        }
        yield new Object[] {option.id()};
      }
      case DATE -> {
        try {
          yield new Object[] {java.sql.Date.valueOf(Dates.parseDate(date.getText(), "Date"))};
        } catch (Exception error) {
          shell.showError(error);
          yield new Object[0];
        }
      }
    };
  }
}
