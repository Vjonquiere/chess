package pdp.view.GUI.themes;

public enum ColorTheme {
  BLUE("#489FB5", "#82C0CC", "#FFA62B", "#EDE7E3"),
  PURPLE("#5E548E", "#BE95C4", "#9F86C0", "#F8E6F0");

  private final String primary;
  private final String secondary;
  private final String accent;
  private final String background;

  ColorTheme(String primary, String secondary, String accent, String background) {
    this.primary = primary;
    this.secondary = secondary;
    this.accent = accent;
    this.background = background;
  }

  public String getPrimary() {
    return primary;
  }

  public String getSecondary() {
    return secondary;
  }

  public String getAccent() {
    return accent;
  }

  public String getBackground() {
    return background;
  }
}
