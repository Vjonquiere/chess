package pdp.view.gui.themes;

/** Class representing custom themes. */
public class CustomColorTheme implements ColorThemeInterface {
  /** Name of the color theme. */
  private final String name;

  /** String corresponding to the primary color, in hexadecimal format. */
  private final String primary;

  /** String corresponding to the secondary color, in hexadecimal format. */
  private final String secondary;

  /** String corresponding to the tertiary color, in hexadecimal format. */
  private final String tertiary;

  /** String corresponding to the accent color, in hexadecimal format. */
  private final String accent;

  /** String corresponding to the background color, in hexadecimal format. */
  private final String background;

  /** String corresponding to the second background color, in hexadecimal format. */
  private final String background2;

  /** String corresponding to the text color, in hexadecimal format. */
  private final String text;

  /** String corresponding to the second text color, in hexadecimal format. */
  private final String textInverted;

  /**
   * Creates a custom color theme.
   *
   * @param name name of the theme
   * @param primary primary color (in string format)
   * @param secondary secondary color (in string format)
   * @param tertiary tertiary color (in string format)
   * @param accent accent color (in string format)
   * @param background primary background color (in string format)
   * @param background2 secondary background color (in string format)
   * @param text primary text color (in string format)
   * @param textInverted secondary text color (in string format)
   */
  public CustomColorTheme(
      final String name,
      final String primary,
      final String secondary,
      final String tertiary,
      final String accent,
      final String background,
      final String background2,
      final String text,
      final String textInverted) {
    this.name = name;
    this.primary = primary;
    this.secondary = secondary;
    this.tertiary = tertiary;
    this.accent = accent;
    this.background = background;
    this.background2 = background2;
    this.text = text;
    this.textInverted = textInverted;
  }

  /**
   * Retrieves the name of the theme.
   *
   * @return name of the theme
   */
  public String getName() {
    return name;
  }

  @Override
  public String getPrimary() {
    return primary;
  }

  @Override
  public String getSecondary() {
    return secondary;
  }

  @Override
  public String getTertiary() {
    return tertiary;
  }

  @Override
  public String getAccent() {
    return accent;
  }

  @Override
  public String getBackground() {
    return background;
  }

  @Override
  public String getBackground2() {
    return background2;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getTextInverted() {
    return textInverted;
  }
}
