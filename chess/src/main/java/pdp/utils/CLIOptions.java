package pdp.utils;

import static pdp.utils.Logging.DEBUG;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLIOptions {
  private static final Logger LOGGER = Logger.getLogger(CLIOptions.class.getName());

  private static final String DEFAULT_CONFIG_FILE = "default.chessrc";

  private CLIOptions() {}

  public static HashMap<OptionType, String> parseOptions(String[] args, Runtime runtime) {
    Options options = new Options();
    for (OptionType optionType : OptionType.values()) {
      options.addOption(optionType.getOption());
    }

    Map<String, String> defaultArgs = new HashMap<>();
    CommandLineParser parser = new DefaultParser();
    HashMap<OptionType, String> activatedOptions = new HashMap<>();

    try {
      CommandLine cmd = parser.parse(options, args);
      String configFile = cmd.getOptionValue(OptionType.CONFIG.getLong(), (String) null);
      defaultArgs = loadDefaultArgs(configFile, activatedOptions);
      handleLoggingOptions(cmd, defaultArgs);
      if (handleImmediateExitOptions(cmd, options, runtime)) return null;
      processOptions(cmd, defaultArgs, activatedOptions);
    } catch (ParseException exp) {
      System.out.println("Parsing failed.  Reason: " + exp.getMessage());
      new HelpFormatter().printHelp("chess", options);
      runtime.exit(1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return activatedOptions;
  }

  private static Map<String, String> loadDefaultArgs(
      String file, HashMap<OptionType, String> activatedOptions) {
    InputStream inputStream = null;
    if (file != null && !file.endsWith(".chessrc")) {
      file = null;
      System.out.println("Selected file is not of .chessrc format, defaulting to default options");
      activatedOptions.put(OptionType.CONFIG, DEFAULT_CONFIG_FILE);
    }
    if (file != null) {
      try {
        inputStream = new FileInputStream(file);
      } catch (FileNotFoundException e) {
        System.err.println("Error while parsing chessrc file: " + e.getMessage());
        System.err.println("Default options will be used");
        file = null;
        activatedOptions.put(OptionType.CONFIG, DEFAULT_CONFIG_FILE);
      }
    }
    if (file == null) {
      try {
        inputStream = CLIOptions.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
        activatedOptions.put(OptionType.CONFIG, DEFAULT_CONFIG_FILE);
        if (inputStream == null)
          throw new FileNotFoundException("config.chessrc not found in classpath!");
      } catch (Exception e) {
        System.err.println("Error while parsing chessrc file: " + e.getMessage());
        activatedOptions.put(OptionType.CONFIG, null);
        return new HashMap<>();
      }
    }

    if (inputStream != null) {
      try {
        Map<String, Map<String, String>> iniMap = IniParser.parseIni(inputStream);
        return iniMap.getOrDefault("Default", new HashMap<>());
      } catch (Exception e) {
        activatedOptions.put(OptionType.CONFIG, null);
        return new HashMap<>();
      }
    }

    activatedOptions.put(OptionType.CONFIG, null);
    return new HashMap<>();
  }

  private static void handleLoggingOptions(CommandLine cmd, Map<String, String> defaultArgs) {
    if (cmd.hasOption(OptionType.DEBUG.getLong()) || "true".equals(defaultArgs.get("debug"))) {
      Logging.setDebug(true);
      Logging.configureLogging(LOGGER);
      DEBUG(LOGGER, "Debug mode activated");
    }
    if (cmd.hasOption(OptionType.VERBOSE.getLong()) || "true".equals(defaultArgs.get("verbose"))) {
      Logging.setVerbose(true);
      Logging.configureLogging(LOGGER);
      DEBUG(LOGGER, "Verbose mode activated");
    }
  }

  private static boolean handleImmediateExitOptions(
      CommandLine cmd, Options options, Runtime runtime) throws IOException {
    if (cmd.hasOption(OptionType.HELP.getLong())) {
      DEBUG(LOGGER, "Help option activated");
      new HelpFormatter().printHelp("chess", options);
      runtime.exit(0);
      return true;
    }
    if (cmd.hasOption(OptionType.VERSION.getLong())) {
      DEBUG(LOGGER, "Version option activated");
      Properties properties = new Properties();
      properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
      System.out.println("Version: " + properties.getProperty("version"));
      runtime.exit(0);
      return true;
    }
    return false;
  }

  private static void processOptions(
      CommandLine cmd,
      Map<String, String> defaultArgs,
      HashMap<OptionType, String> activatedOptions) {
    for (OptionType option : OptionType.values()) {

      if (option == OptionType.CONFIG) {
        if (activatedOptions.containsKey(OptionType.CONFIG)) {
          continue;
        }
      }
      boolean userProvided = cmd.hasOption(option.getLong());
      boolean defaultEnabled =
          defaultArgs.containsKey(option.getLong())
              && !"false".equals(defaultArgs.get(option.getLong()));

      if (userProvided || defaultEnabled) {
        String value =
            userProvided
                ? cmd.getOptionValue(option.getLong(), "")
                : defaultArgs.get(option.getLong());
        activatedOptions.put(option, value != null ? value : "");
        DEBUG(LOGGER, option.getLong() + " option activated");

        if (option == OptionType.LANG) {
          if (value.equals("en")) {
            DEBUG(LOGGER, "Language = English (already set by default)");
            // TODO: de-comment when french file finished
          } /*else if (value.equals("fr")) {
              DEBUG(LOGGER, "Language = French");
              TextGetter.setLocale(cmd.getParsedOptionValue("lang"));
            } */ else {
            System.err.println(
                "Language "
                    + cmd.getOptionValue(option.getLong(), "")
                    + " not supported, language = english");
          }
        }

        if (!isFeatureImplemented(option)) {
          System.err.println(option.getLong() + " not implemented yet");
        }
      }
    }
    validateAIOptions(cmd, activatedOptions);
  }

  private static void validateAIOptions(
      CommandLine cmd, HashMap<OptionType, String> activatedOptions) {
    if (!activatedOptions.containsKey(OptionType.AI)) {
      for (OptionType aiOption :
          new OptionType[] {
            OptionType.AI_MODE, OptionType.AI_DEPTH, OptionType.AI_HEURISTIC, OptionType.AI_TIME
          }) {
        if (cmd.hasOption(aiOption.getLong())) {
          System.err.println("Modifying " + aiOption.getLong() + " requires 'a' argument");
        }
      }
    }
  }

  private static boolean isFeatureImplemented(OptionType option) {
    return switch (option) {
      case BLITZ, GUI, TIME, CONTEST, AI, AI_MODE, AI_DEPTH, AI_HEURISTIC, AI_TIME -> false;
      default -> true;
    };
  }
}
