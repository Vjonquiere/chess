package pdp;

import static pdp.utils.Logging.print;

import java.util.HashMap;
import java.util.logging.Logger;
import pdp.controller.GameController;
import pdp.utils.CommandLineOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;

/** Base of the application. */
public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Main method of the application. Checks the options given in command line and initializes the
   * model. Launches the view depending on the options given.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    HashMap<OptionType, String> options =
        CommandLineOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);
    if (!options.containsKey(OptionType.UCI)) {
      print(TextGetter.getText("title"));
      print("options: " + options.toString());
    }

    if (options.containsKey(OptionType.CONTEST)) {
      // throw new UnsupportedOperationException("Contest mode not implemented");
    }

    GameController controller = GameControllerInit.initialize(options);
    Thread viewThread = controller.getView().start();

    try {
      viewThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
