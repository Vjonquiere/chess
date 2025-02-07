package pdp.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class TextGetter {
  private static Locale locale = Locale.ENGLISH; // Default language
  private static ResourceBundle messages = ResourceBundle.getBundle("chessResources");

  /*Private constructor to avoid instantiation*/
  private TextGetter() {}

  /**
   * Set the language of the app, english by default if the language asked is not implemented.
   *
   * @param languageCode wished language for the app
   */
  public static void setLocale(String languageCode) {
    locale = new Locale(languageCode);
    messages = ResourceBundle.getBundle("chessResources", locale);
  }

  /**
   * Gets the string corresponding to the key in the correct language.
   *
   * @param key key corresponding to the string to get from the resource file
   * @return String corresponding to the value of the given key
   */
  public static String getText(String key) {
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
  public static String getText(String key, Object... args) {
    return MessageFormat.format(getText(key), args);
  }
}
