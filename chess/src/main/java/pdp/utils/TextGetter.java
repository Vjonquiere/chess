package pdp.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextGetter {
  private static Locale locale = Locale.ENGLISH; // Default language
  private static ResourceBundle messages = ResourceBundle.getBundle("chessResources");

  /*Private constructor to avoid instantiation*/
  private TextGetter() {}

  public static void setLocale(String languageCode) {
    locale = new Locale(languageCode);
    messages = ResourceBundle.getBundle("chessResources", locale);
  }

  public static String getText(String key) {
    return messages.getString(key);
  }
}
