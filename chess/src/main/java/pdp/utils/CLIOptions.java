package pdp.utils;

import static pdp.utils.Logging.DEBUG;

import java.io.IOException;
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
  public static void parseOptions(String[] args, Runtime runtime) {
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
                "Launch the program in AI mode, with artificial player with COLOR ’B’ or ’A’ (All),(W by default).")
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

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption(debug)) {
        Logging.setDebug(true);
        Logging.configureLogging(LOGGER);
        DEBUG(LOGGER, "Debug mode activated");
      }
      if (cmd.hasOption(verbose)) {
        Logging.setVerbose(true);
        Logging.configureLogging(LOGGER);
        DEBUG(LOGGER, "Verbose mode activated");
      }
      if (cmd.hasOption(help)) {
        DEBUG(LOGGER, "Help option activated");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("chess", options);
        runtime.exit(0);
        return;
      }
      if (cmd.hasOption(version)) {
        DEBUG(LOGGER, "Version option activated");
        final Properties properties = new Properties();
        properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
        System.out.println("Version: " + properties.getProperty("version"));
        runtime.exit(0);
        return;
      }
      if (cmd.hasOption(lang)) {
        DEBUG(LOGGER, "Language option activated");
        if (cmd.getParsedOptionValue(lang).equals("en")) {
          TextGetter.setLocale("en");
          DEBUG(LOGGER, "Language = English (already set by default)");
          // TODO: de-comment when french file finished
        } /*else if (cmd.getParsedOptionValue(lang).equals("fr")) {
            DEBUG(LOGGER, "Language = French");
            TextGetter.setLocale(cmd.getParsedOptionValue("lang"));
          } */ else {
          TextGetter.setLocale("en");
          System.err.println(
              "Language " + cmd.getParsedOptionValue(lang) + " not supported, language = english");
        }
      } else {
        TextGetter.setLocale("en");
      }
      if (cmd.hasOption(blitz)) {
        DEBUG(LOGGER, "Blitz mode activated");
        System.err.println("Blitz not implemented yet");
      }
      if (cmd.hasOption(gui)) {
        DEBUG(LOGGER, "GUI mode activated");
        System.err.println("GUI not implemented yet");
      }
      if (cmd.hasOption(time)) {
        DEBUG(LOGGER, "Blitz time option activated");
        System.err.println("Blitz time not implemented yet");
      }
      if (cmd.hasOption(contest)) {
        DEBUG(LOGGER, "Contest mode activated");
        System.err.println("Contest not implemented yet");
      }
      if (cmd.hasOption(ai)) {
        DEBUG(LOGGER, "AI activated");
        System.err.println("AI not implemented yet");
      }
      if (cmd.hasOption(ai_mode)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-mode activated");
          System.err.println("AI mode not implemented yet");
        }
      }
      if (cmd.hasOption(ai_heuristic)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Choosing the AI heuristic requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-heuristic activated");
          System.err.println("AI mode not implemented yet");
        }
      }
      if (cmd.hasOption(ai_depth)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Modifying the AI depth requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-depth activated");
          System.err.println("AI mode not implemented yet");
        }
      }
      if (cmd.hasOption(ai_time)) {
        if (!cmd.hasOption(ai)) {
          System.err.println("Modifying the AI time requires 'a' argument");
        } else {
          DEBUG(LOGGER, "AI-time activated");
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
  }
}
