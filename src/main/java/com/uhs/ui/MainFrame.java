package com.uhs.ui;

import com.uhs.dao.TopicQueries;
import com.uhs.dao.TopicQuery;
import com.uhs.util.DBConnection;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Properties;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Main window of the University Health Service application. The menu is the single place that opens
 * every screen: forms F0 to F5 and queries Q-T1 to Q-T5.
 */
public final class MainFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private JFrame f0;
  private JFrame f1;
  private JFrame f2;
  private JFrame f3;
  private JFrame f4;
  private JFrame f5;

  public MainFrame(String connectionInfo) {
    super("University Health Service");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setJMenuBar(buildMenuBar());

    JLabel title = new JLabel("University Health Service", SwingConstants.CENTER);
    title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
    JLabel hint =
        new JLabel("Use the Forms and Queries menus to open a screen.", SwingConstants.CENTER);
    JPanel center = new JPanel(new BorderLayout());
    center.add(title, BorderLayout.CENTER);
    center.add(hint, BorderLayout.SOUTH);
    center.setBorder(BorderFactory.createEmptyBorder(80, 20, 80, 20));

    JLabel status = new JLabel("Database: " + connectionInfo);
    status.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    getContentPane().add(center, BorderLayout.CENTER);
    getContentPane().add(status, BorderLayout.SOUTH);
    setSize(760, 420);
    setLocationRelativeTo(null);
  }

  /** Builds the connection text shown in the status bar (no password). */
  public static String describeConnection(Properties p) {
    return DBConnection.buildUrl(p) + "  user=" + p.getProperty("db.user");
  }

  private JMenuBar buildMenuBar() {
    JMenuBar bar = new JMenuBar();

    JMenu file = new JMenu("File");
    JMenuItem exit = new JMenuItem("Exit");
    exit.addActionListener(e -> dispose());
    file.add(exit);

    bar.add(file);
    bar.add(buildFormsMenu());
    bar.add(buildQueriesMenu());
    return bar;
  }

  private JMenu buildFormsMenu() {
    JMenu forms = new JMenu("Forms");
    forms.add(item("F0 - Database connection", () -> f0 = show(f0, F0Connection::new)));
    forms.add(item("F1 - Browse tables", () -> f1 = show(f1, F1Browse::new)));
    forms.add(item("F2 - Search history and free slots", () -> f2 = show(f2, F2Search::new)));
    forms.add(
        item("F3 - Consultation and prescription lines", () -> f3 = show(f3, F3MasterDetail::new)));
    forms.add(item("F4 - Edit consultations", () -> f4 = show(f4, ConsultationForm::new)));
    forms.add(item("F5 - Report: Top-5 diagnoses", () -> f5 = show(f5, Top5ReportForm::new)));
    return forms;
  }

  private JMenu buildQueriesMenu() {
    JMenu queries = new JMenu("Queries");
    queries.add(queryItem(TopicQueries.HISTORY));
    queries.add(queryItem(TopicQueries.FREE_SLOTS));
    // Q-T3 is the Top-5 diagnoses aggregate, which is form F5.
    queries.add(item("Q-T3  Top-5 diagnoses (form F5)", () -> f5 = show(f5, Top5ReportForm::new)));
    queries.add(queryItem(TopicQueries.FULL_DOSE));
    queries.add(queryItem(TopicQueries.OUTBREAK));
    return queries;
  }

  private JMenuItem queryItem(TopicQuery q) {
    return item(q.code() + "  " + q.title(), () -> new TopicQueryForm(q).setVisible(true));
  }

  private static JMenuItem item(String text, Runnable action) {
    JMenuItem mi = new JMenuItem(text);
    mi.addActionListener(e -> action.run());
    return mi;
  }

  /** Opens the window, or brings the existing one to the front. */
  private static JFrame show(JFrame existing, Supplier<JFrame> factory) {
    JFrame frame = existing;
    if (frame == null || !frame.isDisplayable()) {
      frame = factory.get();
      frame.setVisible(true);
    } else {
      frame.toFront();
    }
    return frame;
  }
}
