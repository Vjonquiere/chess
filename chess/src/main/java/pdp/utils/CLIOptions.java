package pdp.utils;

import static pdp.utils.Logging.DEBUG;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.cli.*;

public class CLIOptions {
  private static final Logger LOGGER = Logger.getLogger(CLIOptions.class.getName());

  /*Private constructor to avoid instantiation*/
  private CLIOptions() {}
  ;

  /**
   * Parse the options given in parameters. The help will have the priority over any other option.
   * If the usage is wrong, an error message will be displayed on the standard error output.
   * Careful, partial matching : -aifdghj will still enable -a, no errors are displayed.
   *
   * @param args Array of strings received by main, arguments to parse
   * @param runtime Runtime to exit cleanly
   */
  public static HashMap<OptionType, String> parseOptions(String[] args, Runtime runtime) {
    final Options options = new Options();
    Option help = new Option("h", "help", false, "Print this message and exit");
    Option version = new Option("V", "version", false, "Print the version information and exit");
    Option verbose = new Option("v", "verbose", false, "Display more information");
    Option debug = new Option("d", "debug", false, "Print debugging information");
    Option blitz = new Option("b", "blitz", false, "Play in blitz mode");
    Option gui = new Option("g", "gui", false, "Displays the game with a  graphical interface.");
    Option time =
        Option.builder("t")
            .longOpt("time")
            .hasArg(true)
            .argName("TIME")
            .desc("Specify time per round for blitz mode (default 30min)")
            .type(Integer.class)
            .build();
    Option contest =
        Option.builder("c")
            .longOpt("contest")
            .hasArg(true)
            .argName("FILENAME")
            .desc("AI plays one move in the given file")
            .build();
    Option ai =
        Option.builder("a")
            .longOpt("ai")
            .optionalArg(true)
            .argName("COLOR")
            .desc(
                "Launch the program in AI mode, with artificial player with COLOR 'B' or 'A' (All),(W by default).")
            .build();
    Option ai_mode =
        Option.builder()
            .longOpt("ai-mode")
            .hasArg(true)
            .argName("ALGORITHM")
            .desc("Choose the exploration algorithm for the artificial player.")
            .build();
    Option ai_depth =
        Option.builder()
            .longOpt("ai-depth")
            .hasArg(true)
            .argName("DEPTH")
            .desc("Specify the depth of the AI algorithm")
            .build();
    Option ai_heuristic =
        Option.builder()
            .longOpt("ai-heuristic")
            .hasArg(true)
            .argName("HEURISTIC")
            .desc("Choose the heuristic for the artificial player")
            .build();
    Option ai_time =
        Option.builder()
            .longOpt("ai-time")
            .argName("TIME")
            .hasArg(true)
            .desc("Specify the time of reflexion for AI mode (default 5 seconds)")
            .build();
    Option lang =
        Option.builder()
            .longOpt("lang")
            .argName("LANGUAGE")
            .hasArg(true)
            .desc("Choose the language for the app (en supported)")
            .build();

    options.addOption(help);
    options.addOption(version);
    options.addOption(verbose);
    options.addOption(debug);
    options.addOption(blitz);
    options.addOption(gui);
    options.addOption(time);
    options.addOption(contest);
    options.addOption(ai);
    options.addOption(ai_mode);
    options.addOption(ai_depth);
    options.addOption(ai_heuristic);
    options.addOption(ai_time);
    options.addOption(lang);

    Map<String, String> defaultArgs;
    try {
      InputStream inputStream =
          CLIOptions.class.getClassLoader().getResourceAsStream("config.chessrc");
      if (inputStream == null) {
        throw new FileNotFoundException("config.chessrc not found in classpath!");
      }
      Map<String, Map<String, String>> iniMap = IniParser.parseIni(inputStream);
      defaultArgs = iniMap.get("Default");
    } catch (Exception e) {
      System.err.println("Error while parsing chessrc file: " + e.getMessage());
      defaultArgs = new HashMap<>();
    }

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    HashMap<OptionType, String> activatedOptions = new HashMap<>();
    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption(debug)
          || defaultArgs.containsKey("debug") && defaultArgs.get("debug").equals("true")) {
        Logging.setDebug(true);
        Logging.configureLogging(LOGGER);
        DEBUG(LOGGER, "Debug mode activated");
      }
      if (cmd.hasOption(verbose)
          || defaultArgs.containsKey("verbose") && defaultArgs.get("verbose").equals("true")) {
        Logging.setVerbose(true);
        Logging.configureLogging(LOGGER);
        DEBUG(LOGGER, "Verbose mode activated");
      }
      if (cmd.hasOption(help)) {
        DEBUG(LOGGER, "Help option activated");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("chess", options);
        runtime.exit(0);
        return null;
      }
      if (cmd.hasOption(version)) {
        DEBUG(LOGGER, "Version option activated");
        final Properties properties = new Properties();
        properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
        System.out.println("Version: " + properties.getProperty("version"));
        runtime.exit(0);
        return null;
      }
      if (cmd.hasOption(lang)) {
        DEBUG(LOGGER, "Language option activated");
        if (cmd.getParsedOptionValue(lang).equals("en")) {
          DEBUG(LOGGER, "Language = English (already set by default)");
          // TODO: de-comment when french file finished
        } /*else if (cmd.getParsedOptionValue(lang).equals("fr")) {
            DEBUG(LOGGER, "Language = French");
            TextGetter.setLocale(cmd.getParsedOptionValue("lang"));
          } */ else {
          System.err.println(
              "Language " + cmd.getParsedOptionValue(lang) + " not supported, language = english");
        }
      }
      if (cmd.hasOption(blitz)
          || defaultArgs.containsKey("blitz") && defaultArgs.get("blitz").equals("true")) {
        DEBUG(LOGGER, "Blitz mode activated");
        activatedOptions.put(OptionType.BLITZ, "");
        System.err.println("Blitz not implemented yet");
      }
      if (cmd.hasOption(gui)) {
        DEBUG(LOGGER, "GUI mode activated");
        activatedOptions.put(OptionType.GUI, "");
        System.err.println("GUI not implemented yet");
      }
      if (cmd.hasOption(time)) {
        DEBUG(LOGGER, "Blitz time option activated");
        activatedOptions.put(OptionType.TIME, cmd.getOptionValue(time));
        System.err.println("Blitz time not implemented yet");
      } else if (defaultArgs.containsKey("time")) {
        DEBUG(LOGGER, "Blitz time option activated");
        activatedOptions.put(OptionType.TIME, defaultArgs.get("time"));
        System.err.println("Blitz time not implemented yet");
      }
      if (cmd.hasOption(contest)) {
        DEBUG(LOGGER, "Contest mode activated");
        activatedOptions.put(OptionType.CONTEST, cmd.getOptionValue(contest));
        System.err.println("Contest not implemented yet");
      }
      if (cmd.hasOption(ai)) {
        DEBUG(LOGGER, "AI activated");
        activatedOptions.put(OptionType.AI, cmd.getOptionValue(ai));
        System.err.println("AI not implemented yet");
      } else if (defaultArgs.containsKey("ai") && !defaultArgs.get("ai").equals("false")) {
        DEBUG(LOGGER, "AI activated");
        activatedOptions.put(OptionType.AI, defaultArgs.get("ai"));
        System.err.println("AI not implemented yet");
      }

