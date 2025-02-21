package pdp;

import java.util.HashMap;
import java.util.logging.Logger;
import pdp.controller.GameController;
import pdp.utils.CLIOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    HashMap<OptionType, String> options = CLIOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);
    System.out.println(TextGetter.getText("title"));
    System.out.println("options: " + options.toString());

    if (options.containsKey(OptionType.CONTEST)) {
      throw new UnsupportedOperationException("Contest mode not implemented");
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
