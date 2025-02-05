package pdp;

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
  }
}
