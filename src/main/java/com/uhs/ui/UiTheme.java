package com.uhs.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

/** Dark visual tokens shared by every screen of the University Health Service shell. */
public final class UiTheme {
  public static final Color CANVAS = new Color(11, 18, 32);
  public static final Color SURFACE = new Color(18, 28, 46);
  public static final Color SURFACE_RAISED = new Color(26, 39, 64);
  public static final Color BORDER = new Color(36, 53, 79);
  public static final Color TEXT = new Color(230, 237, 247);
  public static final Color TEXT_MUTED = new Color(148, 168, 196);
  public static final Color ACCENT = new Color(45, 212, 191);
  public static final Color ACCENT_DARK = new Color(14, 124, 107);
  public static final Color SUCCESS = new Color(52, 211, 153);
  public static final Color WARNING = new Color(251, 191, 36);
  public static final Color DANGER = new Color(248, 113, 113);

  private UiTheme() {}

  /** Installs the dark look and feel and the application colour tokens. */
  public static void install() {
    FlatDarkLaf.setup();
    UIManager.put("Panel.background", CANVAS);
    UIManager.put("OptionPane.background", SURFACE);
    UIManager.put("OptionPane.messageForeground", TEXT);
    UIManager.put("Table.background", SURFACE);
    UIManager.put("Table.foreground", TEXT);
    UIManager.put("Table.selectionBackground", ACCENT_DARK);
    UIManager.put("Table.selectionForeground", TEXT);
    UIManager.put("Table.gridColor", BORDER);
    UIManager.put("TableHeader.background", SURFACE_RAISED);
    UIManager.put("TableHeader.foreground", TEXT_MUTED);
    UIManager.put("ScrollPane.background", SURFACE);
    UIManager.put("Viewport.background", SURFACE);
    UIManager.put("TextField.background", SURFACE_RAISED);
    UIManager.put("TextField.foreground", TEXT);
    UIManager.put("ComboBox.background", SURFACE_RAISED);
    UIManager.put("ComboBox.foreground", TEXT);
    UIManager.put("Button.background", SURFACE_RAISED);
    UIManager.put("Button.foreground", TEXT);
    UIManager.put("Label.foreground", TEXT);
    UIManager.put("TabbedPane.background", SURFACE);
    UIManager.put("TabbedPane.foreground", TEXT);
    UIManager.put("Component.borderColor", BORDER);
    UIManager.put("Component.focusColor", ACCENT);
    UIManager.put("Component.arc", 10);
  }

  /** Returns a bold derived font at the requested size. */
  public static Font bold(Font base, float size) {
    return base.deriveFont(Font.BOLD, size);
  }
}
