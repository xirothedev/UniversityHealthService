package com.uhs.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * A content surface with four consistent states: content, loading, empty and error.
 *
 * <p>Screens switch states as a background task runs, so every table and chart behaves the same
 * way.
 */
public final class StatePanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private static final String CONTENT = "content";
  private static final String LOADING = "loading";
  private static final String EMPTY = "empty";
  private static final String ERROR = "error";

  private final JPanel views = new JPanel(new CardLayout());
  private final JLabel emptyMessage = UiComponents.muted("Nothing here yet.");
  private final JLabel errorMessage = UiComponents.muted("");
  private final JButton emptyAction = UiComponents.ghostButton("Refresh");
  private final JButton errorAction = UiComponents.ghostButton("Try again");

  public StatePanel(JComponent content) {
    setLayout(new BorderLayout());
    setOpaque(false);

    views.setOpaque(false);
    views.add(content, CONTENT);

    JProgressBar spinner = new JProgressBar();
    spinner.setIndeterminate(true);
    spinner.setPreferredSize(new Dimension(140, 6));
    views.add(centered(UiComponents.muted("Loading…"), spinner), LOADING);

    errorMessage.setForeground(UiTheme.DANGER);
    errorMessage.setHorizontalAlignment(SwingConstants.CENTER);

    views.add(centered(emptyMessage, emptyAction), EMPTY);
    views.add(centered(errorMessage, errorAction), ERROR);

    add(views, BorderLayout.CENTER);
    showContent();
  }

  /** Shows the wrapped component. */
  public void showContent() {
    show(CONTENT);
  }

  /** Shows an indeterminate progress indicator while data is loading. */
  public void showLoading() {
    show(LOADING);
  }

  /** Shows a quiet message with an optional retry action. */
  public void showEmpty(String message, Runnable retry) {
    emptyMessage.setText(message);
    configureAction(emptyAction, retry);
    show(EMPTY);
  }

  /** Shows a plain-language error with a retry action. */
  public void showError(String message, Runnable retry) {
    String safe = message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    errorMessage.setText("<html><div width='380' align='center'>" + safe + "</div></html>");
    configureAction(errorAction, retry);
    show(ERROR);
  }

  private static void configureAction(JButton button, Runnable retry) {
    button.setFont(button.getFont().deriveFont(Font.PLAIN, 12f));
    for (java.awt.event.ActionListener listener : button.getActionListeners()) {
      button.removeActionListener(listener);
    }
    if (retry == null) {
      button.setVisible(false);
    } else {
      button.setVisible(true);
      button.addActionListener(event -> retry.run());
    }
  }

  private static JPanel centered(JComponent... children) {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    JPanel stack = new JPanel();
    stack.setOpaque(false);
    stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
    for (JComponent child : children) {
      stack.add(child);
      stack.add(Box.createVerticalStrut(8));
    }
    wrapper.add(stack, BorderLayout.CENTER);
    return wrapper;
  }

  private void show(String name) {
    ((CardLayout) views.getLayout()).show(views, name);
  }
}
