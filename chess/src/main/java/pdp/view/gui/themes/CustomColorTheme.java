package pdp.view.gui.themes;

import java.util.regex.Pattern;

/** Class representing custom themes */
public class CustomColorTheme implements ColorThemeInterface {

  private static final Pattern HEX_PATTERN = Pattern.compile("^#[A-Fa-f0-9]{6}$");

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

    if (!isValidHex(primary)
        || !isValidHex(secondary)
        || !isValidHex(tertiary)
        || !isValidHex(accent)
        || !isValidHex(background)
        || !isValidHex(background2)
        || !isValidHex(text)
        || !isValidHex(textInverted)) {
      throw new IllegalArgumentException("All color values must be in #RRGGBB format.");
    }

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

  private static boolean isValidHex(String color) {
    return color != null && HEX_PATTERN.matcher(color).matches();
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
