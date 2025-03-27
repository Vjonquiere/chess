package pdp.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class for internationalization. Need to put all strings needed to be translated in the
 * chessResources_locale.properties files in the resources.
 */
public final class TextGetter {
  /** Locale corresponding to the language of the app, english by default. */
  private static Locale locale = Locale.ENGLISH;

  /** ResourceBundle to get the strings from. */
  private static ResourceBundle messages = ResourceBundle.getBundle("chessResources", locale);

  static {
    Locale.setDefault(locale);
  }

  /*Private constructor to avoid instantiation.*/
  private TextGetter() {}

  /**
   * Set the language of the app, english by default if the language asked is not implemented.
   *
   * @param languageCode wished language for the app
   */
  public static void setLocale(final String languageCode) {
    // If the language code is "fr", set to French, otherwise default to English
    if ("fr".equalsIgnoreCase(languageCode)) {
      locale = Locale.FRENCH;
    } else {
      locale = Locale.ENGLISH;
    }
    messages = ResourceBundle.getBundle("chessResources", locale);
  }

  /**
   * Retrieves the locale used in the application.
   *
   * @return locale save in the fields
   */
  public static Locale getLocale() {
    return locale;
  }

  /**
   * Gets the string corresponding to the key in the correct language.
   *
   * @param key key corresponding to the string to get from the resource file
   * @return String corresponding to the value of the given key
   */
  public static String getText(final String key) {
    return messages.getString(key);
  }

  /**
   * Gets the string corresponding to the key and formats it using the provided arguments.
   *
   * @param key the key to the format string containing placeholders ({0}, {1}, etc.).
   * @param args the values to be substituted into the placeholders.
   * @return the formatted string with arguments replaced.
   * @throws IllegalArgumentException if the pattern is invalid or argument indices are out of
   *     range.
   */
  public static String getText(final String key, final Object... args) {
    return MessageFormat.format(getText(key), args);
  }
}
