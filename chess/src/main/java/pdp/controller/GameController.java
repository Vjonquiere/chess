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
    this.bagOfCommands.setModel(model);
    this.bagOfCommands.setController(this);
    this.model.addObserver(view);
    this.model.addErrorObserver(view);
    // this.model.startAI();
  }

  /**
   * Gets the View object of the controller.
   *
   * @return The View object of the controller.
   */
  public View getView() {
    return this.view;
  }

  /**
   * Gets the Game model of the controller.
   *
   * @return The Game model of the controller.
   */
  public Game getModel() {
    return this.model;
  }

  /**
   * Handles an exception by passing it to the view for display.
   *
   * @param e The exception to handle.
   */
  public void onErrorEvent(Exception e) {
    this.view.onErrorEvent(e);
  }
}
