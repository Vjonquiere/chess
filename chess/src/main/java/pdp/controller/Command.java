package pdp.controller;

import java.util.Optional;
import pdp.model.GameInterface;

/** Interface needed for the design pattern Command. */
public interface Command {

  /**
   * Executes the command in the model, returns an exception if one is encountered.
   *
   * @param model model to execute the command on
   * @param controller controller that manages the model.
   * @return An exception if one occurs
   */
  Optional<Exception> execute(GameInterface model, GameController controller);
}