      if (cmd.hasOption(ai_mode)) {
        if (!activatedOptions.containsKey(OptionType.AI)) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-mode activated");
          activatedOptions.put(OptionType.AI_MODE, cmd.getOptionValue(ai_mode));
          System.err.println("AI mode not implemented yet");
        }
      } else if (defaultArgs.containsKey("ai-mode")) {
        if (!activatedOptions.containsKey(OptionType.AI)) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-mode activated");
          activatedOptions.put(OptionType.AI_MODE, defaultArgs.get("ai-mode"));
          System.err.println("AI mode not implemented yet");
        }
      }

      if (cmd.hasOption(ai_heuristic)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Choosing the AI heuristic requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-heuristic activated");
          activatedOptions.put(OptionType.AI_HEURISTIC, cmd.getOptionValue(ai_heuristic));
          System.err.println("AI mode not implemented yet");
        }
      } else if (defaultArgs.containsKey("ai-heuristic")) {
        if (!activatedOptions.containsKey(OptionType.AI)) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-heuristic activated");
          activatedOptions.put(OptionType.AI_HEURISTIC, defaultArgs.get("ai-heuristic"));
          System.err.println("AI mode not implemented yet");
        }
      }

      if (cmd.hasOption(ai_depth)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Modifying the AI depth requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-depth activated");
          activatedOptions.put(OptionType.AI_DEPTH, cmd.getOptionValue(ai_depth));
          System.err.println("AI mode not implemented yet");
        }
      } else if (defaultArgs.containsKey("ai-depth")) {
        if (!activatedOptions.containsKey(OptionType.AI)) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-mode activated");
          activatedOptions.put(OptionType.AI_DEPTH, defaultArgs.get("ai-depth"));
          System.err.println("AI mode not implemented yet");
        }
      }

      if (cmd.hasOption(ai_time)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Modifying the AI time requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-time activated");
          activatedOptions.put(OptionType.AI_TIME, cmd.getOptionValue(ai_time));
          System.err.println("AI mode not implemented yet");
        }
      } else if (defaultArgs.containsKey("ai-time")) {
        if (!activatedOptions.containsKey(OptionType.AI)) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-mode activated");
          activatedOptions.put(OptionType.AI_TIME, defaultArgs.get("ai-time"));
          System.err.println("AI mode not implemented yet");
        }
      }

    } catch (ParseException exp) {
      System.out.println("Parsing failed.  Reason: " + exp.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("chess", options);
      runtime.exit(1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return activatedOptions;
  }
}
