package pdp.controller;

import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;
import pdp.view.View;

/** Controller of our MVC architecture. */
public class GameController {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

  /** Model, for MVC architecture. */
  private Game model;

  /** View, for MVC architecture. */
  private View view;

  /**
   * Initializes the private fields corresponding to the model, the view and the bag of commands.
   *
   * @param model Game, our model for MVC
   * @param view View, our view for MVC
   * @param bagOfCommands Singleton for Command Design pattern
   */
  public GameController(final Game model, final View view, final BagOfCommands bagOfCommands) {
    Logging.configureLogging(LOGGER);
    this.view = view;
    bagOfCommands.setController(this);
    bagOfCommands.setModel(model);
    model.addObserver(view);
    model.addErrorObserver(view);
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
   * @param exception The exception to handle.
   */
  public void onErrorEvent(final Exception exception) {
    this.getView().onErrorEvent(exception);
  }

  /**
   * Assigns the field model to the value given in parameter.
   *
   * @param model Current model
   */
  public void setModel(final Game model) {
    this.model = model;
  }

  /**
   * Assigns the field view to the value given in parameter.
   *
   * @param view Current view
   */
  public void setView(final View view) {
    this.view = view;
  }
}
