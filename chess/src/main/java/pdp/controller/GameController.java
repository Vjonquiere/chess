package pdp.controller;

import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;
import pdp.view.View;

public class GameController {
  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
  Game model;
  View view;
  BagOfCommands bagOfCommands;

  public GameController(Game model, View view, BagOfCommands bagOfCommands) {
    Logging.configureLogging(LOGGER);
    this.model = model;
    this.view = view;
    this.bagOfCommands = bagOfCommands;
  }

  // TODO
}
