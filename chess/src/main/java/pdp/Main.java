package pdp;

import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.utils.CLIOptions;
import pdp.utils.Logging;
import pdp.view.CLIView;
import pdp.view.View;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    CLIOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);

    Game model = Game.initialize(false, false, null);
    View view = new CLIView();
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    GameController controller = new GameController(model, view, bagOfCommands);
    Thread viewThread = view.start();

    try {
      viewThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
