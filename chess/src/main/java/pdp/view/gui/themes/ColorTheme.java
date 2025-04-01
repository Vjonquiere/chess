package pdp.view.gui.themes;

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

  private static final String PACKAGE_THEMES_FILE = "/styles/custom_themes.csv";
  private static final String USER_THEMES_FILE =
      System.getProperty("user.home") + "/.chessThemes/custom_themes.csv";
  private static final Map<String, ColorThemeInterface> themes = new HashMap<>();
  private static File loadFile;
  private static boolean fileWritable = false;

  static {
    for (ColorTheme theme : values()) {
      themes.put(theme.name(), theme);
    }

    setThemeFile();
    System.out.println("Theme file: " + loadFile);
    loadCustomThemes();
  }

  /** String corresponding to the primary color, in hexadecimal format. */
  private String primary;

  /** String corresponding to the secondary color, in hexadecimal format. */
  private String secondary;

  /** String corresponding to the tertiary color, in hexadecimal format. */
  private String tertiary;

  /** String corresponding to the accent color, in hexadecimal format. */
  private String accent;

  /** String corresponding to the background color, in hexadecimal format. */
  private String background;

  /** String corresponding to the second background color, in hexadecimal format. */
  private String background2;

  /** String corresponding to the text color, in hexadecimal format. */
  private String text;

  /** String corresponding to the second text color, in hexadecimal format. */
  private String textInverted;

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

  /** Loads custom themes from the CSV file and updates the map. */
  public static void loadCustomThemes() {
    try (InputStream is =
            (fileWritable)
                ? new FileInputStream(loadFile)
                : ColorTheme.class.getResourceAsStream(loadFile.getAbsolutePath());
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {

      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length == 9) {
          String name = parts[0].trim().toUpperCase();
          CustomColorTheme customTheme =
              new CustomColorTheme(
                  name, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7],
                  parts[8]);
          themes.put(name, customTheme);
        }
      }
    } catch (IOException | NullPointerException e) {
      System.err.println("Failed to load custom themes: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void setThemeFile() {
    File file = new File(USER_THEMES_FILE);
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }

    if (!file.exists()) {
      try (InputStream is = ColorTheme.class.getResourceAsStream(PACKAGE_THEMES_FILE);
          BufferedReader reader =
              new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
          BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

        String line;
        while ((line = reader.readLine()) != null) {
          writer.write(line);
          writer.newLine();
        }
        System.out.println("Custom theme file created at: " + file.getAbsolutePath());
      } catch (IOException | NullPointerException e) {
        System.err.println("Failed to copy theme file: " + e.getMessage());
        loadFile = new File(PACKAGE_THEMES_FILE);
        fileWritable = false;
      }
    } else {
      fileWritable = true;
    }
    loadFile = file;
  }

  /**
   * Retrieves a theme by name.
   *
   * @param name The theme name (case-insensitive).
   * @return The corresponding ColorThemeInterface or null if not found.
   */
  public static ColorThemeInterface getTheme(String name) {
    return themes.get(name.toUpperCase());
  }

  /**
   * @return An unmodifiable map of all available themes.
   */
  public static Map<String, ColorThemeInterface> getAllThemes() {
    return Collections.unmodifiableMap(themes);
  }

  public static void addTheme(String name, ColorThemeInterface theme) {
    themes.put(name.toUpperCase(), theme);

    if (fileWritable) {
      saveThemeToFile(name, theme);
    }
  }

  private static void saveThemeToFile(String name, ColorThemeInterface theme) {
    ;

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
      System.err.println("Failed to save custom theme: " + e.getMessage());
    }
  }
}
