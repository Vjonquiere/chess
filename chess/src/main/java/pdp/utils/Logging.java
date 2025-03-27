package pdp.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging class abstracting java.util.logging Usage : in each class, create a logger from
 * java.utils.logging, then configure the logger in the constructor or main function. Then you can
 * call the DEBUG/VERBOSE function. example for Main: private static final Logger LOGGER =
 * Logger.getLogger(Main.class.getName()); in constructor or main :
 * Logging.configureLogging(LOGGER); Logging.DEBUG(LOGGER, message);
 */
public final class Logging {
  /** Boolean to indicate whether debug is enabled. */
  private static boolean debugOn;

  /** Boolean to indicate whether verbose is enabled. */
  private static boolean verboseOn;

  /*
   private static final Logger GLOBAL_LOGGER = Logger.getLogger("GlobalLogger");

   static {
     configureGlobalLogger();
   }
  */
  /*Private constructor to avoid instantiation.*/
  private Logging() {}

  /**
   * Sets the debug field.
   *
   * @param debug true to enable debug, false otherwise
   */
  public static void setDebug(final boolean debug) {
    Logging.debugOn = debug;
  }

  /**
   * Sets the verbose field.
   *
   * @param verbose true to enable verbose, false otherwise
   */
  public static void setVerbose(final boolean verbose) {
    Logging.verboseOn = verbose;
  }

  /**
   * Creates the messages for the debug mode with the following format ClassName [DEBUG] message.
   *
   * @param logger Logger of the class calling the function
   * @param message String to log
   */
  public static void debug(final Logger logger, final String message) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(logger.getName() + " [DEBUG] " + message);
    }
  }

  /**
   * Creates the messages for the verbose mode with the following format ClassName [VERBOSE]
   * message.
   *
   * @param logger Logger of the class calling the function
   * @param message String to log
   */
  public static void verbose(final Logger logger, final String message) {
    if (logger.isLoggable(Level.FINER)) {
      logger.finer(logger.getName() + " [VERBOSE] " + message);
    }
  }

  /**
   * Configure the given logger to display information of the correct level in the console.
   * Configures the class loggers.
   *
   * @param logger Logger of the class calling the function
   */
  public static void configureLogging(final Logger logger) {
    Level level = Level.WARNING;
    if (debugOn) {
      level = Level.FINE;
    }
    if (verboseOn) {
      level = Level.FINER;
    }
    logger.setLevel(level);

    // Display logs in console
    final ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(level);
    consoleHandler.setFormatter(new CustomFormatter());

    for (final Handler handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }
    logger.addHandler(consoleHandler);
    logger.setUseParentHandlers(false);
  }

  /*
    /** Configures the global logger for print and error messages.
    private static void configureGlobalLogger() {
      GLOBAL_LOGGER.setLevel(Level.INFO);
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(Level.INFO);
      consoleHandler.setFormatter(new SimpleFormatter() {
        @Override
        public String format(LogRecord record) {
          return record.getMessage() + System.lineSeparator();
        }
      });

      for (Handler handler : GLOBAL_LOGGER.getHandlers()) {
        GLOBAL_LOGGER.removeHandler(handler);
      }
      GLOBAL_LOGGER.addHandler(consoleHandler);
      GLOBAL_LOGGER.setUseParentHandlers(false);
    }
  */
  /** Logs a normal message (replaces System.out in the rest of the code). */
  public static void print(final String message) {
    System.out.println(message);
  }

  /** Logs an error message (replaces System.err in the rest of the code). */
  public static void error(final String message) {
    System.err.println(message);
  }
}
