package pdp.utils;

import java.io.IOException;
import java.util.Properties;
import org.apache.commons.cli.*;

public class CLIOptions {

  /**
   * Parse the options given in parameters. The help will have the priority over any other option.
   * If the usage is wrong, an error message will be displayed on the standard error output.
   *
   * @param args Array of strings received by main, arguments to parse
   * @param runtime Runtime to exit cleanly
   */
  public static void parseOptions(String[] args, Runtime runtime) {
    final Options options = new Options();
    options.addOption("h", "help", false, "Print this message and exit");
    options.addOption("V", "version", false, "Print the version information and exit");
    options.addOption("v", "verbose", false, "Display more information");
    options.addOption("d", "debug", false, "Print debugging information");
    options.addOption("b", "blitz", false, "Play in blitz mode");
    options.addOption("g", "gui", false, "Displays the game with a  graphical interface.");
    options.addOption(
        Option.builder("t")
            .longOpt("time")
            .hasArg(true)
            .argName("TIME")
            .desc("Specify time per round for blitz mode (default 30min)")
            .type(Integer.class)
            .build());
    options.addOption(
        Option.builder("c")
            .longOpt("contest")
            .hasArg(true)
            .argName("FILENAME")
            .desc("AI plays one move in the given file")
            .build());
    options.addOption(
        Option.builder("a")
            .longOpt("ai")
            .optionalArg(true)
            .argName("COLOR")
            .desc(
                "Launch the program in AI mode, with artificial player with COLOR ’B’ or ’A’ (All),(W by default).")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("ai-mode")
            .hasArg(true)
            .argName("ALGORITHM")
            .desc("Choose the exploration algorithm for the artificial player.")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("ai-depth")
            .hasArg(true)
            .argName("DEPTH")
            .desc("Specify the depth of the AI algorithm")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("ai-heuristic")
            .hasArg(true)
            .argName("HEURISTIC")
            .desc("Choose the heuristic for the artificial player")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("ai-time")
            .argName("TIME")
            .hasArg(true)
            .desc("Specify the time of reflexion for AI mode (default 5 seconds)")
            .build());

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("h")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("chess", options);
        runtime.exit(0);
        return;
      }
      if (cmd.hasOption("V")) {
        final Properties properties = new Properties();
        properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
        System.out.println("Version: " + properties.getProperty("version"));
        runtime.exit(0);
        return;
      }
      if (cmd.hasOption("d")) {
        System.err.println("Debug not implemented yet");
      }
      if (cmd.hasOption("v")) {
        System.err.println("Verbose not implemented yet");
      }
      if (cmd.hasOption("b")) {
        System.err.println("Blitz not implemented yet");
      }
      if (cmd.hasOption("g")) {
        System.err.println("GUI not implemented yet");
      }
      if (cmd.hasOption("t")) {
        System.err.println("Blitz time not implemented yet");
      }
      if (cmd.hasOption("c")) {
        System.err.println("Contest not implemented yet");
      }
      if (cmd.hasOption("a")) {
        System.err.println("AI not implemented yet");
      }
      if (cmd.hasOption("ai-mode")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        }
        System.err.println("AI mode not implemented yet");
      }
      if (cmd.hasOption("ai-heuristic")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Choosing the AI heuristic requires 'a' argument");
        }
        System.err.println("AI mode not implemented yet");
      }
      if (cmd.hasOption("ai-depth")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Modifying the AI depth requires 'a' argument");
        }
        System.err.println("AI mode not implemented yet");
      }
      if (cmd.hasOption("ai-time")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Modifying the AI time requires 'a' argument");
        }
        System.err.println("AI mode not implemented yet");
      }

    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("chess", options);
      System.exit(1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
