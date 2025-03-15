package pdp;

import java.util.HashMap;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.view.*;

/** Utility class for initializing a {@link GameController} instance. */
public abstract class GameControllerInit {
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
      view = new GUIView();
    } else if (options.containsKey(OptionType.AI_TRAINING)) {
      view = new AIView();
    } else if (options.containsKey(OptionType.CONTEST)) {
      view = new ContestMode();
    } else {
      view = new CLIView();
    }
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    return new GameController(model, view, bagOfCommands);
  }
}
