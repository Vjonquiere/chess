package pdp;

import java.util.HashMap;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.view.CLIView;
import pdp.view.GameView;
import pdp.view.View;

public abstract class GameControllerInit {
  // TODO Internationalization
  /**
   * Initialize the game with the given options.
   *
   * @param options The options to use to initialize the game.
   * @return A new GameController instance.
   */
  public static GameController initialize(HashMap<OptionType, String> options) {

    Game model = GameInitializer.initialize(options);

    View view;
    if (options.containsKey(OptionType.GUI)) {
      view = new GameView();
    } else {
      view = new CLIView();
    }
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    return new GameController(model, view, bagOfCommands);
  }
}
