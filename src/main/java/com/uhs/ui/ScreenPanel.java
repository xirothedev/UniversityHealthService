package com.uhs.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/** Base class for every screen: a title band over a padded body area. */
public abstract class ScreenPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private final JPanel body = new JPanel(new BorderLayout());
  private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
  private boolean loaded;

  protected ScreenPanel(String title, String subtitle) {
    setLayout(new BorderLayout());
    setBackground(UiTheme.CANVAS);

    JPanel heading = new JPanel();
    heading.setOpaque(false);
    heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
    heading.add(UiComponents.label(title, 24));
    heading.add(javax.swing.Box.createVerticalStrut(6));
    heading.add(UiComponents.muted(subtitle));

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);
    top.setBorder(BorderFactory.createEmptyBorder(28, 32, 22, 32));
    top.add(heading, BorderLayout.WEST);
    actions.setOpaque(false);
    top.add(actions, BorderLayout.EAST);

    body.setOpaque(false);
    body.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));

    add(top, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
  }

  /** Loads this screen's data the first time the user opens it. */
  public final void ensureLoaded() {
    if (!loaded) {
      loaded = true;
      onShow();
    }
  }

  /** Hook for a screen to load its data. Called once, before the screen is first shown. */
  protected void onShow() {}

  /** Returns the padded area where a screen places its cards. */
  protected JPanel body() {
    return body;
  }

  /** Adds buttons to the right side of the title band. */
  protected void addActions(Component... buttons) {
    for (Component button : buttons) {
      actions.add(button);
    }
  }
}
