package pdp.utils;

import org.apache.commons.cli.*;

public class CLIOptions {

  public static void parseOptions(String[] args, Runtime runtime) {
    final Options options = new Options();
    options.addOption("h", "help", false, "Print this message and exit");
    options.addOption("V", "version", false, "Print the version information and exit");
    options.addOption("v", "verbose", false, "Display more information");
    options.addOption("d", "debug", false, "Print debugging information");
    options.addOption("b", "blitz", false, "Play in blitz mode");
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
      }
      if (cmd.hasOption("V")) {
        // TODO: write version somewhere and update it regularly
        System.out.println("Version: 0.0.1");
        runtime.exit(0);
      }
      if (cmd.hasOption("d")) {
        System.out.println("Debug not implemented yet");
      }
      if (cmd.hasOption("v")) {
        System.out.println("Verbose not implemented yet");
      }
      if (cmd.hasOption("b")) {
        System.out.println("Blitz not implemented yet");
      }
      if (cmd.hasOption("t")) {
        System.out.println("Blitz time not implemented yet");
      }
      if (cmd.hasOption("c")) {
        System.out.println("Contest not implemented yet");
      }
      if (cmd.hasOption("a")) {
        System.out.println("AI not implemented yet");
      }
      if (cmd.hasOption("ai-mode")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Modifying the AI algorithm requires 'a' argument");
        }
        System.out.println("AI mode not implemented yet");
      }
      if (cmd.hasOption("ai-heuristic")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Choosing the AI heuristic requires 'a' argument");
        }
        System.out.println("AI mode not implemented yet");
      }
      if (cmd.hasOption("ai-depth")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Modifying the AI depth requires 'a' argument");
        }
        System.out.println("AI mode not implemented yet");
      }
      if (cmd.hasOption("ai-time")) {
        if (!cmd.hasOption("a")) {
          System.err.println("Modifying the AI time requires 'a' argument");
        }
        System.out.println("AI mode not implemented yet");
      }

    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("chess", options);
      System.exit(1);
    }
  }
}
