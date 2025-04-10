package pdp.utils;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;
import static pdp.utils.Logging.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/** Utility class to parse the command line options. */
public final class CommandLineOptions {

  /** Location of the default configuration folder. */
  private static final String USER_SETTINGS_DIR =
      System.getProperty("user.home") + "/.chessSettings";

  /** Default name of the configuration file. Not final for test purposes. */
  private static String defaultConfigFile = "default.chessrc";

  /** Location of the default configuration file */
  private static final String USER_DEFAULT_CONFIG = USER_SETTINGS_DIR + "/default.chessrc";

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(CommandLineOptions.class.getName());

  /** Private constructor to avoid instantiation. */
  private CommandLineOptions() {}

  /**
   * Parses the given command line arguments.
   *
   * <p>Returns a map of the activated options, with the option name as the key and the option value
   * as the value.
   *
   * @param args The command line arguments.
   * @param runtime The runtime.
   * @return A map of the activated options.
   */
  public static HashMap<OptionType, String> parseOptions(
      final String[] args, final Runtime runtime) {
    final Options options = new Options();
    for (final OptionType optionType : OptionType.values()) {
      options.addOption(optionType.getOption());
    }

    final Map<String, String> defaultArgs;
    final CommandLineParser parser = new DefaultParser();
    final HashMap<OptionType, String> activatedOptions = new HashMap<>();

    try {
      final CommandLine cmd = parser.parse(options, args);
      final String configFile = cmd.getOptionValue(OptionType.CONFIG.getLong(), (String) null);
      defaultArgs = loadDefaultArgs(configFile, activatedOptions);
      handleLoggingOptions(cmd, defaultArgs);
      if (handleImmediateExitOptions(cmd, options, runtime)) {
        return null;
      }
      processOptions(cmd, defaultArgs, activatedOptions, options, runtime);

      if (!cmd.getArgList().isEmpty()) {
        final String loadFile = cmd.getArgList().get(0);
        activatedOptions.put(OptionType.LOAD, loadFile);
        debug(LOGGER, "Load file set to: " + loadFile);
      }
    } catch (ParseException exp) {
      error("Parsing failed.  Reason: " + exp.getMessage());
      new HelpFormatter().printHelp("chess", options);
      runtime.exit(1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return activatedOptions;
  }

  private static Map<String, String> loadDefaultArgs(
      String file, final HashMap<OptionType, String> activatedOptions) throws IOException {
    InputStream inputStream = null;

    if (file != null && !file.endsWith(".chessrc")) {
      file = null;
      print("Invalid config file format. Must end with .chessrc. Using defaults.");
      activatedOptions.put(OptionType.CONFIG, defaultConfigFile);
    }

    if (file != null) {
      try {
        inputStream = new FileInputStream(file);
        activatedOptions.put(OptionType.CONFIG, file);
        debug(LOGGER, "Using user-provided config: " + file);
      } catch (FileNotFoundException e) {
        error("Config file not found: " + file);
        file = null;
      }
    }

    if (file == null) {
      final File userConfigFile = new File(USER_DEFAULT_CONFIG);
      ensureUserConfigExists(userConfigFile);

      try {
        inputStream = new FileInputStream(userConfigFile);
        activatedOptions.put(OptionType.CONFIG, USER_DEFAULT_CONFIG);
        debug(LOGGER, "Using user home config: " + USER_DEFAULT_CONFIG);
      } catch (FileNotFoundException e) {
        inputStream =
            CommandLineOptions.class.getClassLoader().getResourceAsStream(defaultConfigFile);
        activatedOptions.put(OptionType.CONFIG, defaultConfigFile);
        debug(LOGGER, "Falling back to resource config");
      }
    }

    if (inputStream != null) {
      try {
        final Map<String, Map<String, String>> iniMap = IniParser.parseIni(inputStream);
        return iniMap.getOrDefault("Default", new HashMap<>());
      } catch (Exception e) {
        error("Error parsing config: " + e.getMessage());
        return new HashMap<>();
      }
    }

    return new HashMap<>();
  }

  /**
   * Ensures the user config file exists by copying from resources if needed. Returns true if the
   * file exists or was successfully created.
   */
  private static boolean ensureUserConfigExists(File userConfigFile) {
    if (userConfigFile.exists()) {
      return true;
    }

    File parentDir = userConfigFile.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }

    try (InputStream is = CommandLineOptions.class.getResourceAsStream("/" + defaultConfigFile);
        FileOutputStream fos = new FileOutputStream(userConfigFile)) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) != -1) {
        fos.write(buffer, 0, bytesRead);
      }
      print("Created default config at: " + userConfigFile.getAbsolutePath());
      return true;
    } catch (IOException | NullPointerException e) {
      error("Failed to create user config: " + e.getMessage());
      return false;
    }
  }

  /**
   * Sets the logging options to be used in the program according to the command line options and
   * the default arguments.
   *
   * @param cmd The command line options.
   * @param defaultArgs The default arguments.
   */
  private static void handleLoggingOptions(
      final CommandLine cmd, final Map<String, String> defaultArgs) {
    if (cmd.hasOption(OptionType.DEBUG.getLong()) || "true".equals(defaultArgs.get("debug"))) {
      Logging.setDebug(true);
      Logging.configureLogging(LOGGER);
      debug(LOGGER, "Debug mode activated");
    }
    if (cmd.hasOption(OptionType.VERBOSE.getLong()) || "true".equals(defaultArgs.get("verbose"))) {
      Logging.setVerbose(true);
      Logging.configureLogging(LOGGER);
      debug(LOGGER, "Verbose mode activated");
    }
  }

  /**
   * Checks if the user has requested immediate exit options (help or version) and handles them.
   *
   * @param cmd The command line options.
   * @param options The options to print in the help message.
   * @param runtime The runtime to exit.
   * @return true if the option was handled, false otherwise.
   * @throws IOException If an IO exception occurs.
   */
  private static boolean handleImmediateExitOptions(
      final CommandLine cmd, final Options options, final Runtime runtime) throws IOException {
    if (cmd.hasOption(OptionType.HELP.getLong())) {
      debug(LOGGER, "Help option activated");
      new HelpFormatter().printHelp("chess", options);
      runtime.exit(0);
      return true;
    }
    if (cmd.hasOption(OptionType.VERSION.getLong())) {
      debug(LOGGER, "Version option activated");
      final Properties properties = new Properties();
      properties.load(CommandLineOptions.class.getClassLoader().getResourceAsStream(".properties"));
      print("Version: " + properties.getProperty("version"));
      runtime.exit(0);
      return true;
    }
    return false;
  }

  /**
   * Handles the command line options that require a valid file path. This method checks for the
   * presence of the --contest, --load, and --config options and if the path provided is null or
   * empty.
   *
   * @param activatedOptions The map of activated command line options.
   * @param runtime The runtime to exit.
   */
  private static void handlePathInput(
      final HashMap<OptionType, String> activatedOptions, final Runtime runtime) {
    for (final OptionType type : List.of(OptionType.CONTEST, OptionType.LOAD, OptionType.CONFIG)) {
      if (activatedOptions.containsKey(type)) {
        final String path = activatedOptions.get(type);
        if (path == null || path.isEmpty()) {
          error("Error: --" + type.getLong() + " option requires a valid file path.");
          error("Use '-h' option for a list of available options.");
          activatedOptions.remove(type);
          runtime.exit(1);
        }
      }
    }
  }

  /**
   * Processes the command line general options and the default arguments to build a map of the
   * activated options. The map contains the option name as the key and the option value as the
   * value.
   *
   * @param cmd The command line options.
   * @param defaultArgs The default arguments.
   * @param activatedOptions The map to store the activated options.
   */
  private static void processOptions(
      final CommandLine cmd,
      final Map<String, String> defaultArgs,
      final HashMap<OptionType, String> activatedOptions,
      final Options options,
      final Runtime runtime) {
    for (final OptionType option : OptionType.values()) {

      if (option == OptionType.CONFIG) {
        if (activatedOptions.containsKey(OptionType.CONFIG)) {
          continue;
        }
      }

      final boolean userProvided = cmd.hasOption(option.getLong());
      final boolean defaultEnabled =
          defaultArgs.containsKey(option.getLong())
              && !"false".equals(defaultArgs.get(option.getLong()));

      if (userProvided || defaultEnabled) {
        final String value =
            userProvided
                ? cmd.getOptionValue(option.getLong(), "")
                : defaultArgs.get(option.getLong());
        activatedOptions.put(option, value != null ? value : "");
        debug(LOGGER, option.getLong() + " option activated");

        if (option == OptionType.LANG) {
          switch (value) {
            case "en" -> {
              debug(LOGGER, "Language = English (already set by default)");
              TextGetter.setLocale("en");
            }
            case "fr" -> {
              debug(LOGGER, "Language = French");
              TextGetter.setLocale("fr");
            }
            default -> {
              error(
                  "Language "
                      + cmd.getOptionValue(option.getLong(), "")
                      + " not supported, language = english");
              TextGetter.setLocale("en");
            }
          }
        }
      }
    }

    handlePathInput(activatedOptions, runtime);

    if (activatedOptions.containsKey(OptionType.CONTEST)) {
      activatedOptions.put(OptionType.AI, "A");
      activatedOptions.remove(OptionType.LOAD);
      debug(
          LOGGER, "Contest mode activated with file: " + activatedOptions.get(OptionType.CONTEST));
    }

    if (activatedOptions.containsKey(OptionType.TIME)
        && !activatedOptions.containsKey(OptionType.BLITZ)) {
      error("The TIME option can't be used without BLITZ activated : option ignored.");
      activatedOptions.remove(OptionType.TIME);
    } else if (activatedOptions.containsKey(OptionType.BLITZ)
        && !activatedOptions.containsKey(OptionType.TIME)) {
      activatedOptions.put(OptionType.TIME, "30");
    }

    if (activatedOptions.containsKey(OptionType.AI)
        && activatedOptions.get(OptionType.AI).isEmpty()) {
      activatedOptions.put(OptionType.AI, "W");
    }

    validateAiOptions(activatedOptions);
  }

  /**
   * Validates AI-related command line options and ensures they are correctly activated.
   *
   * <p>This method checks if the AI option is present in the activated options map. If not,
   * AI-related options (AI_MODE, AI_DEPTH, AI_HEURISTIC, AI_TIME) can't be used.
   *
   * @param activatedOptions The map containing the currently activated options.
   */
  public static void validateAiOptions(final HashMap<OptionType, String> activatedOptions) {
    if (!activatedOptions.containsKey(OptionType.AI)) {
      for (final OptionType aiOption :
          new OptionType[] {
            OptionType.AI_MODE,
            OptionType.AI_DEPTH,
            OptionType.AI_HEURISTIC,
            OptionType.AI_TIME,
            OptionType.AI_DEPTH_W,
            OptionType.AI_DEPTH_B,
            OptionType.AI_HEURISTIC_W,
            OptionType.AI_HEURISTIC_B,
            OptionType.AI_MODE_W,
            OptionType.AI_MODE_B,
            OptionType.AI_SIMULATION,
            OptionType.AI_ENDGAME,
            OptionType.AI_ENDGAME_W,
            OptionType.AI_ENDGAME_B
          }) {
        if (activatedOptions.containsKey(aiOption)) {
          error("Modifying " + aiOption.getLong() + " requires 'a' argument");
          activatedOptions.remove(aiOption);
        }
      }
    } else {
      if (!activatedOptions.containsKey(OptionType.AI_MODE)) {
        activatedOptions.put(OptionType.AI_MODE, "ALPHA_BETA");
      }
      if (!activatedOptions.containsKey(OptionType.AI_MODE_W)) {
        activatedOptions.put(OptionType.AI_MODE_W, activatedOptions.get(OptionType.AI_MODE));
      }
      if (!activatedOptions.containsKey(OptionType.AI_MODE_B)) {
        activatedOptions.put(OptionType.AI_MODE_B, activatedOptions.get(OptionType.AI_MODE));
      }

      activatedOptions.remove(OptionType.AI_MODE);

      if (!activatedOptions.containsKey(OptionType.AI_ENDGAME)) {
        activatedOptions.put(OptionType.AI_ENDGAME, "ENDGAME");
      }
      if (!activatedOptions.containsKey(OptionType.AI_ENDGAME_W)) {
        activatedOptions.put(OptionType.AI_ENDGAME_W, activatedOptions.get(OptionType.AI_ENDGAME));
      }
      if (!activatedOptions.containsKey(OptionType.AI_ENDGAME_B)) {
        activatedOptions.put(OptionType.AI_ENDGAME_B, activatedOptions.get(OptionType.AI_ENDGAME));
      }

      activatedOptions.remove(OptionType.AI_ENDGAME);

      if (!activatedOptions.containsKey(OptionType.AI_DEPTH)) {
        activatedOptions.put(OptionType.AI_DEPTH, "4");
      }

      if (!activatedOptions.containsKey(OptionType.AI_DEPTH_W)) {
        activatedOptions.put(OptionType.AI_DEPTH_W, activatedOptions.get(OptionType.AI_DEPTH));
      }
      if (!activatedOptions.containsKey(OptionType.AI_DEPTH_B)) {
        activatedOptions.put(OptionType.AI_DEPTH_B, activatedOptions.get(OptionType.AI_DEPTH));
      }

      activatedOptions.remove(OptionType.AI_DEPTH);

      if (!activatedOptions.containsKey(OptionType.AI_SIMULATION)) {
        activatedOptions.put(OptionType.AI_SIMULATION, "150");
      }

      if (!activatedOptions.containsKey(OptionType.AI_SIMULATION_W)) {
        activatedOptions.put(
            OptionType.AI_SIMULATION_W, activatedOptions.get(OptionType.AI_SIMULATION));
      }
      if (!activatedOptions.containsKey(OptionType.AI_SIMULATION_B)) {
        activatedOptions.put(
            OptionType.AI_SIMULATION_B, activatedOptions.get(OptionType.AI_SIMULATION));
      }

      activatedOptions.remove(OptionType.AI_SIMULATION);

      if (!activatedOptions.containsKey(OptionType.AI_HEURISTIC)) {
        activatedOptions.put(OptionType.AI_HEURISTIC, "STANDARD");
      }
      if (!activatedOptions.containsKey(OptionType.AI_HEURISTIC_W)) {
        activatedOptions.put(
            OptionType.AI_HEURISTIC_W, activatedOptions.get(OptionType.AI_HEURISTIC));
      }
      if (!activatedOptions.containsKey(OptionType.AI_HEURISTIC_B)) {
        activatedOptions.put(
            OptionType.AI_HEURISTIC_B, activatedOptions.get(OptionType.AI_HEURISTIC));
      }

      activatedOptions.remove(OptionType.AI_HEURISTIC);

      if (activatedOptions.containsKey(OptionType.AI_TIME)
          && activatedOptions.get(OptionType.AI_TIME).isEmpty()) {
        activatedOptions.put(OptionType.AI_TIME, "5");
      }
    }
  }
}
