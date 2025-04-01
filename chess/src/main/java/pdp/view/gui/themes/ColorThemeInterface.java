package pdp.view.gui.themes;

/** Common interface for both predefined and custom themes. */
public interface ColorThemeInterface {
  /**
   * Retrieves the primary color of the theme.
   *
   * @return primary color in String
   */
  String getPrimary();

  /**
   * Retrieves the secondary color of the theme.
   *
   * @return secondary color in String
   */
  String getSecondary();

  /**
   * Retrieves the tertiary color of the theme.
   *
   * @return tertiary color in String
   */
  String getTertiary();

  /**
   * Retrieves the accent color of the theme.
   *
   * @return accent color in String
   */
  String getAccent();

  /**
   * Retrieves the primary background color of the theme.
   *
   * @return primary background color in String
   */
  String getBackground();

  /**
   * Retrieves the secondary background color of the theme.
   *
   * @return secondary background color in String
   */
  String getBackground2();

  /**
   * Retrieves the primary color of text of the theme.
   *
   * @return primary text color in String
   */
  String getText();

  /**
   * Retrieves the secondary color of text of the theme.
   *
   * @return secondary text color in String
   */
  String getTextInverted();
}
