package pdp.utils;

import java.util.logging.*;

/**
 * Logging class abstracting java.util.logging Usage : in each class, create a logger from
 * java.utils.logging, then configure the logger in the constructor or main function. Then you can
 * call the DEBUG/VERBOSE function. example for Main: private static final Logger LOGGER =
 * Logger.getLogger(Main.class.getName()); in constructor or main :
 * Logging.configureLogging(LOGGER); Logging.DEBUG(LOGGER, message);
 */
public class Logging {
  private static boolean debug = false;
  private static boolean verbose = false;

  /*Private constructor to avoid instantiation*/
  private Logging() {}
  ;

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
    logger.fine(logger.getName() + " [DEBUG] " + message);
  }

  /**
   * Creates the messages for the verbose mode with the following format ClassName [VERBOSE] message
   *
   * @param logger Logger of the class calling the function
   * @param message String to log
   */
  public static void VERBOSE(Logger logger, String message) {
    logger.finer(logger.getName() + " [VERBOSE] " + message);
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
    logger.setUseParentHandlers(false);
  }
}
