package pdp.view.gui.themes;

import static pdp.utils.Logging.error;
import static pdp.utils.Logging.print;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Enum containing all pre-built themes. */
public enum ColorTheme implements ColorThemeInterface {
  BLUE("#16697A", "#82C0CC", "#489FB5", "#FFA62B", "#EDE7E3", "#E0D3C9", "#000000", "#FFFFFF"),
  PURPLE("#5E548E", "#BE95C4", "#231942", "#9F86C0", "#F8E6F0", "#E8C8DA", "#231942", "#FFFFFF"),
  SIMPLE("#6D6FD9", "#DAE0F2", "#272727", "#F9CFF2", "#EDE7E3", "#DAE0F2", "#000000", "#FFFFFF"),
  GREY("#415a77", "#778da9", "#0d1b2a", "#1d3557", "#e0e1dd", "#F2F4F3", "#000000", "#FFFFFF");

  /** Location of the basic csv file in the resources. */
  private static final String PACKAGE_THEMES_FILE = "/styles/custom_themes.csv";

  /** Place to save the custom themes of the users. */
  private static final String USER_THEMES_FILE =
      System.getProperty("user.home") + "/.chessThemes/custom_themes.csv";

  /** Map containing the themes of the application (from enum and loaded from the csv). */
  private static final Map<String, ColorThemeInterface> THEMES = new HashMap<>();

  /** File to load with the custom themes. */
  private static File loadFile;

  /** Boolean to indicate whether the file is writeable or not. */
  private static boolean isUserFile;

  static {
    for (final ColorTheme theme : values()) {
      THEMES.put(theme.name(), theme);
    }

    setThemeFile();
    loadCustomThemes();
  }

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
   * Creates the elements of the enum.
   *
   * @param primary primary color (in string format)
   * @param secondary secondary color (in string format)
   * @param tertiary tertiary color (in string format)
   * @param accent accent color (in string format)
   * @param background primary background color (in string format)
   * @param background2 secondary background color (in string format)
   * @param text primary text color (in string format)
   * @param textInverted secondary text color (in string format)
   */
  ColorTheme(
      final String primary,
      final String secondary,
      final String tertiary,
      final String accent,
      final String background,
      final String background2,
      final String text,
      final String textInverted) {
    this.primary = primary;
    this.secondary = secondary;
    this.tertiary = tertiary;
    this.accent = accent;
    this.background = background;
    this.background2 = background2;
    this.text = text;
    this.textInverted = textInverted;
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

  /** Loads custom themes from the CSV file and updates the map. */
  public static void loadCustomThemes() {
    try (InputStream inputStream =
            isUserFile
                ? new FileInputStream(loadFile)
                : ColorTheme.class.getResourceAsStream(loadFile.getAbsolutePath());
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {

      String line;
      while ((line = reader.readLine()) != null) {
        final String[] parts = line.split(",");
        if (parts.length == 9) {
          final String name = parts[0].trim().toUpperCase();
          final CustomColorTheme customTheme =
              new CustomColorTheme(
                  name, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7],
                  parts[8]);
          THEMES.put(name, customTheme);
        }
      }
    } catch (IOException | NullPointerException e) {
      error("Failed to load custom themes: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void setThemeFile() {
    File file = new File(USER_THEMES_FILE);
    final File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }

    if (!file.exists()) {
      try (InputStream inputStream = ColorTheme.class.getResourceAsStream(PACKAGE_THEMES_FILE);
          BufferedReader reader =
              new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
          BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

        String line;
        while ((line = reader.readLine()) != null) {
          writer.write(line);
          writer.newLine();
        }
        print("Custom theme file created at: " + file.getAbsolutePath());
        isUserFile = true;
      } catch (IOException | NullPointerException e) {
        error("Failed to copy theme file: " + e.getMessage());
        file = new File(PACKAGE_THEMES_FILE);
        isUserFile = false;
      }
    } else {
      isUserFile = true;
    }
    loadFile = file;
  }

  /**
   * Retrieves a theme by name.
   *
   * @param name The theme name (case-insensitive).
   * @return The corresponding ColorThemeInterface or null if not found.
   */
  public static ColorThemeInterface getTheme(final String name) {
    return THEMES.get(name.toUpperCase());
  }

  /**
   * Retrieves all the saved themes in the field THEMES.
   *
   * @return An unmodifiable map of all available themes.
   */
  public static Map<String, ColorThemeInterface> getAllThemes() {
    return Collections.unmodifiableMap(THEMES);
  }

  /**
   * Adds a new theme to the list containing all themes.
   *
   * @param name name of the theme.
   * @param theme color theme.
   */
  public static void addTheme(final String name, final ColorThemeInterface theme) {
    THEMES.put(name.toUpperCase(), theme);

    if (isUserFile) {
      saveThemeToFile(name, theme);
    }
  }

  /**
   * Saves the given theme to a csv file.
   *
   * @param name name of the given theme.
   * @param theme theme to save.
   */
  private static void saveThemeToFile(final String name, final ColorThemeInterface theme) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(loadFile, true))) {
      writer.write(
          String.format(
              "%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
              name,
              theme.getPrimary(),
              theme.getSecondary(),
              theme.getTertiary(),
              theme.getAccent(),
              theme.getBackground(),
              theme.getBackground2(),
              theme.getText(),
              theme.getTextInverted()));
    } catch (IOException e) {
      error("Failed to save custom theme: " + e.getMessage());
    }
  }
}
