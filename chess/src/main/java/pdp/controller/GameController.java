package pdp.controller;

import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;
import pdp.view.View;

/** Controller of our MVC architecture. */
public class GameController {
  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
  private Game model;
  private View view;
  private BagOfCommands bagOfCommands;

  /**
   * Initializes the private fields corresponding to the model, the view and the bag of commands.
   *
   * @param model Game, our model for MVC
   * @param view View, our view for MVC
   * @param bagOfCommands Singleton for Command Design pattern
   */
  public GameController(Game model, View view, BagOfCommands bagOfCommands) {
    Logging.configureLogging(LOGGER);
    this.setView(view);
    this.setBagOfCommands(bagOfCommands);
    this.getBagOfCommands().setController(this);
    this.getBagOfCommands().setModel(model);
    this.getModel().addObserver(view);
    this.getModel().addErrorObserver(view);
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
    this.getView().onErrorEvent(e);
  }

  public void setModel(Game model) {
    this.model = model;
  }

  public void setView(View view) {
    this.view = view;
  }

  public BagOfCommands getBagOfCommands() {
    return bagOfCommands;
  }

  public void setBagOfCommands(BagOfCommands bagOfCommands) {
    this.bagOfCommands = bagOfCommands;
  }
}
