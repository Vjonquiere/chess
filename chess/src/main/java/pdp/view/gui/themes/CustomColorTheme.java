package pdp.view.gui.themes;

/** Class representing custom themes */
public class CustomColorTheme implements ColorThemeInterface {
  private final String name;
  private final String primary;
  private final String secondary;
  private final String tertiary;
  private final String accent;
  private final String background;
  private final String background2;
  private final String text;
  private final String textInverted;

  public CustomColorTheme(
      String name,
      String primary,
      String secondary,
      String tertiary,
      String accent,
      String background,
      String background2,
      String text,
      String textInverted) {
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

  public String getName() {
    return name;
  }

  public String getPrimary() {
    return primary;
  }

  public String getSecondary() {
    return secondary;
  }

  public String getTertiary() {
    return tertiary;
  }

  public String getAccent() {
    return accent;
  }

  public String getBackground() {
    return background;
  }

  public String getBackground2() {
    return background2;
  }

  public String getText() {
    return text;
  }

  public String getTextInverted() {
    return textInverted;
  }
}
