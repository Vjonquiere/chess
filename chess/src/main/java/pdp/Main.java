package pdp;

import static pdp.utils.Logging.print;

import java.util.Map;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pdp.controller.GameController;
import pdp.utils.CommandLineOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;

/** Base of the application. */
@SpringBootApplication
public class Main {

  /** Private constructor to avoid instanciating a utility class. */
  public Main() {}

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Main method of the application. Checks the options given in command line and initializes the
   * model. Launches the view depending on the options given.
   *
   * @param args Command line arguments.
   */
  public static void main(final String[] args) {
    SpringApplication.run(Main.class, args);
    final Map<OptionType, String> options =
        CommandLineOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);
    if (!options.containsKey(OptionType.UCI)) {
      print(TextGetter.getText("title"));
      print("options: " + options);
    }

    final GameController controller = GameControllerInit.initialize(options);
    final Thread viewThread = controller.getView().start();

    try {
      viewThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
