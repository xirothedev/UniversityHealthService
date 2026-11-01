package com.uhs.ui;

import com.uhs.dao.LookupDao;
import com.uhs.dao.TopicQueries;
import com.uhs.dao.TopicQuery;
import com.uhs.model.Option;
import com.uhs.model.ResultTable;
import com.uhs.util.Dates;
import com.uhs.util.DbErrors;
import com.uhs.util.ValidationException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** One screen for a topic query from the Queries menu: question, input, result grid, and SQL. */
public final class TopicQueryForm extends JFrame {
  private static final long serialVersionUID = 1L;

  private final transient TopicQuery query;
  private final JComboBox<Option> patientBox = new JComboBox<>();
  private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
  private final JButton runButton = new JButton("Run query");
  private final JTable table = new JTable();
  private final JLabel countLabel = new JLabel(" ");

  public TopicQueryForm(TopicQuery query) {
    super(query.code() + " - " + query.title());
    this.query = query;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    Tables.style(table);

    JTextArea question = new JTextArea(query.question());
    question.setEditable(false);
    question.setLineWrap(true);
    question.setWrapStyleWord(true);
    question.setOpaque(false);
    question.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

    JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
    switch (query.input()) {
      case PATIENT -> {
        inputs.add(new JLabel("Patient:"));
        inputs.add(patientBox);
      }
      case DATE -> {
        inputs.add(new JLabel("Date (" + Dates.DATE_HINT + "):"));
        inputs.add(dateField);
      }
      default -> {
        // no input needed
      }
    }
    inputs.add(runButton);
    runButton.addActionListener(e -> run());
    getRootPane().setDefaultButton(runButton);

    JPanel north = new JPanel(new BorderLayout());
    north.add(question, BorderLayout.NORTH);
    north.add(inputs, BorderLayout.SOUTH);

    JPanel results = new JPanel(new BorderLayout());
    results.add(new JScrollPane(table), BorderLayout.CENTER);
    JPanel countBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    countBar.add(countLabel);
    results.add(countBar, BorderLayout.SOUTH);

    JTextArea sql = new JTextArea(query.sql().strip(), 12, 80);
    sql.setEditable(false);
    sql.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
    JPanel sqlPanel = new JPanel(new BorderLayout());
    sqlPanel.setBorder(BorderFactory.createTitledBorder("SQL behind this screen"));
    sqlPanel.add(new JScrollPane(sql), BorderLayout.CENTER);

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, results, sqlPanel);
    split.setResizeWeight(0.6);
    getContentPane().add(north, BorderLayout.NORTH);
    getContentPane().add(split, BorderLayout.CENTER);
    setSize(900, 680);
    setLocationRelativeTo(null);

    if (query.input() == TopicQuery.Input.PATIENT) {
      runButton.setEnabled(false);
      Background.run(
          () -> new LookupDao().patients(),
          this::fillPatients,
          e -> Tables.error(this, DbErrors.explain(e)));
    } else if (query.input() == TopicQuery.Input.NONE) {
      run();
    }
  }

  private void fillPatients(List<Option> patients) {
    patientBox.setModel(new DefaultComboBoxModel<>(patients.toArray(new Option[0])));
    runButton.setEnabled(true);
    run();
  }

  private void run() {
    Object[] params;
    try {
      params = readParams();
    } catch (ValidationException e) {
      Tables.warn(this, e.getMessage());
      return;
    }
    runButton.setEnabled(false);
    countLabel.setText("Running...");
    Background.run(
        () -> TopicQueries.run(query.sql(), params),
        this::showResult,
        e -> {
          runButton.setEnabled(true);
          countLabel.setText(" ");
          Tables.error(this, DbErrors.explain(e));
        });
  }

  private Object[] readParams() throws ValidationException {
    switch (query.input()) {
      case PATIENT:
        Option chosen = (Option) patientBox.getSelectedItem();
        if (chosen == null || chosen.id() == null) {
          throw new ValidationException("Choose a patient.");
        }
        return new Object[] {chosen.id()};
      case DATE:
        LocalDate d = Dates.parseDate(dateField.getText(), "Date");
        return new Object[] {java.sql.Date.valueOf(d)};
      default:
        return new Object[0];
    }
  }

  private void showResult(ResultTable result) {
    runButton.setEnabled(true);
    table.setModel(Tables.fromResult(result));
    countLabel.setText(result.rows().size() + " row(s)");
  }
}
