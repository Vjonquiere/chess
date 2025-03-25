package pdp.view.gui.themes;

/** Enum containing all pre-built themes. */
public enum ColorTheme {
  BLUE("#16697A", "#82C0CC", "#489FB5", "#FFA62B", "#EDE7E3", "#E0D3C9", "#000000", "#FFFFFF"),
  PURPLE("#5E548E", "#BE95C4", "#231942", "#9F86C0", "#F8E6F0", "#E8C8DA", "#231942", "#FFFFFF"),
  SIMPLE("#6D6FD9", "#DAE0F2", "#272727", "#F9CFF2", "#EDE7E3", "#DAE0F2", "#000000", "#FFFFFF"),
  GREY("#415a77", "#778da9", "#0d1b2a", "#1d3557", "#e0e1dd", "#F2F4F3", "#000000", "#FFFFFF"),
  CUSTOM("#5B5F97", "#B8B8D1", "#FF6B6C", "#FFC145", "#FFFFFB", "#FFFFF0", "#000000", "#FFFFFF");
  private String primary;
  private String secondary;
  private String tertiary;
  private String accent;
  private String background;
  private String background2;
  private String text;
  private String textInverted;

  ColorTheme(
      String primary,
      String secondary,
      String tertiary,
      String accent,
      String background,
      String background2,
      String text,
      String textInverted) {
    this.primary = primary;
    this.secondary = secondary;
    this.tertiary = tertiary;
    this.accent = accent;
    this.background = background;
    this.background2 = background2;
    this.text = text;
    this.textInverted = textInverted;
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

  /**
   * Set the CUSTOM theme to the given values.
   *
   * @param primary The primary color.
   * @param secondary The secondary color.
   * @param tertiary The tertiary color.
   * @param accent The accent color.
   * @param background The background color.
   * @param background2 The second background color.
   * @param text The text color.
   * @param textInverted The inverted text color.
   */
  public static void setCustom(
      String primary,
      String secondary,
      String tertiary,
      String accent,
      String background,
      String background2,
      String text,
      String textInverted) {
    CUSTOM.primary = primary;
    CUSTOM.secondary = secondary;
    CUSTOM.tertiary = tertiary;
    CUSTOM.accent = accent;
    CUSTOM.background = background;
    CUSTOM.background2 = background2;
    CUSTOM.text = text;
    CUSTOM.textInverted = textInverted;
  }
}
