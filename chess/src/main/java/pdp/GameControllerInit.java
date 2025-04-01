package pdp;

import java.util.HashMap;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.view.CliView;
import pdp.view.ContestView;
import pdp.view.GuiView;
import pdp.view.UciView;
import pdp.view.View;

/** Utility class for initializing a {@link GameController} instance. */
public abstract class GameControllerInit {
  /**
   * Initialize the game with the given options.
   *
   * @param options The options to use to initialize the game.
   * @return A new GameController instance.
   */
  public static GameController initialize(final HashMap<OptionType, String> options) {

    final Game model = GameInitializer.initialize(options);

    final View view;
    if (options.containsKey(OptionType.GUI)) {
      view = new GuiView();
    } else if (options.containsKey(OptionType.UCI)) {
      view = new UciView();
    } else if (options.containsKey(OptionType.CONTEST)) {
      view = new ContestView(options.get(OptionType.CONTEST));
    } else {
      view = new CliView();
    }
    final BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    return new GameController(model, view, bagOfCommands);
  }
}
