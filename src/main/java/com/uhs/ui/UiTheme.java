package com.uhs.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

/**
 * Astryx colour tokens, resolved for the dark mode of the core theme.
 *
 * <p>Each constant names the Astryx token it mirrors. Source: https://astryx.atmeta.com/docs/tokens
 */
public final class UiTheme {
  /** --color-background-body, dark value. */
  public static final Color CANVAS = new Color(0x1B1B1B);

  /** --color-background-surface, dark value. */
  public static final Color SURFACE = new Color(0x262626);

  /** --color-background-gray, dark value. */
  public static final Color SURFACE_RAISED = new Color(0x303030);

  /** --color-border, dark value (#ffffff1A: white at 10% opacity). */
  public static final Color BORDER = new Color(255, 255, 255, 26);

  /** --color-text-primary, dark value. */
  public static final Color TEXT = new Color(0xFFFFFF);

  /** --color-text-secondary, dark value. */
  public static final Color TEXT_MUTED = new Color(0x9E9E9E);

  /** --color-accent, dark value. */
  public static final Color ACCENT = new Color(0xF1F1F1);

  /** --color-accent-muted, dark value. */
  public static final Color ACCENT_DARK = new Color(0x474747);

  /** --color-on-accent, dark value. */
  public static final Color ON_ACCENT = new Color(0x111111);

  /** --color-success, dark value. */
  public static final Color SUCCESS = new Color(0x64AF4C);

  /** --color-warning. */
  public static final Color WARNING = new Color(0xFFCE2F);

  /** --color-error, dark value. */
  public static final Color DANGER = new Color(0xFF705D);

  /** --color-info, dark value. */
  public static final Color INFO = new Color(0x6D9CFE);

  /** --color-data-categorical-teal. */
  public static final Color DATA_TEAL = new Color(0x08A3A3);

  private UiTheme() {}

  /** Installs FlatLaf and maps Astryx tokens onto Swing UI defaults. */
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
    UIManager.put("Component.focusColor", DATA_TEAL);
    UIManager.put("Component.arc", 12);
  }

  /** Returns a bold derived font at the requested size. */
  public static Font bold(Font base, float size) {
    return base.deriveFont(Font.BOLD, size);
  }
}
