package pdp;

import static pdp.utils.Logging.DEBUG;
import static pdp.utils.Logging.VERBOSE;

import java.util.logging.Logger;
import pdp.utils.CLIOptions;
import pdp.utils.Logging;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static String returnsA() {
    return "A";
  }

  public static void main(String[] args) {
    CLIOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);
    DEBUG(LOGGER, "debug test");
    VERBOSE(LOGGER, "verbose test");
    System.out.println("Hello world!");
  }
}
