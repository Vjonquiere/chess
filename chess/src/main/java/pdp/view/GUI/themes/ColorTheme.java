package pdp.view.GUI.themes;

import pdp.view.GUIView;

public enum ColorTheme {
  BLUE("#16697A", "#82C0CC", "#489FB5", "#FFA62B", "#EDE7E3", "#E0D3C9", "black", "white"),
  PURPLE("#5E548E", "#BE95C4", "#231942", "#9F86C0", "#F8E6F0", "#E8C8DA", "#231942", "white"),
  SIMPLE("#6D6FD9", "#DAE0F2", "#272727", "#F9CFF2", "#EDE7E3", "#DAE0F2", "black", "white"),
  CUSTOM("#415a77", "#778da9", "#0d1b2a", "#1d3557", "#e0e1dd", "#e0e1dd", "white", "white");

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

  public void setCustom(
      String primary,
      String secondary,
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

  public String getCSSStyle() {
    return ".list-view {\n"
        + "    -fx-background-color: transparent;\n"
        + "}\n"
        + "\n"
        + ".list-cell:even  {\n"
        + "    -fx-background-color: "
        + GUIView.theme.getBackground2()
        + ";\n"
        + "    -fx-text-fill: "
        + GUIView.theme.getText()
        + ";\n"
        + "}\n"
        + "\n"
        + ".list-cell:odd{\n"
        + "    -fx-background-color: "
        + GUIView.theme.getBackground()
        + ";\n"
        + "    -fx-text-fill: "
        + GUIView.theme.getText()
        + ";\n"
        + "}\n"
        + "\n"
        + ".list-cell:selected  {\n"
        + "    -fx-background-color: "
        + GUIView.theme.getSecondary()
        + ";\n"
        + "    -fx-text-fill: "
        + GUIView.theme.getPrimary()
        + ";\n"
        + "}\n"
        + "\n"
        + ".menu-bar{\n"
        + "    -fx-background-color: "
        + GUIView.theme.getAccent()
        + ";\n"
        + "    -fx-border-color: "
        + GUIView.theme.getTertiary()
        + ";\n"
        + "}\n"
        + " .menu-item, .menu, .context-menu {\n"
        + "    -fx-background-color: "
        + GUIView.theme.getAccent()
        + ";\n"
        + "}\n"
        + ".menu .label {\n"
        + "    -fx-text-fill: "
        + GUIView.theme.getText()
        + ";\n"
        + "}\n"
        + ".menu-item:hover, .menu:hover {\n"
        + "    -fx-background-color: "
        + GUIView.theme.getPrimary()
        + ";\n"
        + "}\n"
        + ".button:hover {"
        + "-fx-background-color: "
        + GUIView.theme.getPrimary()
        + ";"
        + "-fx-text-fill: "
        + GUIView.theme.getSecondary()
        + ";"
        + "-fx-border-color: "
        + GUIView.theme.getSecondary()
        + ";"
        + "-fx-font-size: 18px;"
        + "-fx-font-weight: bold;"
        + "-fx-padding: 15;"
        + "-fx-background-radius: 20;"
        + "-fx-border-radius: 20;"
        + "}\n"
        + ".button {"
        + "-fx-background-color: "
        + GUIView.theme.getSecondary()
        + ";"
        + "-fx-text-fill: "
        + GUIView.theme.getPrimary()
        + ";"
        + "-fx-border-color: "
        + GUIView.theme.getPrimary()
        + ";"
        + "-fx-font-size: 18px;"
        + "-fx-font-weight: bold;"
        + "-fx-padding: 15;"
        + "-fx-background-radius: 20;"
        + "-fx-border-radius: 20;"
        + "}\n";
  }
}
