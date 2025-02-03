package pdp.controller;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;

public class BagOfCommands {
  private static final Logger LOGGER = Logger.getLogger(BagOfCommands.class.getName());
  BagOfCommands instance;
  ConcurrentLinkedQueue<Command> commands;
  Game model;
  GameController controller;
  boolean isRunning;

  private BagOfCommands() {
    Logging.configureLogging(LOGGER);
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  private void processCommands() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public void executeFirst() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public void executeAll() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public void addCommand(Command command) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public void setModel(Game model) {
    this.model = model;
  }

  public void setController(GameController controller) {
    this.controller = controller;
  }

  public static BagOfCommands getInstance() {
    // TODO
    throw new UnsupportedOperationException("Method not implemented");
  }
}
