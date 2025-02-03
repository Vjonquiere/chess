package pdp.utils;

import java.util.logging.*;

/** Logging class abstracting java.util.logging */
public class Logging {
  private static boolean debug = false;
  private static boolean verbose = false;

  public static void setDebug(boolean debug) {
    Logging.debug = debug;
  }

  public static void setVerbose(boolean verbose) {
    Logging.verbose = verbose;
  }

  /**
   * Creates the messages for the debug mode with the following format ClassName [DEBUG] message
   *
   * @param logger Logger of the class calling the function
   * @param message String to log
   */
  public static void DEBUG(Logger logger, String message) {
    logger.fine(logger.getName() + " [DEBUG] \n" + message);
  }

  /**
   * Creates the messages for the verbose mode with the following format ClassName [VERBOSE] message
   *
   * @param logger Logger of the class calling the function
   * @param message String to log
   */
  public static void VERBOSE(Logger logger, String message) {
    logger.finer(logger.getName() + " [VERBOSE] \n" + message);
  }

  /**
   * Configure the given logger to display information of the correct level in the console.
   *
   * @param logger Logger of the class calling the function
   */
  public static void configureLogging(Logger logger) {
    Level level = Level.WARNING;
    if (debug) {
      level = Level.FINE;
    }
    if (verbose) {
      level = Level.FINER;
    }
    logger.setLevel(level);

    // Display logs in console
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(level);
    consoleHandler.setFormatter(new CustomFormatter());

    for (Handler handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }
    logger.addHandler(consoleHandler);
  }
}
